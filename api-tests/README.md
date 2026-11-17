# API Tests (`.http` Files)

This folder contains runnable HTTP request files compatible with:
* **VS Code** (using the [REST Client extension](https://marketplace.visualstudio.com/items?itemName=humao.rest-client))
* **IntelliJ IDEA / Aqua / Fleet** (native HTTP Client)
* **cURL** (command templates provided in each file)

---

## 📂 Test Files Index

| File | Controller / Topic | Key Concepts Demonstrated |
|---|---|---|
| [**`00-home-catalog.http`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests/00-home-catalog.http) | `HomeController` | System overview, live API catalog, Actuator health |
| [**`01-drools-stateless.http`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests/01-drools-stateless.http) | `DroolsStatelessController` | `StatelessKieSession`, `salience`, `activation-group`, discount tiers |
| [**`02-drools-stateful.http`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests/02-drools-stateful.http) | `DroolsStatefulController` | `KieSession` (Stateful), `update()` reactivity, dynamic `FreeGift` fact insertion, working memory queries, session disposal |
| [**`03-dmn-standard.http`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests/03-dmn-standard.http) | `DmnController` | Pre-compiled DMN 1.4 Decision Tables (Loan Eligibility, Insurance Pricing) |
| [**`04-dynamic-dmn.http`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests/04-dynamic-dmn.http) | `DynamicDmnController` | **Dynamic DMN Registration**: Registering DMN models exported from spreadsheets at runtime via JSON or file upload, and evaluating immediately |
| [**`05-kie-agenda-dmn.http`**](file:///Volumes/work-space/brac-v3/rule-engine-example/api-tests/05-kie-agenda-dmn.http) | `AgendaDmnOrchestrationController` | **Agenda Group Targeting & Service Invocations**: KieSession targeting specific agenda groups (`setFocus()`), calling service functions via Drools `global`, bridging DMN decisions into working memory, and executing Drools VIP overrides |

---

## 🚀 How to Run

1. Start the application:
   ```bash
   ./gradlew bootRun
   ```
2. Open any `.http` file in your IDE and click **"Send Request"** above any request block.
3. Or test with cURL:
   ```bash
   curl -X GET http://localhost:8080/api
   ```
