# Rule Engine Example (Drools & KIE DMN with Spring Boot)

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-21%20%7C%2025-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)]()
[![Drools](https://img.shields.io/badge/Drools-10.2.0-blue)]()
[![KIE DMN](https://img.shields.io/badge/KIE%20DMN-10.2.0-blue)]()

A clean, production-ready Spring Boot reference project demonstrating how to design, configure, and evaluate business rules using **Drools Rule Engine (DRL)**, **KIE Sessions (Stateless vs Stateful)**, and **KIE DMN (Decision Model and Notation)**.

---

## 📖 Table of Contents

- [Core Concepts & Architecture](#-core-concepts--architecture)
  - [1. Drools Rule Engine (DRL)](#1-drools-rule-engine-drl)
  - [2. KIE Session: Stateless vs Stateful](#2-kie-session-stateless-vs-stateful)
  - [3. DMN (Decision Model and Notation)](#3-dmn-decision-model-and-notation)
  - [4. DRL vs DMN Comparison](#4-drl-vs-dmn-comparison)
- [Project Architecture](#-project-architecture)
- [Quick Start](#-quick-start)
- [API Walkthrough & cURL Examples](#-api-walkthrough--curl-examples)
  - [1. Drools Stateless Rule: Order Loyalty Discount](#1-drools-stateless-rule-order-loyalty-discount)
  - [2. Drools Stateful Rule: Shopping Cart & Free Gift Inference](#2-drools-stateful-rule-shopping-cart--free-gift-inference)
  - [3. DMN Evaluation: Loan Eligibility Decision Table](#3-dmn-evaluation-loan-eligibility-decision-table)
  - [4. DMN Evaluation: Insurance Premium Calculation](#4-dmn-evaluation-insurance-premium-calculation)
  - [5. API Catalog](#5-api-catalog)
- [Running Automated Tests](#-running-automated-tests)

---

## 🧠 Core Concepts & Architecture

### 1. Drools Rule Engine (DRL)
Drools uses an enhanced Rete/Phreak pattern matching algorithm to evaluate facts against declarative rules written in `.drl` files.
- **LHS (`when`)**: Conditional pattern matching against facts in memory.
- **RHS (`then`)**: Actions triggered when conditions are satisfied.
- **Salience**: Determines execution priority (higher numbers execute first).
- **Activation-Group**: Only the highest-priority rule in the group fires; others are cancelled.
- **Working Memory & Reactivity**: Rules can insert new facts (`insert(...)`), modify existing ones (`update(...)`), or query the working memory (`query ...`).

### 2. KIE Session: Stateless vs Stateful

| Dimension | `StatelessKieSession` | `KieSession` (Stateful) |
|---|---|---|
| **Memory Lifetime** | Single execution (`execute(...)`). Working memory is discarded immediately. | Long-lived. Memory persists across multiple fact insertions and rule firings. |
| **Concurrency** | Thread-safe for sequential calls. Lightweight. | Not thread-safe across concurrent threads. Bound to a conversation/session. |
| **Resource Management** | Automatic garbage collection. | **Must be explicitly disposed** (`kieSession.dispose()`) to prevent memory leaks. |
| **Use Cases** | Stateless calculations (discounts, validation, scoring, pricing). | Cart management, transaction monitoring, multi-step workflows, rule chaining. |

### 3. DMN (Decision Model and Notation)
DMN is an **OMG industry standard** for visual, declarative decision tables and graphs:
- **Business Analyst Friendly**: Standardized XML format editable in visual diagram tools (VS Code DMN editor, Camunda Modeler, Kogito).
- **FEEL Expressions**: Friendly Enough Expression Language for clean predicate logic (`>= 18`, `[500..800]`).
- **Hit Policies**: Defines how table rows are evaluated:
  - `FIRST`: Returns the first rule that matches.
  - `UNIQUE`: Exactly one rule must match.
  - `COLLECT`: Accumulates results of all matching rules.
- **Decoupled**: Executed directly by `DMNRuntime` without compiling into Java bytecode.

### 4. DRL vs DMN Comparison

```
+------------------------+---------------------------------------+---------------------------------------+
| Feature                | Drools DRL                            | KIE DMN                               |
+------------------------+---------------------------------------+---------------------------------------+
| Format                 | Proprietary DSL (.drl)                | OMG Standard XML / Decision Tables    |
| Primary Audience       | Software Engineers                    | Business Analysts & Domain Experts    |
| Stateful Reasoning     | Full support (insert, update, delete) | Stateless input -> output mapping     |
| Complex Inference      | Forward chaining, event CEP           | Decision dependency graphs (DRDs)     |
| Performance Overhead   | Java bytecode compilation             | Fast interpreted FEEL evaluation      |
+------------------------+---------------------------------------+---------------------------------------+
```

---

## 📁 Project Architecture

```
rule-engine-example/
├── src/
│   ├── main/
│   │   ├── java/com/example/ruleengine/
│   │   │   ├── RuleEngineApplication.java
│   │   │   ├── config/
│   │   │   │   ├── DroolsConfig.java                # KieContainer & DMNRuntime beans
│   │   │   │   └── TrackingAgendaEventListener.java # Execution provenance & listener
│   │   │   ├── controller/
│   │   │   │   ├── DroolsStatelessController.java   # POST /api/drools/stateless/discount
│   │   │   │   ├── DroolsStatefulController.java    # POST /api/drools/stateful/cart-evaluate
│   │   │   │   ├── DmnController.java               # POST /api/dmn/loan-eligibility & /insurance-quote
│   │   │   │   └── HomeController.java              # GET /api catalog
│   │   │   ├── model/
│   │   │   │   ├── Customer.java, Order.java, Discount.java
│   │   │   │   ├── ShoppingCart.java, CartItem.java, FreeGift.java
│   │   │   │   ├── LoanApplication.java, LoanDecision.java
│   │   │   │   ├── InsuranceQuoteRequest.java, InsuranceQuoteDecision.java
│   │   │   │   └── ExecutionSummary.java            # Standardized envelope with provenance
│   │   │   └── service/
│   │   │       ├── StatelessDroolsService.java      # StatelessKieSession execution
│   │   │       ├── StatefulDroolsService.java       # Stateful KieSession + Queries + Disposal
│   │   │       └── DmnEvaluationService.java        # DMNRuntime evaluation
│   │   └── resources/
│   │       ├── application.properties
│   │       └── rules/
│   │           ├── drools/
│   │           │   ├── discount-rules.drl           # Salience, activation-groups, tiers
│   │           │   └── cart-rules.drl               # Queries, fact insertion, forward chaining
│   │           └── dmn/
│   │               ├── loan-eligibility.dmn         # DMN 1.4 Decision Table (Hit policy: FIRST)
│   │               └── insurance-pricing.dmn        # Multi-input FEEL decision table
│   └── test/
│       └── java/com/example/ruleengine/
│           ├── DroolsStatelessTest.java             # Unit tests for stateless rules
│           ├── DroolsStatefulTest.java              # Unit tests for working memory & queries
│           ├── DmnEvaluationTest.java               # Unit tests for DMN runtime & FEEL
│           └── RuleEngineIntegrationTest.java       # MockMvc / SpringBootTest REST API tests
├── build.gradle
├── settings.gradle
└── gradlew
```

---

## 🚀 Quick Start

### Prerequisites
- **JDK 21 or 25**
- Git

### Build and Run
```bash
# Clone the repository
git clone https://github.com/skull-sage/rule-engine-example.git
cd rule-engine-example

# Run unit and integration tests
./gradlew test

# Start the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

---

## 📡 API Walkthrough & cURL Examples

### 1. Drools Stateless Rule: Order Loyalty Discount
Demonstrates **StatelessKieSession** with rule salience and `activation-group`.

```bash
curl -X POST http://localhost:8080/api/drools/stateless/discount \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-1001",
    "orderAmount": 250.0,
    "customer": {
      "id": "CUST-01",
      "name": "Alice Cooper",
      "loyaltyTier": "PLATINUM",
      "loyaltyPoints": 420
    }
  }'
```

**Response:**
```json
{
  "engineType": "DROOLS_STATELESS",
  "sessionOrModel": "StatelessKieSession",
  "executionDurationMs": 2,
  "rulesFired": [
    "Platinum Tier VIP Discount"
  ],
  "result": {
    "orderId": "ORD-1001",
    "customer": {
      "id": "CUST-01",
      "name": "Alice Cooper",
      "loyaltyTier": "PLATINUM",
      "loyaltyPoints": 420
    },
    "orderAmount": 250.0,
    "discount": {
      "percentage": 25.0,
      "discountAmount": 62.5,
      "loyaltyPointsAwarded": 500,
      "freeShipping": true,
      "description": "Platinum VIP: 25% off + Free Shipping + 2x Points"
    },
    "appliedRules": [
      "Platinum Tier VIP Discount"
    ]
  }
}
```

---

### 2. Drools Stateful Rule: Shopping Cart & Free Gift Inference
Demonstrates **Stateful KieSession**, fact modification (`update($cart)`), rule chaining, querying working memory for dynamically inserted `FreeGift` facts, and session disposal.

```bash
curl -X POST http://localhost:8080/api/drools/stateful/cart-evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": "CART-501",
    "customerId": "CUST-88",
    "items": [
      {
        "productId": "PROD-KB",
        "productName": "RGB Mechanical Keyboard",
        "category": "ELECTRONICS",
        "unitPrice": 100.0,
        "quantity": 3
      }
    ]
  }'
```

**Response:**
```json
{
  "engineType": "DROOLS_STATEFUL",
  "sessionOrModel": "KieSession (Stateful)",
  "executionDurationMs": 3,
  "rulesFired": [
    "Bulk Electronics Discount",
    "Calculate Cart Totals",
    "Award Premium Free Gift"
  ],
  "result": {
    "cartId": "CART-501",
    "customerId": "CUST-88",
    "originalTotal": 300.0,
    "discountTotal": 60.0,
    "finalTotal": 240.0,
    "freeGifts": [
      {
        "giftId": "GIFT-PREMIUM-HEADPHONES",
        "giftName": "Wireless Bluetooth Earbuds",
        "qualifyingReason": "Cart total exceeded $200"
      }
    ],
    "appliedPromotions": [
      "Bulk Electronics Discount (-$60.0 on RGB Mechanical Keyboard)",
      "Bonus Gift: Wireless Bluetooth Earbuds!"
    ]
  }
}
```

---

### 3. DMN Evaluation: Loan Eligibility Decision Table
Demonstrates **KIE DMN 1.4 Runtime**, passing input data into `DMNContext`, and evaluating decision tables.

```bash
curl -X POST http://localhost:8080/api/dmn/loan-eligibility \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "APP-901",
    "applicantName": "John Smith",
    "age": 34,
    "monthlyIncome": 75000.0,
    "creditScore": 780,
    "requestedAmount": 200000.0,
    "employmentMonths": 48
  }'
```

**Response:**
```json
{
  "engineType": "DMN",
  "sessionOrModel": "LoanEligibility (Loan Eligibility Decision)",
  "executionDurationMs": 2,
  "rulesFired": [
    "Decision Table: Loan Eligibility Decision"
  ],
  "result": {
    "approved": true,
    "maxApprovedAmount": 500000.0,
    "riskGrade": "LOW_RISK",
    "interestRate": 6.5,
    "reasons": [
      "Prime Tier: Approved with lowest interest rate"
    ]
  }
}
```

---

### 4. DMN Evaluation: Insurance Premium Calculation
Evaluates driver profile against multiple FEEL rules to calculate risk tier and premium.

```bash
curl -X POST http://localhost:8080/api/dmn/insurance-quote \
  -H "Content-Type: application/json" \
  -d '{
    "driverName": "Sarah Connor",
    "driverAge": 32,
    "accidentCount": 0,
    "vehicleType": "SEDAN"
  }'
```

**Response:**
```json
{
  "engineType": "DMN",
  "sessionOrModel": "InsurancePricing (Insurance Quote Decision)",
  "executionDurationMs": 1,
  "rulesFired": [
    "Decision Table: Insurance Quote Decision"
  ],
  "result": {
    "eligible": true,
    "basePremium": 600.0,
    "riskMultiplier": 1.0,
    "finalPremium": 600.0,
    "riskCategory": "LOW_RISK",
    "explanation": "Experienced driver with spotless record - preferred rate."
  }
}
```

---

### 5. API Catalog
Inspect all active rule engine endpoints:
```bash
curl http://localhost:8080/api
```

---

## 🧪 Running Automated Tests

Run all unit, stateful reasoning, DMN decision table, and Spring Boot integration tests:

```bash
./gradlew test
```

### Test Suite Highlights:
- **`DroolsStatelessTest`**: Verifies discount calculation, loyalty tiers, activation groups, and listener tracking.
- **`DroolsStatefulTest`**: Verifies fact insertion (`FreeGift`), cart totals calculation, working memory queries (`GetFreeGifts`), and memory disposal.
- **`DmnEvaluationTest`**: Verifies DMN 1.4 decision tables, FEEL ranges, condition matching, and edge case rejections (underage, poor credit score, excessive accidents).
- **`RuleEngineIntegrationTest`**: Verifies end-to-end HTTP JSON serialization and REST controller responses.

---

## 📄 License
MIT License.
