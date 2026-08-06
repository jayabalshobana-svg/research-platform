# Architecture

Research Platform is a Maven multi-module modular monolith running on Java 21 and Spring Boot 3.5.x.

The current module boundaries are:

- `application` depends on `market-data` and `market-data-collector`.
- `market-data` depends on `common`.
- `market-data-collector` depends on `common`.
- `common` has no internal module dependencies.

The Market Data Collector module exposes contracts only. REST clients, exchange API integration, persistence, and business logic are outside the current scope.
