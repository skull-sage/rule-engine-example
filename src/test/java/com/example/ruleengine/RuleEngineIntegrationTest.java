package com.example.ruleengine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class RuleEngineIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Root /api endpoint returns rule engine catalog and available endpoints")
    void testApiCatalogEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api", Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertTrue(response.getBody().containsKey("drools_engine_examples"));
        assertTrue(response.getBody().containsKey("dmn_engine_examples"));
        assertTrue(response.getBody().containsKey("dynamic_dmn_management"));
        assertTrue(response.getBody().containsKey("agenda_group_dmn_orchestration"));
    }

    @Test
    @DisplayName("Stateless Drools REST endpoint evaluates discount correctly")
    void testStatelessRestEndpoint() {
        String payload = """
                {
                    "orderId": "ORD-REST-1",
                    "orderAmount": 250.0,
                    "customer": {
                        "id": "C-1",
                        "name": "Jane",
                        "loyaltyTier": "PLATINUM",
                        "loyaltyPoints": 100
                    }
                }
                """;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/drools/stateless/discount", entity, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DROOLS_STATELESS", response.getBody().get("engineType"));

        Map result = (Map) response.getBody().get("result");
        assertNotNull(result);
        Map discount = (Map) result.get("discount");
        assertEquals(25.0, ((Number) discount.get("percentage")).doubleValue());
    }

    @Test
    @DisplayName("Stateful Drools REST endpoint evaluates cart and inserts free gift")
    void testStatefulRestEndpoint() {
        String payload = """
                {
                    "cartId": "CART-REST-1",
                    "customerId": "CUST-REST",
                    "items": [
                        {
                            "productId": "P1",
                            "productName": "Gaming Mouse",
                            "category": "ELECTRONICS",
                            "unitPrice": 100.0,
                            "quantity": 3
                        }
                    ]
                }
                """;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/drools/stateful/cart-evaluate", entity, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DROOLS_STATEFUL", response.getBody().get("engineType"));

        Map result = (Map) response.getBody().get("result");
        assertEquals(240.0, ((Number) result.get("finalTotal")).doubleValue());
        java.util.List freeGifts = (java.util.List) result.get("freeGifts");
        assertEquals(1, freeGifts.size());
    }

    @Test
    @DisplayName("DMN Loan Eligibility REST endpoint evaluates application correctly")
    void testDmnLoanRestEndpoint() {
        String payload = """
                {
                    "applicantId": "APP-REST-1",
                    "applicantName": "Alice Smith",
                    "age": 30,
                    "monthlyIncome": 80000.0,
                    "creditScore": 760,
                    "requestedAmount": 150000.0,
                    "employmentMonths": 36
                }
                """;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/dmn/loan-eligibility", entity, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DMN", response.getBody().get("engineType"));

        Map result = (Map) response.getBody().get("result");
        assertEquals(true, result.get("approved"));
        assertEquals("LOW_RISK", result.get("riskGrade"));
        assertEquals(6.5, ((Number) result.get("interestRate")).doubleValue());
    }

    @Test
    @DisplayName("Dynamic DMN REST endpoint allows registering new model and evaluating it")
    void testDynamicDmnRestEndpoints() {
        String dynamicXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/"
                             id="_promo_test_defs"
                             name="PromoDiscount"
                             namespace="https://example.com/dmn/promo">
                  <itemDefinition id="_it_age" name="tAge"><typeRef>number</typeRef></itemDefinition>
                  <itemDefinition id="_it_rate" name="tDiscount"><typeRef>number</typeRef></itemDefinition>
                  <inputData id="_in_age" name="age"><variable id="_v_age" name="age" typeRef="tAge"/></inputData>
                  <decision id="_dec_promo" name="Promo Decision">
                    <variable id="_v_dec" name="Promo Decision" typeRef="tDiscount"/>
                    <informationRequirement id="_ir1"><requiredInput href="#_in_age"/></informationRequirement>
                    <decisionTable id="_dt1" hitPolicy="FIRST" typeRef="tDiscount">
                      <input id="_dt_in"><inputExpression typeRef="number"><text>age</text></inputExpression></input>
                      <output id="_dt_out" name="discount" typeRef="number"/>
                      <rule id="_r1"><inputEntry><text>&gt;= 60</text></inputEntry><outputEntry><text>25.0</text></outputEntry></rule>
                      <rule id="_r2"><inputEntry><text>-</text></inputEntry><outputEntry><text>10.0</text></outputEntry></rule>
                    </decisionTable>
                  </decision>
                </definitions>
                """;

        String registerPayload = """
                {
                    "modelId": "senior-promo",
                    "dmnXml": %s,
                    "description": "Senior promo decision table"
                }
                """.formatted(quote(dynamicXml));

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> registerEntity = new org.springframework.http.HttpEntity<>(registerPayload, headers);

        // 1. Register DMN
        ResponseEntity<Map> regResponse = restTemplate.postForEntity("/api/dmn/dynamic", registerEntity, Map.class);
        assertEquals(HttpStatus.CREATED, regResponse.getStatusCode());
        assertEquals("senior-promo", regResponse.getBody().get("modelId"));
        assertEquals("SUCCESS", regResponse.getBody().get("status"));

        // 2. Direct Evaluate
        String evalPayload = "{\"age\": 65}";
        org.springframework.http.HttpEntity<String> evalEntity = new org.springframework.http.HttpEntity<>(evalPayload, headers);
        ResponseEntity<Map> evalResponse = restTemplate.postForEntity("/api/dmn/dynamic/senior-promo/evaluate", evalEntity, Map.class);
        assertEquals(HttpStatus.OK, evalResponse.getStatusCode());
        assertNotNull(evalResponse.getBody());
        assertEquals(25.0, ((Number) evalResponse.getBody().get("Promo Decision")).doubleValue());
    }

    @Test
    @DisplayName("KieSession targets agenda group and dynamically evaluates registered DMN")
    void testAgendaGroupDmnOrchestrationRestEndpoint() {
        String orchestrationPayload = """
                {
                    "agendaGroup": "loan-approval-orchestration",
                    "targetModelId": "loan-eligibility",
                    "inputs": {
                        "age": 35,
                        "monthlyIncome": 85000.0,
                        "creditScore": 810,
                        "requestedAmount": 150000.0
                    }
                }
                """;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(orchestrationPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/orchestration/agenda-dmn/evaluate", entity, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("HYBRID_AGENDA_DMN", response.getBody().get("engineType"));

        Map result = (Map) response.getBody().get("result");
        assertEquals(true, result.get("executed"));
        assertEquals(true, result.get("overrideApplied"));

        Map decisionFact = (Map) result.get("decisionFact");
        assertNotNull(decisionFact);
        Map decisionValues = (Map) decisionFact.get("decisionValues");
        assertEquals(5.5, ((Number) decisionValues.get("interestRate")).doubleValue());
        assertEquals(true, decisionValues.get("vipOverrideApplied"));
    }

    private String quote(String raw) {
        return "\"" + raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "") + "\"";
    }
}
