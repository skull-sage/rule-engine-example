package com.example.ruleengine;

import com.example.ruleengine.config.DroolsConfig;
import com.example.ruleengine.model.Customer;
import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.model.Order;
import com.example.ruleengine.service.StatelessDroolsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;

import static org.junit.jupiter.api.Assertions.*;

class DroolsStatelessTest {

    private StatelessDroolsService statelessService;

    @BeforeEach
    void setUp() {
        DroolsConfig config = new DroolsConfig();
        KieContainer kieContainer = config.kieContainer();
        statelessService = new StatelessDroolsService(kieContainer);
    }

    @Test
    @DisplayName("Platinum VIP customer with order over $100 receives 25% discount and Free Shipping")
    void testPlatinumVipDiscount() {
        Customer platinumCustomer = new Customer("CUST-001", "Alice", "PLATINUM", 500);
        Order order = new Order("ORD-101", platinumCustomer, 200.0);

        ExecutionSummary<Order> summary = statelessService.evaluateOrderDiscount(order);

        assertNotNull(summary);
        assertEquals("DROOLS_STATELESS", summary.getEngineType());
        assertTrue(summary.getRulesFired().contains("Platinum Tier VIP Discount"));

        assertEquals(25.0, order.getDiscount().getPercentage());
        assertEquals(50.0, order.getDiscount().getDiscountAmount());
        assertTrue(order.getDiscount().isFreeShipping());
        assertEquals(400, order.getDiscount().getLoyaltyPointsAwarded()); // 2x points
    }

    @Test
    @DisplayName("Gold customer receives 15% discount and Free Shipping on orders >= $100")
    void testGoldDiscount() {
        Customer goldCustomer = new Customer("CUST-002", "Bob", "GOLD", 200);
        Order order = new Order("ORD-102", goldCustomer, 150.0);

        ExecutionSummary<Order> summary = statelessService.evaluateOrderDiscount(order);

        assertTrue(summary.getRulesFired().contains("Gold Tier Discount"));
        assertEquals(15.0, order.getDiscount().getPercentage());
        assertEquals(22.5, order.getDiscount().getDiscountAmount());
        assertTrue(order.getDiscount().isFreeShipping());
    }

    @Test
    @DisplayName("Silver customer receives 10% discount on orders >= $50 without free shipping")
    void testSilverDiscount() {
        Customer silverCustomer = new Customer("CUST-003", "Charlie", "SILVER", 50);
        Order order = new Order("ORD-103", silverCustomer, 80.0);

        ExecutionSummary<Order> summary = statelessService.evaluateOrderDiscount(order);

        assertTrue(summary.getRulesFired().contains("Silver Tier Discount"));
        assertEquals(10.0, order.getDiscount().getPercentage());
        assertEquals(8.0, order.getDiscount().getDiscountAmount());
        assertFalse(order.getDiscount().isFreeShipping());
    }

    @Test
    @DisplayName("Regular customer with order under threshold gets fallback standard points")
    void testRegularCustomerFallback() {
        Customer regularCustomer = new Customer("CUST-004", "Diana", "REGULAR", 10);
        Order order = new Order("ORD-104", regularCustomer, 40.0);

        ExecutionSummary<Order> summary = statelessService.evaluateOrderDiscount(order);

        assertTrue(summary.getRulesFired().contains("Default Standard Points"));
        assertEquals(0.0, order.getDiscount().getPercentage());
        assertEquals(0.0, order.getDiscount().getDiscountAmount());
        assertEquals(4, order.getDiscount().getLoyaltyPointsAwarded()); // 0.1x points
    }
}
