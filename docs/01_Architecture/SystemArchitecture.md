# System Architecture

**Version:** 1.0

**Status:** Draft (Architecture Freeze Pending)

---

# Table of Contents

1. Overview
2. Architectural Goals
3. Architecture Style
4. High-Level System Overview
5. Layered Architecture
6. Module Overview
7. Module Responsibilities
8. Dependency Rules
9. Data Flow
10. Domain Model
11. Package Structure
12. Technology Stack
13. Design Principles
14. Error Handling Strategy
15. Testing Strategy
16. Future Expansion
17. Architecture Rules

---

# 1. Overview

Research Platform is designed as a generic financial market research platform.

It is not a cryptocurrency application.

It is not a trading bot.

It is a reusable research framework.

The architecture must remain generic enough to support:

- Crypto
- Stocks
- Forex
- Commodities
- Indices

without redesign.

---

# 2. Architectural Goals

The architecture should optimise for:

- Simplicity
- Maintainability
- Testability
- Extensibility
- Performance
- Documentation
- AI-assisted development

Every design decision should improve long-term maintainability.

---

# 3. Architecture Style

The project follows

**Maven Multi-Module Modular Monolith**

Reason:

- Easier debugging
- Faster development
- Lower operational complexity
- Easier testing
- Future migration to microservices

Each module behaves like an independent component.

---

# 4. High-Level System Overview

```
                    +----------------------+
                    |     React UI         |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    |    Application       |
                    |  Spring Boot APIs    |
                    +----------+-----------+
                               |
          +--------------------+--------------------+
          |                    |                    |
          v                    v                    v
+----------------+    +----------------+    +----------------+
| Market Data    |    | Indicator      |    | Strategy       |
| Collector      |    | Engine         |    | Engine         |
+----------------+    +----------------+    +----------------+
          |                    |                    |
          +--------------------+--------------------+
                               |
                               v
                    +----------------------+
                    |    Backtest Engine   |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    |      Analytics       |
                    +----------------------+
```

---

# 5. Layered Architecture

```
Presentation Layer
        │
        ▼
Application Layer
        │
        ▼
Domain Layer
        │
        ▼
Infrastructure Layer
```

Responsibilities:

Presentation

- REST Controllers

Application

- Orchestration

Domain

- Business Rules

Infrastructure

- Exchange APIs
- Database
- File System

---

# 6. Module Overview

Current Modules

application

Spring Boot application.

Responsibilities

- REST APIs
- Configuration
- Dependency Injection
- OpenAPI
- Health Checks

---

common

Shared module.

Contains

- DTOs
- Enums
- Interfaces
- Exceptions
- Constants

---

market-data

Market data domain.

Contains

- Candle
- Asset
- Timeframe
- Market Models

---

market-data-collector

Responsible only for historical market data collection.

Responsibilities

- Exchange Providers
- Download Validation
- Data Normalisation
- Persistence Orchestration

---

Future Modules

indicator-engine

pattern-engine

market-structure

strategy-engine

backtest-engine

analytics-engine

frontend

---

# 7. Module Responsibilities

Every module owns exactly one responsibility.

Example

market-data-collector

Responsible for

✓ Download

✓ Validation

✓ Normalisation

Not Responsible for

✗ Indicators

✗ Strategy

✗ Backtesting

---

indicator-engine

Responsible only for calculating indicators.

No REST APIs.

No persistence.

No market downloads.

---

strategy-engine

Responsible only for trading strategy evaluation.

Never downloads market data.

Never calculates indicators directly.

---

# 8. Dependency Rules

Allowed

application

↓

market-data-collector

↓

market-data

↓

common

Future

application

↓

strategy-engine

↓

indicator-engine

↓

market-data

↓

common

Forbidden

common → application

market-data → application

indicator-engine → collector

Circular dependencies are never allowed.

---

# 9. Data Flow

Historical Download

Exchange

↓

Collector

↓

Validation

↓

Normalisation

↓

Persistence

↓

Market Data

↓

Indicators

↓

Patterns

↓

Strategy

↓

Backtest

↓

Analytics

---

# 10. Domain Model

Core Domain Objects

Exchange

Asset

Timeframe

Candle

Indicator

Pattern

Strategy

Trade

BacktestResult

AnalyticsReport

Every future module should build upon these domain objects.

---

# 11. Package Structure

Every module follows the same layout.

```
module

api

domain

infrastructure

configuration

service

model

exception

mapper

validation
```

Consistency across modules is mandatory.

---

# 12. Technology Stack

Backend

Java 21

Framework

Spring Boot

Build

Maven

Frontend

React

Database

DuckDB

Testing

JUnit

Mockito

Version Control

Git

GitHub

Documentation

Markdown

---

# 13. Design Principles

Follow

- SOLID
- Clean Architecture
- Interface First
- Documentation First
- Immutable DTOs
- Constructor Injection

Avoid

- Static business logic
- Circular dependencies
- Hardcoded exchanges
- Hardcoded assets

---

# 14. Error Handling Strategy

Errors should be represented using meaningful exceptions.

Example

CollectorException

↓

ExchangeUnavailableException

↓

ValidationException

↓

PersistenceException

Avoid generic RuntimeException whenever possible.

---

# 15. Testing Strategy

Every module must contain:

Unit Tests

Integration Tests (where applicable)

Public APIs should have coverage.

Business logic should remain testable without Spring Boot.

---

# 16. Future Expansion

The architecture should support future modules without redesign.

Potential additions:

- Live streaming
- Paper trading
- Portfolio analytics
- Machine learning
- AI strategy optimisation
- Cloud deployment

---

# 17. Architecture Rules

1. One module = one responsibility.

2. Modules communicate through public interfaces.

3. No circular dependencies.

4. Documentation precedes implementation.

5. Every feature begins with an RFC.

6. Every feature updates documentation.

7. Every implementation must include tests.

8. Architecture changes require an ADR.

9. Public APIs require Javadoc.

10. Preserve backward compatibility whenever possible.