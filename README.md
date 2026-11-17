# Rule Engine Example (Drools & KIE DMN with Spring Boot)

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-21%20%7C%2025-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)]()
[![Drools](https://img.shields.io/badge/Drools-10.2.0-blue)]()
[![KIE DMN](https://img.shields.io/badge/KIE%20DMN-10.2.0-blue)]()

A clean, production-ready Spring Boot reference project demonstrating how to design, configure, and evaluate business rules using:
* **Drools Rule Engine (DRL)**: Pattern matching, working memory reactivity, fact chaining, salience, activation groups, and agenda groups.
* **KIE Sessions**: Deep dive into `StatelessKieSession` vs stateful `KieSession`.
* **KIE DMN (Decision Model and Notation)**: Visual, declarative decision tables with FEEL expressions.
* **Dynamic DMN Management**: REST APIs allowing business analysts to register and evaluate new DMN models exported from spreadsheets at runtime.
* **KIE Session & Agenda Group Orchestration**: Targeted evaluation of DMN models via Drools `agenda-group`, invoking service functions via `global`, and applying Drools overrides on top of DMN decisions.

---

## 📖 Table of Contents

- [Core Concepts & Architecture](#-core-concepts--architecture)
  - [1. Drools Rule Engine (DRL)](#1-drools-rule-engine-drl)
  - [2. KIE Session: Stateless vs Stateful](#2-kie-session-stateless-vs-stateful)
  - [3. DMN (Decision Model and Notation)](#3-dmn-decision-model-and-notation)
  - [4. Dynamic DMN Registration (For Business Analysts)](#4-dynamic-dmn-registration-for-business-analysts)
  - [5. KieSession Agenda Group & Service Invocation Orchestration](#5-kiesession-agenda-group--service-invocation-orchestration)
- [Project Architecture](#-project-architecture)
- [Quick Start](#-quick-start)
- [API Tests Folder (`/api-tests`)](#-api-tests-folder-api-tests)
- [API Walkthrough & cURL Examples](#-api-walkthrough--curl-examples)
  - [1. Drools Stateless Rule: Order Loyalty Discount](#1-drools-stateless-rule-order-loyalty-discount)
  - [2. Drools Stateful Rule: Shopping Cart & Free Gift Inference](#2-drools-stateful-rule-shopping-cart--free-gift-inference)
  - [3. DMN Standard Evaluation: Loan Eligibility & Insurance](#3-dmn-standard-evaluation-loan-eligibility--insurance)
  - [4. Dynamic DMN Registration & Direct Evaluation](#4-dynamic-dmn-registration--direct-evaluation)
  - [5. KieSession Agenda Group Targeting & DMN Overrides](#5-kiesession-agenda-group-targeting--dmn-overrides)
  - [6. Live API Catalog](#6-live-api-catalog)
- [Running Automated Tests](#-running-automated-tests)

---

## 🧠 Core Concepts & Architecture

### 1. Drools Rule Engine (DRL)
Drools uses an enhanced Rete/Phreak pattern matching algorithm:
- **LHS (`when`)**: Pattern matching against facts in Working Memory.
- **RHS (`then`)**: Actions triggered when conditions are satisfied.
- **Salience**: Determines execution priority.
- **Activation-Group**: Only the highest-priority rule in the group fires; others are cancelled.
- **Agenda-Group**: Partitions rules into named phases. A group only executes when targeted via `setFocus()`.
- **Working Memory & Reactivity**: Rules can insert new facts (`insert(...)`), modify existing ones (`update(...)`), or query the working memory (`query ...`).

### 2. KIE Session: Stateless vs Stateful

| Dimension | `StatelessKieSession` | `KieSession` (Stateful) |
|---|---|---|
| **Memory Lifetime** | Single execution (`execute(...)`). Working memory is discarded immediately. | Long-lived. Memory persists across multiple fact insertions and rule firings. |
| **Concurrency** | Thread-safe for sequential calls. Lightweight. | Not thread-safe across concurrent threads. Bound to a conversation/session. |
| **Resource Management** | Automatic garbage collection. | **Must be explicitly disposed** (`kieSession.dispose()`) to prevent memory leaks. |
| **Use Cases** | Stateless calculations (discounts, validation, scoring, pricing). | Cart management, transaction monitoring, multi-step workflows, rule chaining. |

### 3. DMN (Decision Model and Notation)
- **Business Analyst Friendly**: Standardized XML format editable in visual diagram tools (VS Code DMN editor, Camunda Modeler, Kogito).
- **FEEL Expressions**: Friendly Enough Expression Language for clean predicate logic (`>= 18`, `[500..800]`).
- **Hit Policies**: Defines how table rows are evaluated (`FIRST`, `UNIQUE`, `COLLECT`).

### 4. Dynamic DMN Registration (For Business Analysts)
Allows business users to export decision tables from spreadsheets or DMN editors and register them on the running application via REST (`POST /api/dmn/dynamic`). The application compiles the XML into an isolated KIE container and exposes it for immediate evaluation without server restarts.

### 5. KieSession Agenda Group & Service Invocation Orchestration
Shows how to combine Drools and DMN in a hybrid pipeline:
1. Client requests an evaluation targeting a specific `agenda-group` (e.g. `"loan-approval-orchestration"`).
2. The service creates a `KieSession`, injects `DynamicDmnService` as a Drools `global`, and sets focus on the requested agenda group (`setFocus()`).
3. Drools rules in that agenda group invoke the DMN evaluation service, insert the decision as a `DmnDecisionFact` into Working Memory, and subsequent rules inspect or override the result (e.g., granting a -1.0% interest rate concession if credit score >= 800).

---

## 📁 Project Architecture

```
rule-engine-example/
├── api-tests/                               # Runnable .http files for testing
│   ├── 00-home-catalog.http                 # API Catalog & Actuator
│   ├── 01-drools-stateless.http             # Stateless discount rules
│   ├── 02-drools-stateful.http              # Stateful cart & free gifts
│   ├── 03-dmn-standard.http                 # Standard pre-compiled DMN
│   ├── 04-dynamic-dmn.http                  # Dynamic DMN registration & evaluate
│   ├── 05-kie-agenda-dmn.http               # KieSession targeting DMN via agenda group
│   └── samples/
│       └── retail-tier-discount.dmn         # Sample DMN export ready for upload
├── src/
│   ├── main/
│   │   ├── java/com/example/ruleengine/
│   │   │   ├── config/                      # DroolsConfig & TrackingAgendaEventListener
│   │   │   ├── controller/                  # REST Controllers
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── DroolsStatelessController.java
│   │   │   │   ├── DroolsStatefulController.java
│   │   │   │   ├── DmnController.java
│   │   │   │   ├── DynamicDmnController.java
│   │   │   │   └── AgendaDmnOrchestrationController.java
│   │   │   ├── model/                       # Domain models & bridge facts
│   │   │   └── service/                     # Business services
│   │   │       ├── StatelessDroolsService.java
│   │   │       ├── StatefulDroolsService.java
│   │   │       ├── DmnEvaluationService.java
│   │   │       ├── DynamicDmnService.java
│   │   │       └── AgendaDmnOrchestrationService.java
│   │   └── resources/
│   │       └── rules/
│   │           ├── drools/                  # DRL rules (including agenda-dmn-orchestration.drl)
│   │           └── dmn/                     # Built-in DMN models
│   └── test/                                # 23 Automated JUnit 5 & MockMvc tests
├── build.gradle
└── gradlew
```

---

## 🚀 Quick Start

### Prerequisites
* **JDK 21 or 25**

```bash
# Build and run tests
./gradlew test

# Start the application
./gradlew bootRun
```

App runs on `http://localhost:8080`.

---

## 🧪 API Tests Folder (`/api-tests`)

You can run every endpoint directly inside **VS Code** (using REST Client) or **IntelliJ IDEA**:
* Open any file in [**`api-tests/`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests) and click **"Send Request"**.

---

## 📡 API Walkthrough & cURL Examples

### 1. Drools Stateless Rule: Order Loyalty Discount
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

---

### 2. Drools Stateful Rule: Shopping Cart & Free Gift Inference
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

---

### 3. Dynamic DMN Registration (Exported from Spreadsheet)
Register a newly exported decision table on the fly:
```bash
curl -X POST http://localhost:8080/api/dmn/dynamic \
  -H "Content-Type: application/json" \
  -d '{
    "modelId": "retail-tier-discount",
    "description": "Exported from business spreadsheet",
    "dmnXml": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<definitions xmlns=\"https://www.omg.org/spec/DMN/20211108/MODEL/\" id=\"_defs\" name=\"RetailTierDiscount\" namespace=\"https://example.com/dmn/retail-tier-discount\"><itemDefinition id=\"_t1\" name=\"tCustomerTier\"><typeRef>string</typeRef></itemDefinition><itemDefinition id=\"_t2\" name=\"tCartAmount\"><typeRef>number</typeRef></itemDefinition><itemDefinition id=\"_tOut\" name=\"tDec\"><itemComponent id=\"_c1\" name=\"discountPercent\"><typeRef>number</typeRef></itemComponent><itemComponent id=\"_c2\" name=\"rewardPoints\"><typeRef>number</typeRef></itemComponent></itemDefinition><inputData id=\"_i1\" name=\"customerTier\"><variable id=\"_vi1\" name=\"customerTier\" typeRef=\"tCustomerTier\"/></inputData><inputData id=\"_i2\" name=\"cartAmount\"><variable id=\"_vi2\" name=\"cartAmount\" typeRef=\"tCartAmount\"/></inputData><decision id=\"_dec\" name=\"Retail Decision\"><variable id=\"_vd\" name=\"Retail Decision\" typeRef=\"tDec\"/><informationRequirement id=\"_ir1\"><requiredInput href=\"#_i1\"/></informationRequirement><informationRequirement id=\"_ir2\"><requiredInput href=\"#_i2\"/></informationRequirement><decisionTable id=\"_dt\" hitPolicy=\"FIRST\" typeRef=\"tDec\"><input id=\"_in1\"><inputExpression typeRef=\"string\"><text>customerTier</text></inputExpression></input><input id=\"_in2\"><inputExpression typeRef=\"number\"><text>cartAmount</text></inputExpression></input><output id=\"_o1\" name=\"discountPercent\" typeRef=\"number\"/><output id=\"_o2\" name=\"rewardPoints\" typeRef=\"number\"/><rule id=\"_r1\"><inputEntry><text>\"VIP\"</text></inputEntry><inputEntry><text>&gt;= 200</text></inputEntry><outputEntry><text>20.0</text></outputEntry><outputEntry><text>500</text></outputEntry></rule><rule id=\"_r2\"><inputEntry><text>-</text></inputEntry><inputEntry><text>-</text></inputEntry><outputEntry><text>5.0</text></outputEntry><outputEntry><text>50</text></outputEntry></rule></decisionTable></decision></definitions>"
  }'
```

Directly evaluate the registered model:
```bash
curl -X POST http://localhost:8080/api/dmn/dynamic/retail-tier-discount/evaluate \
  -H "Content-Type: application/json" \
  -d '{ "customerTier": "VIP", "cartAmount": 300.0 }'
```

---

### 4. KieSession Agenda Group Targeting & DMN Overrides
Target the `"loan-approval-orchestration"` agenda group. Drools evaluates the `"loan-eligibility"` DMN table, detects `creditScore >= 800`, and fires an override reducing the interest rate by 1.0%:

```bash
curl -X POST http://localhost:8080/api/orchestration/agenda-dmn/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "agendaGroup": "loan-approval-orchestration",
    "targetModelId": "loan-eligibility",
    "inputs": {
      "age": 36,
      "monthlyIncome": 120000.0,
      "creditScore": 820,
      "requestedAmount": 300000.0
    }
  }'
```

**Response:**
```json
{
  "engineType": "HYBRID_AGENDA_DMN",
  "sessionOrModel": "AgendaGroup: loan-approval-orchestration -> Model: loan-eligibility",
  "executionDurationMs": 2,
  "rulesFired": [
    "Execute Loan Eligibility DMN in Agenda",
    "High Credit Score Prime Rate Override"
  ],
  "result": {
    "agendaGroup": "loan-approval-orchestration",
    "targetModelId": "loan-eligibility",
    "executed": true,
    "overrideApplied": true,
    "decisionFact": {
      "modelId": "loan-eligibility",
      "decisionName": "Loan Eligibility Decision",
      "decisionValues": {
        "approved": true,
        "maxApprovedAmount": 500000.0,
        "riskGrade": "LOW_RISK",
        "interestRate": 5.5,
        "vipOverrideApplied": true
      },
      "auditTags": [
        "DMN Model evaluated: LoanEligibility (https://example.com/dmn/loan-eligibility)",
        "Drools VIP override applied: -1.0% interest rate concession"
      ]
    },
    "executionTrace": [
      "Step 1: Evaluated loan DMN model 'loan-eligibility'",
      "Step 2 (Drools Override): Credit score >= 800 qualifies for -1.0% interest rate VIP concession"
    ]
  }
}
```

---

### 5. Live API Catalog
```bash
curl http://localhost:8080/api
```

---

## 🧪 Running Automated Tests

```bash
./gradlew test
```
All **23 unit and integration tests** execute with 100% pass rate.

---

## 📄 License
MIT License.
