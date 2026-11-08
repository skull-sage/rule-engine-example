package com.example.ruleengine.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DroolsConfig {

    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);

    private static final String[] DRL_FILES = {
            "rules/drools/discount-rules.drl",
            "rules/drools/cart-rules.drl"
    };

    private static final String[] DMN_FILES = {
            "rules/dmn/loan-eligibility.dmn",
            "rules/dmn/insurance-pricing.dmn"
    };

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load DRL files
        for (String drl : DRL_FILES) {
            log.info("Loading Drools DRL resource: {}", drl);
            kieFileSystem.write("src/main/resources/" + drl, ResourceFactory.newClassPathResource(drl));
        }

        // Load DMN files
        for (String dmn : DMN_FILES) {
            log.info("Loading KIE DMN resource: {}", dmn);
            kieFileSystem.write("src/main/resources/" + dmn, ResourceFactory.newClassPathResource(dmn));
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        if (!errors.isEmpty()) {
            log.error("Drools/DMN build errors encountered: {}", errors);
            throw new IllegalStateException("Failed to build KIE resources: " + errors);
        }

        KieRepository kieRepository = kieServices.getRepository();
        KieContainer container = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        log.info("KieContainer successfully initialized with DRL and DMN assets.");
        return container;
    }

    @Bean
    public DMNRuntime dmnRuntime(KieContainer kieContainer) {
        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);
        log.info("DMNRuntime initialized with {} DMN models: {}",
                dmnRuntime.getModels().size(),
                dmnRuntime.getModels().stream().map(m -> m.getName() + " (" + m.getNamespace() + ")").toList());
        return dmnRuntime;
    }
}
