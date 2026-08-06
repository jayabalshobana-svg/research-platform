# Research Platform

Research Platform is a Java 21 and Spring Boot 3.5.x Maven multi-module modular monolith for quantitative trading research.

## Build

Use the Maven Wrapper from the repository root:

```powershell
.\mvnw.cmd test
```

The repository can be imported directly into IntelliJ IDEA as a Maven project.

## Modules

| Module | Responsibility | Internal dependencies |
| --- | --- | --- |
| `application` | Spring Boot startup and application REST controllers. | `market-data`, `market-data-collector` |
| `common` | Shared DTOs and enums for public module contracts. | None |
| `market-data` | Reserved boundary for market-data functionality. | `common` |
| `market-data-collector` | Public market-data download provider contracts. | `common` |

`HealthController` exposes `GET /api/health` and returns `Research Platform Running`.

## Repository folders

- `docs` — architecture and product documentation.
- `database` — database assets and migrations when introduced.
- `datasets` — local research data (not versioned).
- `frontend` — future frontend application.

The Market Data Collector contains only public contracts. It has no REST client, Binance API integration, persistence, or business logic.
