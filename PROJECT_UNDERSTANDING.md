# Project Understanding: Research Platform

**Author:** Senior Software Engineer  
**Date:** August 2026  
**Repository:** `research-platform`  
**Architecture Style:** Modular Monolith (Java 21 / Spring Boot 3.5.x / Maven)  
**Status:** Pre-Implementation Analysis Complete  

---

## 1. Executive Summary

Research Platform is a high-performance, modular monolith written in Java 21 and Spring Boot 3.5.x for quantitative financial research and backtesting. The platform is designed from the ground up to be asset-agnostic (supporting Crypto, Stocks, Forex, Commodities, and Indices) and exchange-agnostic.

The project adheres to a strict **Documentation-First Development** methodology. The repository is currently in its initial foundation stage (version `0.1.0-SNAPSHOT`), where core module boundaries, Maven build setup, public interface contracts (`DownloadRequest`, `DownloadResult`, `MarketDataProvider`), and initial unit tests have been established, while business execution logic is stubbed out awaiting implementation.

---

## 2. Overall Vision

The vision is to build an enterprise-grade quantitative research framework capable of handling the entire research lifecycle—from multi-exchange market data ingestion to advanced signal detection, strategy modeling, backtesting, and interactive UI reporting.

Key tenets of the vision:
* **Generic & Asset-Agnostic:** Bitcoin is merely the first asset for testing. The domain model does not hardcode exchange, instrument, or asset assumptions.
* **Modular Monolith:** Built as a single deployable artifact structured into clean, isolated Maven modules with strictly linear dependencies. This provides low operational overhead while ensuring modules can be extracted into microservices in the future if required.
* **Documentation-First & AI-Friendly:** Comprehensive architecture rules, RFCs, ADRs, and guidelines ensure all contributors (human or AI) can work autonomously without violating design constraints.

---

## 3. Business Goal

### Target Pipeline
The platform automates the following end-to-end quantitative workflow:
```
Collect -> Store -> Transform -> Analyse -> Detect Patterns -> Backtest -> Visualise
```

### Version 1 Scope
1. **Historical Market Data Ingestion:** Download historical candles (Binance first, followed by other exchanges).
2. **Persistence:** Store normalized candle datasets locally in DuckDB.
3. **Indicator Engine:** Calculate technical indicators (RSI, EMA, MACD, ATR, Volume).
4. **Market Structure & Pattern Engine:** Detect Swing Points, BOS, CHoCH, and Harmonic Patterns (Gartley, Bat, Butterfly, ABCD, etc.).
5. **Strategy & Backtest Engine:** Evaluate trading rules and calculate performance metrics (Win Rate, Drawdown, CAGR, Profit Factor, Expectancy).
6. **Analytics & Frontend UI:** Provide REST APIs and a React web dashboard for visualization.

### Explicit Non-Goals for Version 1
* Automated live order execution
* Broker / Wallet integration
* High-frequency trading (HFT)
* Portfolio execution management

---

## 4. Current Architecture

### System Stack
* **Language & Runtime:** Java 21 (utilizing Records and modern features).
* **Framework:** Spring Boot 3.5.0 (`spring-boot-starter-web`).
* **Build System:** Maven Multi-Module (`.\mvnw.cmd`).
* **Testing:** JUnit 5 (Jupiter 5.12.2), Mockito.
* **Storage (Target):** DuckDB (embedded OLAP database).

### Architectural Rules & Layering
The system enforces a strict 4-layer architecture within each module:
1. **Presentation:** REST Controllers, Request DTOs.
2. **Application:** Use cases, service orchestration.
3. **Domain:** Core business rules, immutable models, ports.
4. **Infrastructure:** Exchange REST clients, database repositories, filesystem.

### Strict Forward Dependency Rules
Dependencies flow strictly downward towards stable abstractions:
```
application -> market-data-collector -> market-data -> common
```
* Circular dependencies are prohibited.
* `common` has zero internal module dependencies.
* Business logic must depend on interfaces (Constructor Injection only; field injection is forbidden).

---

## 5. Module Responsibilities

### Existing Java Modules

| Module | Primary Responsibility | Allowed Dependencies | Status |
| :--- | :--- | :--- | :--- |
| `common` | Shared DTOs (`DownloadRequest`, `DownloadResult`), Enums (`Exchange`, `Timeframe`, `DownloadStatus`), Ports, and Exceptions. | Java Standard Library, approved 3rd-party libs | Fully functional contracts & tests |
| `application` | Spring Boot entry point (`ResearchPlatformApplication`), REST controllers (`HealthController`), and configuration assembly. | `market-data-collector`, `market-data`, `common` | Running (`GET /api/health`) |
| `market-data` | Core domain models (`Candle`, `Asset`, `Timeframe`) and repository interfaces. | `common` | Module skeleton created |
| `market-data-collector` | Download orchestration, exchange provider abstractions (`MarketDataProvider`), data validation, normalization, and persistence. | `market-data`, `common` | Public contracts created; `BinanceMarketDataProvider` stubbed |

### Planned Future Modules
* `indicator-engine`: Computes technical indicators over normalized market candles.
* `market-structure`: Detects market trends, swing highs/lows, BOS, and CHoCH.
* `pattern-engine`: Detects chart and harmonic patterns.
* `strategy-engine`: Evaluates multi-factor signals to generate trade setups.
* `backtest-engine`: Executes historical backtests and computes performance stats.
* `analytics-engine`: Formats reports and risk metrics.
* `frontend`: React web UI communicating via application REST endpoints.

---

## 6. Current Sprint Focus

### Current Phase: Market Data Collector Foundations (RFC-0001)
* **Completed:**
  * Public DTO records (`DownloadRequest`, `DownloadResult`) in `common`.
  * Public contract interface `MarketDataProvider` in `market-data-collector`.
  * Stub class `BinanceMarketDataProvider` throwing `UnsupportedOperationException`.
  * Contract unit tests passing (`DownloadContractTest`, `BinanceMarketDataProviderTest`).
* **Next Immediate Deliverables (Phase 3 & 4 of RFC-0001):**
  1. Build Binance REST HTTP client provider implementation for downloading historical klines.
  2. Implement download validation logic (timestamp continuity, price sanity checks, duplicate removal).
  3. Integrate DuckDB persistence for storing downloaded candles.

---

## 7. Existing Documentation

The project features detailed documentation located under the `docs/` and `RFC/` directories:
* `README.md`: High-level build commands and module overview.
* `docs/00_Project/ProjectGuide.md`: 544-line master project specification detailing values, workflow, release phases, definition of done, and quality gates.
* `docs/01_Architecture/`: Comprehensive architectural specifications:
  * [SystemArchitecture.md](file:///c:/Guru/projects/research-platform/docs/01_Architecture/SystemArchitecture.md)
  * [ModuleArchitecture.md](file:///c:/Guru/projects/research-platform/docs/01_Architecture/ModuleArchitecture.md)
  * [DependencyRules.md](file:///c:/Guru/projects/research-platform/docs/01_Architecture/DependencyRules.md)
  * [DataFlow.md](file:///c:/Guru/projects/research-platform/docs/01_Architecture/DataFlow.md)
  * [PackageStructure.md](file:///c:/Guru/projects/research-platform/docs/01_Architecture/PackageStructure.md)
  * [DesignPatterns.md](file:///c:/Guru/projects/research-platform/docs/01_Architecture/DesignPatterns.md)
* `RFC/RFC-0001-Market-Data-Collector.md`: Fully accepted 617-line design document for historical data collection.
* `.ai/`: Guidelines for AI coding agents (`architecture.md`, `workflow.md`, `review-checklist.md`, etc.).

---

## 8. Missing Documentation & Empty Skeletons

While the core architecture docs are thorough, several files and subdirectories are currently empty placeholders:

1. **Root Meta Files:**
   * `PROJECT_GUIDE.md`, `CONTRIBUTING.md`, `CODE_STYLE.md`, `DECISIONS.md`, and `roadmap.md` in the root folder are 0-to-2 line empty skeletons. (Note: Detailed contents exist under `docs/00_Project/ProjectGuide.md`).
2. **Placeholder RFCs:**
   * `RFC-0002-DuckDB-Persistence.md`
   * `RFC-0003-Indicator-Engine.md`
   * `RFC-0004-Pattern-Engine.md`
   * `RFC-0005-Strategy-Engine.md`
   * `RFC-0006-Backtest-Engine.md`
   (All are 2-line title headers without specifications).
3. **Empty Subdirectories under `docs/`:**
   * `docs/00_Project/` files (`CodingStandards.md`, `Decisions.md`, `Glossary.md`, `Roadmap.md`, `Scope.md`, `Vision.md`).
   * `docs/01_Architecture/SequenceDiagrams.md`.
   * `docs/02_ADR/` (`ADR-0001.md`, `ADR-0002.md`, `ADR-0003.md`).
   * `docs/03_Modules/MarketDataCollector/` (`API.md`, `Requirements.md`, `Design.md`, `Database.md`, `Testing.md`).
   * `docs/strategy/` (9 empty files: `Backtesting.md`, `EMA.md`, `Harmonic.md`, etc.).
   * `.ai/knowledge/` and `PROMPTS/` directories.

---

## 9. Existing Java Modules & Code State

```
research-platform (pom)
├── common (jar)
│   ├── src/main/java/com/guru/researchplatform/common/
│   │   ├── dto/ (DownloadRequest, DownloadResult)
│   │   └── enums/ (DownloadStatus, Exchange, Timeframe)
│   └── src/test/java/com/guru/researchplatform/common/ (Unit tests passing)
├── market-data-collector (jar)
│   ├── src/main/java/com/guru/researchplatform/
│   │   ├── marketdatacollector/provider/ (MarketDataProvider, BinanceMarketDataProvider)
│   │   └── collector/ (Subdirectories: client, configuration, mapper, provider, service, validation)
│   └── src/test/java/com/guru/researchplatform/marketdatacollector/provider/ (BinanceMarketDataProviderTest passing)
├── market-data (jar)
│   └── src/main/java/com/guru/researchplatform/candle/ (Subdirectories: domain, entity, repository, service - empty)
└── application (jar)
    └── src/main/java/com/guru/researchplatform/application/
        ├── ResearchPlatformApplication.java (@SpringBootApplication)
        └── controller/HealthController.java (GET /api/health)
```

All 5 modules compile and pass `mvnw test` cleanly in 3.3 seconds.

---

## 10. Potential Architecture Risks

1. **Package Naming Inconsistency in `market-data-collector`:**
   * `MarketDataProvider` is in package `com.guru.researchplatform.marketdatacollector.provider`.
   * Package structure docs recommend `com.guru.researchplatform.collector.*`.
   * There are duplicate directory paths (`marketdatacollector` vs `collector`), which can lead to package import confusion.
2. **DuckDB Integration without Detailed RFC-0002:**
   * RFC-0001 Phase 5 mandates DuckDB persistence. However, `RFC-0002-DuckDB-Persistence.md` is currently an empty placeholder.
   * Proceeding with persistence implementation without a detailed RFC violates the platform's strict "Documentation-First / RFC-First" policy.
3. **Empty Root Documentation Files:**
   * New developers or AI agents inspecting root `PROJECT_GUIDE.md`, `CODE_STYLE.md`, or `DECISIONS.md` will find empty files, potentially missing the actual master guide in `docs/00_Project/ProjectGuide.md`.
4. **Missing Dependencies for HTTP & Storage:**
   * `pom.xml` does not yet declare dependencies for DuckDB JDBC, HTTP client libraries (e.g. RestClient/WebClient), or resiliency frameworks (e.g. Resilience4j for rate limiting and exponential backoff).

---

## 11. Questions Before Implementation

1. **Root Documentation Alignment:** Should root documentation files (`PROJECT_GUIDE.md`, `CODE_STYLE.md`, `DECISIONS.md`, `ROADMAP.md`) be symlinked or updated to point to `docs/00_Project/ProjectGuide.md` to prevent confusion?
2. **RFC-0002 Completion:** Should `RFC-0002-DuckDB-Persistence.md` be elaborated and formally accepted prior to starting Phase 5 (DuckDB integration) of the Market Data Collector?
3. **Package Consolidation:** Should we consolidate `com.guru.researchplatform.marketdatacollector` into `com.guru.researchplatform.collector` to match the exact module package layout specified in `PackageStructure.md`?
4. **HTTP Client Choice:** Is Spring 6 `RestClient` preferred for exchange REST API integration in `market-data-collector`, or should an alternative client (e.g. WebClient / OkHttp) be selected?
