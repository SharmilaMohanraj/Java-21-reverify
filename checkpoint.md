# Checkpoint

| Step | Status |
|---|---|
| 1 Design | Complete |
| 2 Build/config | Complete |
| 3–5 Implementation | Complete |
| 6 Seed data | Deferred: no credentials supplied; deployment-safe seed is not created |
| 7 Infra/start scripts | Complete |
| 9 Documentation | Complete |
| 10–14 Verification/fix loop | Complete with external MySQL boot blocker recorded |
| 15 Reports | Complete |

## Decisions
- Java 21 / Spring Boot 3.4.0 / Gradle 8.11.1; package `com.enterprise.hr`; app port 8080.
- MySQL 8.4 is external infra, `mysql` compose service. JWT stateless security uses `ROLE_HR`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE` only.
- Packages: domain, dto, repository, service, controller, security, exception, config.
- Independent implementation groups: identity/organization; attendance; leave workflow; payroll; performance cycles; reporting. Shared API contract: JSON `/api`; error `{timestamp,status,error,message,path}`; ISO-8601 dates/times; DTOs never expose password hashes.
- Domain entity contract: generated Long ids. Roles exactly HR/MANAGER/EMPLOYEE and leave statuses exactly PENDING/APPROVED/REJECTED.

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
- `./gradlew test --no-daemon`: PASS (2 JUnit tests).
- Boot verification: attempted 3/3 times with `scripts/start.sh`. Each attempt reached Spring startup but exited because MySQL at localhost:3306 refused the connection. Docker daemon was unavailable, so the generated MySQL compose service could not be started. The requested login curl could not be issued against a live application.
- Reports: `tests-artifacts/api_test_report.xlsx` and `tests-artifacts/project_report.docx` generated. The workbook records the actual JUnit success and the failed/skipped login probe with its real boot-attempt reason.
