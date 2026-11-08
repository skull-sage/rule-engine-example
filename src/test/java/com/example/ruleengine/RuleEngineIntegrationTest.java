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
}
