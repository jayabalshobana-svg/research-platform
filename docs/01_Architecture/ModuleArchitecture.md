# Module Architecture

**Version:** 1.0

**Status:** Draft (Architecture Freeze Pending)

---

# Table of Contents

1. Purpose
2. Module Design Philosophy
3. Current Modules
4. Future Modules
5. Module Dependency Graph
6. Module Responsibilities
7. Public Interfaces
8. Module Communication Rules
9. Internal Package Structure
10. Extension Strategy
11. Module Lifecycle
12. Architecture Constraints

---

# 1. Purpose

This document defines the responsibilities, ownership, boundaries and communication rules for every module within the Research Platform.

Every implementation must comply with this document.

Modules should be cohesive, independent and easy to test.

No module should assume knowledge of another module's internal implementation.

---

# 2. Module Design Philosophy

The platform follows a **Modular Monolith** architecture.

Each module behaves as if it were an independent service while remaining within a single deployable application.

Every module should:

- Have one primary responsibility.
- Expose only a small public API.
- Hide implementation details.
- Avoid unnecessary dependencies.
- Be independently testable.
- Be replaceable without affecting unrelated modules.

---

# 3. Current Modules

## application

Purpose

Application entry point.

Responsibilities

- Spring Boot startup
- REST Controllers
- Dependency Injection
- Configuration
- Health Checks
- OpenAPI (future)

May depend on

- market-data-collector
- market-data
- common

Must never contain

- Business rules
- Indicator calculations
- Trading logic

---

## common

Purpose

Shared reusable components.

Contains

- DTOs
- Enums
- Interfaces
- Exceptions
- Constants
- Utility classes (only generic utilities)

Must remain lightweight.

Must never depend on any other project module.

---

## market-data

Purpose

Owns the market data domain.

Contains

- Candle
- Asset
- Exchange
- Timeframe
- Market models
- Repository contracts

Responsible only for modelling market data.

---

## market-data-collector

Purpose

Download and normalize historical market data.

Responsibilities

- Provider abstraction
- Exchange implementations
- Download orchestration
- Validation
- Data normalization

Not responsible for

- Indicator calculation
- Pattern detection
- Strategy execution
- Analytics

---

# 4. Future Modules

## indicator-engine

Responsibilities

- RSI
- EMA
- SMA
- MACD
- ATR
- Bollinger Bands
- Volume indicators

Consumes

market-data

Produces

Indicator values

---

## pattern-engine

Responsibilities

- Harmonic patterns
- ABCD
- Gartley
- Bat
- Crab
- Butterfly
- Shark
- Cypher

Consumes

Indicator Engine

Market Data

Produces

Pattern signals

---

## market-structure

Responsibilities

- Swing High
- Swing Low
- BOS
- CHoCH
- Trend Detection
- Liquidity Zones

Consumes

Market Data

Produces

Structure events

---

## strategy-engine

Responsibilities

Combine

- Indicators
- Market Structure
- Patterns

Generate

Trading opportunities.

No persistence.

No REST APIs.

---

## backtest-engine

Responsibilities

Simulate trading.

Calculate

- Win Rate
- Drawdown
- CAGR
- Expectancy
- Profit Factor

Consumes

Strategy Engine

Produces

Backtest Results

---

## analytics-engine

Responsibilities

Generate reports.

Examples

- Equity Curve
- Performance Metrics
- Monthly Returns
- Trade Distribution

---

## frontend

Responsibilities

React application.

Only communicates with REST APIs.

Never accesses persistence directly.

---

# 5. Module Dependency Graph

Allowed

```
Frontend
    │
    ▼
Application
    │
    ▼
Strategy Engine
    │
    ├──────────────┐
    ▼              ▼
Pattern Engine   Market Structure
    │              │
    └──────┬───────┘
           ▼
Indicator Engine
           │
           ▼
Market Data
           │
           ▼
Market Data Collector
           │
           ▼
Common
```

Forbidden

- Circular dependencies
- Collector depending on indicators
- Indicators depending on strategy
- Analytics depending on REST controllers

---

# 6. Module Responsibilities

Every module owns exactly one responsibility.

If a responsibility belongs elsewhere, create a new module.

Example

Indicator calculations

↓

indicator-engine

NOT

market-data

This separation is mandatory.

---

# 7. Public Interfaces

Modules communicate through interfaces.

Example

MarketDataProvider

IndicatorCalculator

PatternDetector

StrategyEvaluator

BacktestExecutor

AnalyticsGenerator

Implementation details remain private.

---

# 8. Module Communication Rules

Allowed

Interface

↓

Implementation

Forbidden

Implementation

↓

Implementation

Controllers should communicate with Services.

Services communicate with interfaces.

Infrastructure communicates with external systems.

No module should bypass another module's public API.

---

# 9. Internal Package Structure

Every module should use the same package layout.

```
module

api

domain

service

model

validation

mapper

configuration

infrastructure

exception
```

The structure should remain consistent across all modules.

---

# 10. Extension Strategy

The architecture must support adding:

- New exchanges
- New indicators
- New patterns
- New strategies
- New databases

without modifying existing modules.

Prefer extension over modification.

Follow the Open/Closed Principle.

---

# 11. Module Lifecycle

Every module follows the same lifecycle.

Requirements

↓

RFC

↓

Design

↓

Documentation

↓

Implementation

↓

Tests

↓

Review

↓

Merge

No module skips documentation.

---

# 12. Architecture Constraints

Every module must satisfy:

- Single Responsibility
- Interface-driven communication
- Constructor Injection
- Immutable DTOs where appropriate
- Unit test coverage
- Javadoc for public APIs

Modules must never:

- Access another module's internals.
- Introduce circular dependencies.
- Duplicate business logic.
- Contain hardcoded exchange-specific behavior.

All architectural changes require:

- ADR update
- Documentation update
- Architecture review