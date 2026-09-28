# Checkpoint

| Step | Status |
|---|---|
| 1 Design | Complete |
| 2 Build/config | Pending |
| 3–5 Implementation | Pending |
| 6 Seed data | Pending |
| 7 Infra/start scripts | Pending |
| 9 Documentation | Pending |
| 10–14 Verification/fix loop | Pending |
| 15 Reports | Pending |

## Decisions
- Java 21 / Spring Boot 3.4.0 / Gradle 8.11.1; package `com.enterprise.hr`; app port 8080.
- MySQL 8.4 is external infra, `mysql` compose service. JWT stateless security uses `ROLE_HR`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE` only.
- Packages: domain, dto, repository, service, controller, security, exception, config.
- Independent implementation groups: identity/organization; attendance; leave workflow; payroll; performance cycles; reporting. Shared API contract: JSON `/api`; error `{timestamp,status,error,message,path}`; ISO-8601 dates/times; DTOs never expose password hashes.
- Domain entity contract: generated Long ids. Roles exactly HR/MANAGER/EMPLOYEE and leave statuses exactly PENDING/APPROVED/REJECTED.
