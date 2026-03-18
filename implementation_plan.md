# Goal Description
The objective is to replace the hardcoded risk calculations inside [RiskCalculationService](file:///c:/Users/janan/Downloads/RegReportX/src/main/java/com/cts/regreportx/service/RiskCalculationService.java#22-221) and make it dynamically driven by the rules stored in the [TemplateField](file:///c:/Users/janan/Downloads/RegReportX/src/main/java/com/cts/regreportx/model/TemplateField.java#10-85) entity. 

## Proposed Changes

### Components
#### [NEW] [DynamicRiskEvaluator.java](file:///c:/Users/janan/Downloads/RegReportX/src/main/java/com/cts/regreportx/component/DynamicRiskEvaluator.java)
- A new component that handles parsing string math formulas (e.g., `"(Total_Loans / Total_Deposits) * 100"`) into actual Java arithmetic values (`BigDecimal`).
- It will evaluate both aggregation descriptors (like `SUM(amount) FROM Deposit`) and derived formula configurations.

### Services
#### [MODIFY] [RiskCalculationService.java](file:///c:/Users/janan/Downloads/RegReportX/src/main/java/com/cts/regreportx/service/RiskCalculationService.java)
- **Delete** the hardcoded metric aggregations and thresholds currently executing top-to-bottom.
- **Add** a dynamic query loop:
  1. Fetch [TemplateField](file:///c:/Users/janan/Downloads/RegReportX/src/main/java/com/cts/regreportx/model/TemplateField.java#10-85) list by `report.getTemplateId()`.
  2. Compute base aggregates into a `Map<String, BigDecimal> context`.
  3. Loop over the database's [TemplateField](file:///c:/Users/janan/Downloads/RegReportX/src/main/java/com/cts/regreportx/model/TemplateField.java#10-85) components and evaluate their `mappingExpression` using `DynamicRiskEvaluator`.
  4. Save the dynamically computed results into `RiskMetric` objects.

## Verification Plan
### Automated Tests
- Run `mvn clean compile` to catch any compilation or syntax errors.

### Manual Verification
- After successful implementation, generate an RCA3 report which will trigger the dynamic workflow to fetch the newly seeded database templates and test the rules engine.
