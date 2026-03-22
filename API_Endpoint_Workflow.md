# RegReportX - Complete API Endpoint Workflow

This document outlines the entire end-to-end operational flow of the RegReportX application. The endpoints are ordered sequentially to demonstrate the lifecycle of data from initial entry and ingestion to final regulatory report filing.

---

## 1. Authentication & Authorization
**Participants:** Everyone
*The entry point for all users to authenticate and receive their JWT token for subsequent API calls.*
- **POST** `/api/auth/login`
  - Authenticates the user credentials and returns a secure JWT token.

---

## 2. Source Data Mocking (External System Simulation)
**Participants:** REGTECH_ADMIN, OPERATIONS_OFFICER
*These endpoints simulate the legacy/Core Banking system where source data resides before entering RegReportX.*
- **GET** `/api/loans` - Retrieve mock loan data.
- **GET** `/api/deposits` - Retrieve mock deposit data.
- **GET** `/api/treasury` - Retrieve mock treasury trade data.
- **GET** `/api/gl` - Retrieve mock general ledger data.

---

## 3. Data Ingestion (Initial Load)
**Participants:** OPERATIONS_OFFICER, REGTECH_ADMIN
*The process of safely pulling data from the external source systems into the RegReportX staging area.*
- **POST** `/api/ingestion/run`
  - Triggers the extraction of source data to generate a new `RawDataBatch`.
- **GET** `/api/ingestion/batches`
  - Retrieve the history of all ingestion batches.

---

## 4. Raw Record Generation
**Participants:** OPERATIONS_OFFICER, REGTECH_ADMIN
*Transforms the ingested data batches into individual schema-less JSON raw records for processing.*
- **POST** `/api/raw-records/load/{batchId}`
  - Parses an ingestion batch and loads the individual entries natively into the `RawRecord` tables.
- **GET** `/api/raw-records/batch/{batchId}`
  - View the loaded raw records corresponding to a specific batch.

---

## 5. Data Quality & Validation
**Participants:** COMPLIANCE_ANALYST, REGTECH_ADMIN
*Evaluates the raw records against dynamic business rules to ensure structural integrity and sanity.*
- **GET** `/api/validation/run`
  - Triggers the rule engine to scan raw records and flag `DataQualityIssue`s.
- **GET** `/api/validation/issues`
  - Retrieves all historical validation issues.
- **GET** `/api/data-quality/issues`
  - Retrieves only the *open/unresolved* data quality issues.
- **PUT** `/api/data-quality/issues/{id}/resolve`
  - Accepts a corrected value, dynamically patches the JSON `RawRecord`, and generates a `CorrectionLog` for the audit trail.

---

## 6. Regulatory Reporting Lifecycle
**Participants:** REPORTING_OFFICER (Typically manages state transitions), REGTECH_ADMIN
*Once data is clean, compiled Regulatory Reports are generated and transitioned through their approval lifecycle.*
- **POST** `/api/reports/generate`
  - Aggregates the fully cleaned raw records and populates a new mapped Draft `RegulatoryReport`.
- **GET** `/api/reports`
  - Retrieves all generated reports.
- **GET** `/api/reports/{id}`
  - Views the detailed metrics and fields of a specific report.
- **PUT** `/api/reports/{id}/submit`
  - Transitions a report from `DRAFT` to `SUBMITTED`.
- **PUT** `/api/reports/{id}/approve`
  - Transitions a report from `SUBMITTED` to `APPROVED`.
- **PUT** `/api/reports/{id}/file`
  - Transitions a report from `APPROVED` to `FILED` (Final state).

---

## 7. Risk Engine Calculation
**Participants:** RISK_ANALYST, REGTECH_ADMIN
*Derives Basel/Regulatory risk metrics from the aggregated reports.*
- **POST** `/api/risk/calculate/{reportId}`
  - Computes critical safety metrics (Total Loans, Deposits, CD Ratio, Net GL Balance) and persists them in the `RiskMetric` table.
- **GET** `/api/risk/metrics`
  - Retrieves all computed risk metrics history.

---

## 8. Risk Exception Mitigation
**Participants:** COMPLIANCE_ANALYST, REGTECH_ADMIN
*Identifies catastrophic metric breaches and oversees mitigation strategies.*
- **POST** `/api/exceptions/generate/{reportId}`
  - Evaluates computed Risk Metrics against severe threshold rules (e.g., LCR dropping below 100%) and logs `ExceptionRecord`s.
- **GET** `/api/exceptions/open`
  - Retrieves all unresolved/critical risk exceptions.
- **PUT** `/api/exceptions/{id}/resolve`
  - Accepts a formal mitigation/adjusted valuation amount, resolves the exception, back-propagates the safety valuation into the original `RiskMetric`, and logs the correction.

---

## 9. Template & User Administration
**Participants:** REGTECH_ADMIN
*Administrative panel logic.*
- **GET / POST / PUT / DELETE** `/api/templates`
  - Fully manage dynamic regulatory templates and their corresponding field definitions.
- **GET / POST / PUT / DELETE** `/api/admin/users`
  - Fully manage system actors, roles, status, and credentials.

---

## 10. Audit Auditing
**Participants:** Authenticated Users (Everyone)
*The immutable track record bridging all the above workflows.*
- **GET** `/api/audit`
  - Read-only pipeline returning universally tracked system modifications (e.g., entity changes, record patching, status transitions) handled by the `AuditAspect`.
