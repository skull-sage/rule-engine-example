package com.example.ruleengine.service;

import com.example.ruleengine.model.DmnDecisionFact;
import com.example.ruleengine.model.DmnRegistrationResponse;
import jakarta.annotation.PostConstruct;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.*;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class DynamicDmnService {

    private static final Logger log = LoggerFactory.getLogger(DynamicDmnService.class);

    public record CompiledDmnEntry(
            String modelId,
            String namespace,
            String modelName,
            List<String> decisions,
            List<String> inputs,
            String dmnXml,
            DMNRuntime runtime,
            DMNModel model,
            long compileDurationMs
    ) {}

    private final Map<String, CompiledDmnEntry> registry = new ConcurrentHashMap<>();

    @PostConstruct
    public void initDefaultModels() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:rules/dmn/*.dmn");
            for (Resource res : resources) {
                String filename = res.getFilename();
                if (filename != null) {
                    String modelId = filename.replace(".dmn", "");
                    try (InputStream is = res.getInputStream()) {
                        String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        registerDmn(modelId, xml, "Classpath bootstrapped DMN: " + filename);
                        log.info("Bootstrapped DMN model '{}' into Dynamic DMN Registry", modelId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to bootstrap default DMN resources", e);
        }
    }

    public synchronized DmnRegistrationResponse registerDmn(String modelId, String dmnXml, String description) {
        long startTime = System.nanoTime();
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("modelId must not be empty");
        }
        if (dmnXml == null || dmnXml.isBlank()) {
            throw new IllegalArgumentException("dmnXml must not be empty");
        }

        KieServices kieServices = KieServices.Factory.get();
        String safeModelId = modelId.trim().toLowerCase().replaceAll("[^a-z0-9_-]", "-");
        ReleaseId releaseId = kieServices.newReleaseId("com.example.dynamic", safeModelId, "v" + System.currentTimeMillis());

        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId);
        kfs.write("src/main/resources/rules/dmn/" + safeModelId + ".dmn", dmnXml.getBytes(StandardCharsets.UTF_8));

        KieBuilder builder = kieServices.newKieBuilder(kfs).buildAll();
        List<Message> errors = builder.getResults().getMessages(Message.Level.ERROR);
        if (!errors.isEmpty()) {
            String errorMsg = "DMN Compilation failed for modelId '" + safeModelId + "': " + errors;
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        KieContainer container = kieServices.newKieContainer(releaseId);
        DMNRuntime runtime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);

        if (runtime.getModels().isEmpty()) {
            throw new IllegalArgumentException("No valid DMN models found in provided DMN XML");
        }

        DMNModel model = runtime.getModels().get(0);
        List<String> decisionNames = model.getDecisions().stream().map(DecisionNode::getName).toList();
        List<String> inputNames = model.getInputs().stream().map(InputDataNode::getName).toList();
        long compileDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        CompiledDmnEntry entry = new CompiledDmnEntry(
                safeModelId,
                model.getNamespace(),
                model.getName(),
                decisionNames,
                inputNames,
                dmnXml,
                runtime,
                model,
                compileDurationMs
        );

        registry.put(safeModelId, entry);
        log.info("Registered dynamic DMN model '{}' (decisions: {}, inputs: {}) in {}ms",
                safeModelId, decisionNames, inputNames, compileDurationMs);

        return new DmnRegistrationResponse(
                safeModelId,
                model.getNamespace(),
                model.getName(),
                decisionNames,
                inputNames,
                compileDurationMs,
                "SUCCESS",
                "DMN successfully compiled and registered"
        );
    }

    public List<DmnRegistrationResponse> getRegisteredModels() {
        List<DmnRegistrationResponse> result = new ArrayList<>();
        for (CompiledDmnEntry entry : registry.values()) {
            result.add(new DmnRegistrationResponse(
                    entry.modelId(),
                    entry.namespace(),
                    entry.modelName(),
                    entry.decisions(),
                    entry.inputs(),
                    entry.compileDurationMs(),
                    "ACTIVE",
                    "Ready for evaluation"
            ));
        }
        return result;
    }

    public DmnRegistrationResponse getModelDetails(String modelId) {
        CompiledDmnEntry entry = registry.get(modelId.trim().toLowerCase());
        if (entry == null) {
            throw new NoSuchElementException("DMN model not registered: " + modelId);
        }
        return new DmnRegistrationResponse(
                entry.modelId(),
                entry.namespace(),
                entry.modelName(),
                entry.decisions(),
                entry.inputs(),
                entry.compileDurationMs(),
                "ACTIVE",
                "Ready for evaluation"
        );
    }

    public String getRawDmnXml(String modelId) {
        CompiledDmnEntry entry = registry.get(modelId.trim().toLowerCase());
        if (entry == null) {
            throw new NoSuchElementException("DMN model not registered: " + modelId);
        }
        return entry.dmnXml();
    }

    public Map<String, Object> evaluate(String modelId, Map<String, Object> inputs) {
        CompiledDmnEntry entry = registry.get(modelId.trim().toLowerCase());
        if (entry == null) {
            throw new NoSuchElementException("DMN model not registered: " + modelId);
        }

        DMNContext context = entry.runtime().newContext();
        if (inputs != null) {
            inputs.forEach(context::set);
        }

        DMNResult dmnResult = entry.runtime().evaluateAll(entry.model(), context);
        if (dmnResult.hasErrors()) {
            throw new IllegalStateException("DMN evaluation errors for " + modelId + ": " + dmnResult.getMessages());
        }

        Map<String, Object> decisionsOutput = new LinkedHashMap<>();
        for (DMNDecisionResult decision : dmnResult.getDecisionResults()) {
            decisionsOutput.put(decision.getDecisionName(), decision.getResult());
        }
        return decisionsOutput;
    }

    /**
     * Evaluates a DMN model and bridges the result into a {@link DmnDecisionFact}
     * for seamless consumption inside Drools Working Memory rules.
     */
    public DmnDecisionFact evaluateToFact(String modelId, Map<String, Object> inputs) {
        CompiledDmnEntry entry = registry.get(modelId.trim().toLowerCase());
        if (entry == null) {
            DmnDecisionFact errorFact = new DmnDecisionFact();
            errorFact.setModelId(modelId);
            errorFact.addErrorMessage("DMN model not found: " + modelId);
            return errorFact;
        }

        try {
            DMNContext context = entry.runtime().newContext();
            if (inputs != null) {
                inputs.forEach(context::set);
            }

            DMNResult dmnResult = entry.runtime().evaluateAll(entry.model(), context);
            DmnDecisionFact fact = new DmnDecisionFact();
            fact.setModelId(entry.modelId());

            if (dmnResult.hasErrors()) {
                fact.setHasErrors(true);
                for (DMNMessage msg : dmnResult.getMessages()) {
                    fact.addErrorMessage(msg.getMessage());
                }
                return fact;
            }

            Map<String, Object> values = new LinkedHashMap<>();
            for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
                fact.setDecisionName(dr.getDecisionName());
                values.put(dr.getDecisionName(), dr.getResult());
                if (dr.getResult() instanceof Map<?, ?> map) {
                    map.forEach((k, v) -> values.put(String.valueOf(k), v));
                }
            }
            fact.setDecisionValues(values);
            fact.addAuditTag("DMN Model evaluated: " + entry.modelName() + " (" + entry.namespace() + ")");
            return fact;
        } catch (Exception e) {
            log.error("Error evaluating DMN model '{}' into fact", modelId, e);
            DmnDecisionFact errorFact = new DmnDecisionFact();
            errorFact.setModelId(modelId);
            errorFact.addErrorMessage("Evaluation exception: " + e.getMessage());
            return errorFact;
        }
    }
}
