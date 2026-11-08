package com.example.ruleengine;

import com.example.ruleengine.config.DroolsConfig;
import com.example.ruleengine.model.CartItem;
import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.model.ShoppingCart;
import com.example.ruleengine.service.StatefulDroolsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;

import static org.junit.jupiter.api.Assertions.*;

class DroolsStatefulTest {

    private StatefulDroolsService statefulService;

    @BeforeEach
    void setUp() {
        DroolsConfig config = new DroolsConfig();
        KieContainer kieContainer = config.kieContainer();
        statefulService = new StatefulDroolsService(kieContainer);
    }

    @Test
    @DisplayName("Stateful session executes bulk category discounts and dynamically inserts FreeGift facts")
    void testStatefulCartEvaluationWithGift() {
        ShoppingCart cart = new ShoppingCart("CART-100", "CUST-A");
        // 3 Mechanical Keyboards @ $100 each -> Eligible for bulk electronics discount ($20 * 3 = $60 discount)
        cart.addItem(new CartItem("PROD-KEYBOARD", "Mechanical Keyboard", "ELECTRONICS", 100.0, 3));
        // Subtotal = $300 - $60 = $240. Since finalTotal >= $200, qualifies for Free Earbuds!

        ExecutionSummary<ShoppingCart> summary = statefulService.evaluateShoppingCart(cart);

        assertNotNull(summary);
        assertEquals("DROOLS_STATEFUL", summary.getEngineType());
        assertTrue(summary.getRulesFired().contains("Bulk Electronics Discount"));
        assertTrue(summary.getRulesFired().contains("Calculate Cart Totals"));
        assertTrue(summary.getRulesFired().contains("Award Premium Free Gift"));

        // Verify Cart Totals
        assertEquals(300.0, cart.getOriginalTotal());
        assertEquals(60.0, cart.getDiscountTotal());
        assertEquals(240.0, cart.getFinalTotal());

        // Verify Queried Free Gift from Working Memory
        assertEquals(1, cart.getFreeGifts().size());
        assertEquals("GIFT-PREMIUM-HEADPHONES", cart.getFreeGifts().get(0).getGiftId());
        assertEquals("Wireless Bluetooth Earbuds", cart.getFreeGifts().get(0).getGiftName());
    }

    @Test
    @DisplayName("Stateful session qualifies for super cart powerbank when total exceeds $500")
    void testSuperCartFreeGifts() {
        ShoppingCart cart = new ShoppingCart("CART-200", "CUST-B");
        cart.addItem(new CartItem("PROD-MONITOR", "4K Ultra-wide Monitor", "ELECTRONICS", 600.0, 1));

        ExecutionSummary<ShoppingCart> summary = statefulService.evaluateShoppingCart(cart);

        assertEquals(600.0, cart.getFinalTotal());
        assertTrue(summary.getRulesFired().contains("Award Premium Free Gift"));
        assertTrue(summary.getRulesFired().contains("Award Super Cart Powerbank"));

        // Both free gifts should be inserted into working memory and queried
        assertEquals(2, cart.getFreeGifts().size());
    }

    @Test
    @DisplayName("Cart with books qualifies for Book Lover 10% bundle discount")
    void testBookBundleDiscount() {
        ShoppingCart cart = new ShoppingCart("CART-300", "CUST-C");
        // 5 novels @ $20 each = $100 -> 10% discount = $10 discount
        cart.addItem(new CartItem("PROD-BOOK", "Clean Code Book", "BOOKS", 20.0, 5));

        ExecutionSummary<ShoppingCart> summary = statefulService.evaluateShoppingCart(cart);

        assertTrue(summary.getRulesFired().contains("Book Lover Bundle Discount"));
        assertEquals(100.0, cart.getOriginalTotal());
        assertEquals(10.0, cart.getDiscountTotal());
        assertEquals(90.0, cart.getFinalTotal());
        // $90 does not reach the $200 threshold, so no free gift
        assertTrue(cart.getFreeGifts().isEmpty());
    }
}

