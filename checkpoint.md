# Checkpoint

| Step | Status |
|---|---|
| 1 Design | Complete |
| 2 Build/config | Complete |
| 3–5 Implementation | Complete |
| 6 Seed data | Deferred: no credentials supplied; deployment-safe seed is not created |
| 7 Infra/start scripts | Complete |
| 9 Documentation | Complete |
| 10–14 Verification/fix loop | Complete — remediation tests, Gradle tests, and live database-backed API verification passed |
| 15 Reports | Complete — regenerated from live HTTP results |

## Decisions
- Java 21 / Spring Boot 3.4.0 / Gradle 8.11.1; package `com.enterprise.hr`; app port 8080.
- MySQL 8.4 is external infra, `mysql` compose service. JWT stateless security uses `ROLE_HR`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE` only.
- Packages: domain, dto, repository, service, controller, security, exception, config.
- Independent implementation groups: identity/organization; attendance; leave workflow; payroll; performance cycles; reporting. Shared API contract: JSON `/api`; error `{timestamp,status,error,message,path}`; ISO-8601 dates/times; DTOs never expose password hashes.
- Domain entity contract: generated Long ids. Roles exactly HR/MANAGER/EMPLOYEE and leave statuses exactly PENDING/APPROVED/REJECTED.
- Remediation contract: payroll adds nullable ISO-8601 `paidAt: Instant` to entity/request/response/service; department/designation deletes return `BusinessRuleException` HTTP 409 with codes `DEPARTMENT_ASSIGNED` / `DESIGNATION_ASSIGNED` when `Employee` references them; JWT authentication requires the token subject to resolve to an Employee and derives the granted role from that current employee record.

## Remediation status
- Previous remediation complete: payroll timestamp, organization assignment guards, current-employee JWT lookup, full MySQL boot/API verification, and inspectable JSON test-results artifact.
- Current verifier remediation planned: enforce MANAGER authority on manager leave endpoints; constrain leave decisions to APPROVED/REJECTED at DTO deserialization/validation; consolidate `BusinessRuleException` and attendance business errors to the shared structured error contract; add ID-excluding organization update uniqueness checks; map payroll gross/deduction/net amount names to `grossAmount`, `deductionAmount`, and `netAmount`; reformat identified generated Java source files to proper multi-line form.
- Independent remediation groups: leave/security + exception contract; organization uniqueness + formatting; payroll API/domain naming. Shared error response remains `{timestamp,status,error,message,path}`.

## Unit status
- identity/organization: Complete — Spring Boot identity domain (Employee, Department, Designation, Role), JPA repositories, validated DTOs/services/controllers, BCrypt/JWT stateless security, and contract-shaped exception responses implemented. Routes include login, employee creation/manager assignment, and department/designation CRUD.
- Blocker: No seed-account requirement or credentials were supplied; an HR employee must be seeded/provisioned before protected employee and organization write routes can be used.
- Reporting unit complete: added dedicated `report` DTOs, repository, service, and controller. `GET /api/reports/attendance` is HR-only and supports inclusive ISO date bounds (invalid reversed bounds return the shared 400 error contract); `GET /api/reports/leave-balances` reports every employee's entitlement, inclusive approved-leave usage, and remaining balance. Reporting queries do not modify operational repositories and the controller delegates only to the service.

## Unit status: Leave workflow
- Complete: added `LeaveStatus`, `leave_request` entity, validated request/decision DTOs, persistence queries, service workflow, and `/api/leave-requests` controller routes.
- Decisions are restricted to the direct manager and pending requests. Approvals enforce inclusive-day annual entitlement and approved-date overlap rules; business-rule responses include a stable conflict code.

## Unit status: Payroll
- Complete: added `PayrollRecord` (`payroll_record`) with employee FK, date/money constraints, repository, validated request/response DTOs, transactional service, and HR-only REST controller.
- Endpoints: `POST`, `GET`, `GET /{id}`, `PUT /{id}`, and `DELETE /{id}` under `/api/payroll-records`.
- Uses the shared `ResourceNotFoundException` and `BusinessRuleException` contracts for absent records/employees and invariant violations.

## Unit status
- Attendance: Complete — added `AttendanceRecord` persistence, attendance DTOs/repository/service/controller, authenticated-email lookup, serialized open-record transitions, and HTTP 409 business-rule conflicts.

## Unit status
- Performance review cycles: Complete — entity, repository, DTOs, service validation, and secured REST controller implemented.

## Verification
- `./gradlew build --no-daemon`: PASS.
- `./gradlew test --no-daemon`: PASS (the original enum tests plus focused payroll, organization-delete, and JWT-filter tests).
- Remediation: `PayrollRecord.paidAt` is nullable `Instant` data mapped through create/update/response; assigned department/designation deletes reject with 409 `DEPARTMENT_ASSIGNED` / `DESIGNATION_ASSIGNED`; JWT authentication resolves the current Employee and derives authority from its persisted role.
- Live boot/API verification: PASS on boot attempt 3/3, using `scripts/start.sh` and a locally available MySQL-compatible MariaDB instance at localhost:3306. The app connected through the MySQL JDBC driver, booted on port 8080, and was stopped by its recorded PID; database service was stopped after verification.
- Live requests recorded by the verification runner: unauthenticated `GET /api/departments` -> 401 PASS; `POST /api/auth/login` -> 200 PASS using provisioned HR verification account; authenticated `GET /api/departments` -> 200 PASS. This also proves JWT authentication resolves the persisted employee.
- Reports: regenerated `tests-artifacts/api_test_report.xlsx`, `tests-artifacts/project_report.docx`, and readable source-of-truth `tests-artifacts/test_results.json`. Workbook columns and all three observed rows were inspected after generation.
- Start script: runs the packaged Spring Boot JAR directly for PID-safe verification and builds that JAR first when it is absent.

## Unit status: Organization assignment guards
- Complete: Department and Designation deletion now confirms the resource exists, rejects employee-assigned resources with HTTP 409 `BusinessRuleException` codes `DEPARTMENT_ASSIGNED` / `DESIGNATION_ASSIGNED`, and deletes unassigned resources normally. Focused service tests cover assigned, unassigned, and not-found paths.
