package com.example.ruleengine.controller;

import com.example.ruleengine.model.DmnRegistrationRequest;
import com.example.ruleengine.model.DmnRegistrationResponse;
import com.example.ruleengine.service.DynamicDmnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * REST controller enabling business users to dynamically register, inspect,
 * and evaluate DMN decision tables exported from spreadsheets or visual editors.
 */
@RestController
@RequestMapping("/api/dmn/dynamic")
public class DynamicDmnController {

    private final DynamicDmnService dynamicDmnService;

    public DynamicDmnController(DynamicDmnService dynamicDmnService) {
        this.dynamicDmnService = dynamicDmnService;
    }

    /**
     * Register a new DMN model by submitting DMN XML in JSON.
     */
    @PostMapping
    public ResponseEntity<DmnRegistrationResponse> registerDmnJson(@RequestBody DmnRegistrationRequest request) {
        DmnRegistrationResponse response = dynamicDmnService.registerDmn(
                request.getModelId(),
                request.getDmnXml(),
                request.getDescription()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register a new DMN model by uploading a .dmn / .xml file exported from Excel or a DMN editor.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DmnRegistrationResponse> uploadDmnFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "modelId", required = false) String modelId,
            @RequestParam(value = "description", required = false) String description) throws IOException {

        String safeModelId = (modelId != null && !modelId.isBlank())
                ? modelId
                : Objects.requireNonNullElse(file.getOriginalFilename(), "custom-model")
                .replaceAll("\\.[^.]+$", "");

        String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        DmnRegistrationResponse response = dynamicDmnService.registerDmn(
                safeModelId,
                xmlContent,
                description != null ? description : "Uploaded file: " + file.getOriginalFilename()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * List all currently registered DMN models.
     */
    @GetMapping
    public ResponseEntity<List<DmnRegistrationResponse>> listModels() {
        return ResponseEntity.ok(dynamicDmnService.getRegisteredModels());
    }

    /**
     * Inspect details of a specific registered DMN model.
     */
    @GetMapping("/{modelId}")
    public ResponseEntity<DmnRegistrationResponse> getModelDetails(@PathVariable String modelId) {
        return ResponseEntity.ok(dynamicDmnService.getModelDetails(modelId));
    }

    /**
     * View raw DMN XML for a registered model.
     */
    @GetMapping(value = "/{modelId}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getRawXml(@PathVariable String modelId) {
        return ResponseEntity.ok(dynamicDmnService.getRawDmnXml(modelId));
    }

    /**
     * Direct evaluation of any dynamically registered DMN model using arbitrary JSON input.
     */
    @PostMapping("/{modelId}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateDirect(
            @PathVariable String modelId,
            @RequestBody Map<String, Object> inputs) {
        Map<String, Object> output = dynamicDmnService.evaluate(modelId, inputs);
        return ResponseEntity.ok(output);
    }
}
