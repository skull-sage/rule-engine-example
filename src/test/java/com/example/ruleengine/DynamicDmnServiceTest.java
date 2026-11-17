package com.example.ruleengine;

import com.example.ruleengine.model.DmnDecisionFact;
import com.example.ruleengine.model.DmnRegistrationResponse;
import com.example.ruleengine.service.DynamicDmnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DynamicDmnServiceTest {

    private DynamicDmnService dynamicDmnService;

    private static final String SAMPLE_RETAIL_DMN = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/"
                         id="_retail_discount_definitions"
                         name="RetailTierDiscount"
                         namespace="https://example.com/dmn/retail-tier-discount">

              <itemDefinition id="_it_tier" name="tCustomerTier">
                <typeRef>string</typeRef>
              </itemDefinition>
              <itemDefinition id="_it_amount" name="tCartAmount">
                <typeRef>number</typeRef>
              </itemDefinition>

              <itemDefinition id="_it_out_decision" name="tRetailDiscountDecision">
                <itemComponent id="_it_comp_discount" name="discountPercent">
                  <typeRef>number</typeRef>
                </itemComponent>
                <itemComponent id="_it_comp_points" name="rewardPoints">
                  <typeRef>number</typeRef>
                </itemComponent>
                <itemComponent id="_it_comp_gift" name="freeGiftEligible">
                  <typeRef>boolean</typeRef>
                </itemComponent>
              </itemDefinition>

              <inputData id="_in_tier" name="customerTier">
                <variable id="_v_tier" name="customerTier" typeRef="tCustomerTier"/>
              </inputData>
              <inputData id="_in_amount" name="cartAmount">
                <variable id="_v_amount" name="cartAmount" typeRef="tCartAmount"/>
              </inputData>

              <decision id="_dec_retail_discount" name="Retail Tier Discount Decision">
                <variable id="_v_retail_discount" name="Retail Tier Discount Decision" typeRef="tRetailDiscountDecision"/>
                <informationRequirement id="_ir_t">
                  <requiredInput href="#_in_tier"/>
                </informationRequirement>
                <informationRequirement id="_ir_a">
                  <requiredInput href="#_in_amount"/>
                </informationRequirement>

                <decisionTable id="_dt_retail" hitPolicy="FIRST" typeRef="tRetailDiscountDecision">
                  <input id="_in_1" label="Tier">
                    <inputExpression id="_ie_1" typeRef="string"><text>customerTier</text></inputExpression>
                  </input>
                  <input id="_in_2" label="Cart Amount">
                    <inputExpression id="_ie_2" typeRef="number"><text>cartAmount</text></inputExpression>
                  </input>

                  <output id="_out_d" name="discountPercent" typeRef="number"/>
                  <output id="_out_p" name="rewardPoints" typeRef="number"/>
                  <output id="_out_g" name="freeGiftEligible" typeRef="boolean"/>

                  <!-- Rule 1: VIP over $200 -->
                  <rule id="_r1">
                    <inputEntry id="_r1_1"><text>"VIP"</text></inputEntry>
                    <inputEntry id="_r1_2"><text>&gt;= 200</text></inputEntry>
                    <outputEntry id="_r1_o1"><text>20.0</text></outputEntry>
                    <outputEntry id="_r1_o2"><text>500</text></outputEntry>
                    <outputEntry id="_r1_o3"><text>true</text></outputEntry>
                  </rule>

                  <!-- Rule 2: Gold over $100 -->
                  <rule id="_r2">
                    <inputEntry id="_r2_1"><text>"GOLD"</text></inputEntry>
                    <inputEntry id="_r2_2"><text>&gt;= 100</text></inputEntry>
                    <outputEntry id="_r2_o1"><text>15.0</text></outputEntry>
                    <outputEntry id="_r2_o2"><text>250</text></outputEntry>
                    <outputEntry id="_r2_o3"><text>false</text></outputEntry>
                  </rule>

                  <!-- Rule 3: Fallback -->
                  <rule id="_r3">
                    <inputEntry id="_r3_1"><text>-</text></inputEntry>
                    <inputEntry id="_r3_2"><text>-</text></inputEntry>
                    <outputEntry id="_r3_o1"><text>5.0</text></outputEntry>
                    <outputEntry id="_r3_o2"><text>50</text></outputEntry>
                    <outputEntry id="_r3_o3"><text>false</text></outputEntry>
                  </rule>
                </decisionTable>
              </decision>
            </definitions>
            """;

    @BeforeEach
    void setUp() {
        dynamicDmnService = new DynamicDmnService();
        dynamicDmnService.initDefaultModels();
    }

    @Test
    @DisplayName("Dynamically register new DMN XML exported from spreadsheet and evaluate immediately")
    void testRegisterAndEvaluateDynamicDmn() {
        DmnRegistrationResponse regResponse = dynamicDmnService.registerDmn(
                "retail-tier-discount",
                SAMPLE_RETAIL_DMN,
                "Dynamic test model"
        );

        assertNotNull(regResponse);
        assertEquals("SUCCESS", regResponse.getStatus());
        assertEquals("retail-tier-discount", regResponse.getModelId());
        assertTrue(regResponse.getDecisions().contains("Retail Tier Discount Decision"));

        // Evaluate VIP customer over $200
        Map<String, Object> inputs = Map.of(
                "customerTier", "VIP",
                "cartAmount", 250.0
        );
        Map<String, Object> output = dynamicDmnService.evaluate("retail-tier-discount", inputs);

        assertNotNull(output);
        assertTrue(output.containsKey("Retail Tier Discount Decision"));

        @SuppressWarnings("unchecked")
        Map<String, Object> decisionMap = (Map<String, Object>) output.get("Retail Tier Discount Decision");
        assertEquals(20.0, ((Number) decisionMap.get("discountPercent")).doubleValue());
        assertEquals(500, ((Number) decisionMap.get("rewardPoints")).intValue());
        assertEquals(true, decisionMap.get("freeGiftEligible"));
    }

    @Test
    @DisplayName("Evaluate DMN to DmnDecisionFact for Drools working memory integration")
    void testEvaluateToFact() {
        dynamicDmnService.registerDmn("retail-tier-discount", SAMPLE_RETAIL_DMN, "Test");

        Map<String, Object> inputs = Map.of(
                "customerTier", "GOLD",
                "cartAmount", 120.0
        );
        DmnDecisionFact fact = dynamicDmnService.evaluateToFact("retail-tier-discount", inputs);

        assertNotNull(fact);
        assertFalse(fact.isHasErrors());
        assertEquals("retail-tier-discount", fact.getModelId());
        assertEquals(15.0, ((Number) fact.getValue("discountPercent")).doubleValue());
        assertEquals(250, ((Number) fact.getValue("rewardPoints")).intValue());
    }
}
