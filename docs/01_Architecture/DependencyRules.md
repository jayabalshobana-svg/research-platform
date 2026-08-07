# Dependency Rules

**Version:** 1.0

**Status:** Draft (Architecture Freeze Pending)

---

# Table of Contents

1. Purpose
2. Dependency Philosophy
3. Architectural Layers
4. Module Dependency Rules
5. Allowed Dependencies
6. Forbidden Dependencies
7. Interface Ownership
8. Runtime Dependency Flow
9. Maven Dependency Rules
10. Package Dependency Rules
11. External Dependencies
12. Dependency Injection Rules
13. Circular Dependency Policy
14. Extension Guidelines
15. Architecture Validation Checklist

---

# 1. Purpose

This document defines the dependency rules for the entire Research Platform.

Every module, package and class must comply with these rules.

The objective is to maintain:

- Loose Coupling
- High Cohesion
- Testability
- Maintainability
- Predictable Architecture

No implementation should violate these rules without an approved Architecture Decision Record (ADR).

---

# 2. Dependency Philosophy

Dependencies always point toward more stable components.

Business logic must never depend directly on implementation details.

Preferred direction:

Application

↓

Domain

↓

Infrastructure

Never the opposite.

Every dependency should make the system easier to maintain rather than more convenient to implement.

---

# 3. Architectural Layers

The platform is organised into four logical layers.

Presentation Layer

- REST Controllers
- Request Validation
- OpenAPI

Application Layer

- Use Cases
- Orchestration
- Services

Domain Layer

- Business Rules
- Models
- Interfaces

Infrastructure Layer

- Database
- Exchange APIs
- File System
- Configuration

Dependencies may only flow downward.

---

# 4. Module Dependency Rules

Current Module Order

application

↓

market-data-collector

↓

market-data

↓

common

Future Module Order

application

↓

analytics-engine

↓

backtest-engine

↓

strategy-engine

↓

pattern-engine

↓

market-structure

↓

indicator-engine

↓

market-data

↓

common

No module may skip architectural layers unless explicitly documented.

---

# 5. Allowed Dependencies

application

May depend on

- common
- market-data
- market-data-collector
- strategy-engine
- analytics-engine

common

May depend only on

- Java Standard Library
- Approved third-party libraries

market-data

May depend on

- common

market-data-collector

May depend on

- market-data
- common

indicator-engine

May depend on

- market-data
- common

pattern-engine

May depend on

- indicator-engine
- market-data
- common

strategy-engine

May depend on

- pattern-engine
- market-structure
- indicator-engine
- market-data
- common

backtest-engine

May depend on

- strategy-engine
- market-data
- common

analytics-engine

May depend on

- backtest-engine
- strategy-engine
- common

---

# 6. Forbidden Dependencies

The following are never allowed.

common

↓

application

market-data

↓

application

indicator-engine

↓

collector

strategy-engine

↓

collector

collector

↓

strategy-engine

analytics

↓

collector

REST Controller

↓

Repository

Repository

↓

REST Controller

Any circular dependency

Architecture violations should fail code review.

---

# 7. Interface Ownership

Every module owns its public interfaces.

Example

Market Data Collector

Owns

MarketDataProvider

Indicator Engine

Owns

IndicatorCalculator

Pattern Engine

Owns

PatternDetector

Strategy Engine

Owns

StrategyEvaluator

Backtest Engine

Owns

BacktestExecutor

No module may implement another module's internal interfaces unless explicitly intended.

---

# 8. Runtime Dependency Flow

Historical Download

Exchange

↓

Provider

↓

Collector

↓

Validation

↓

Normalization

↓

Persistence

↓

Market Data

↓

Indicator Engine

↓

Pattern Engine

↓

Market Structure

↓

Strategy Engine

↓

Backtest Engine

↓

Analytics

↓

REST API

↓

Frontend

The runtime flow should remain linear and predictable.

---

# 9. Maven Dependency Rules

Every module should declare only direct dependencies.

Avoid transitive dependency assumptions.

Example

Correct

application

↓

market-data

Incorrect

application

↓

market-data

↓

common

while also directly using common classes without declaring the dependency.

Unused dependencies should be removed.

---

# 10. Package Dependency Rules

Within every module

api

↓

service

↓

domain

↓

infrastructure

↓

configuration

General Rules

Controllers must not access repositories directly.

Services must not depend on controllers.

Infrastructure must not contain business logic.

Validation should remain reusable.

---

# 11. External Dependencies

Only well-maintained libraries should be introduced.

Every new external dependency should satisfy:

- Active maintenance
- Good documentation
- Compatible license
- Clear purpose

Avoid adding libraries for functionality already available in the JDK or Spring Boot.

Every new dependency should be documented in an ADR.

---

# 12. Dependency Injection Rules

Constructor Injection only.

Field Injection is prohibited.

Setter Injection is discouraged.

Business logic should depend on interfaces rather than concrete implementations.

Example

Correct

StrategyService

↓

MarketDataProvider

Incorrect

StrategyService

↓

BinanceMarketDataProvider

---

# 13. Circular Dependency Policy

Circular dependencies are forbidden.

Examples

Module A

↓

Module B

↓

Module A

Package A

↓

Package B

↓

Package A

Class A

↓

Class B

↓

Class A

Such designs should be refactored immediately.

---

# 14. Extension Guidelines

The architecture should support extension without modification.

Examples

New Exchange

↓

Implement

MarketDataProvider

New Indicator

↓

Implement

IndicatorCalculator

New Pattern

↓

Implement

PatternDetector

New Strategy

↓

Implement

StrategyEvaluator

New Database

↓

Implement

Repository Interface

Existing modules should remain unchanged whenever possible.

---

# 15. Architecture Validation Checklist

Before every Pull Request verify:

✓ No circular dependencies

✓ Module boundaries respected

✓ Public interfaces preserved

✓ Constructor Injection used

✓ No forbidden dependencies

✓ No business logic inside controllers

✓ No business logic inside repositories

✓ Dependencies documented

✓ Build successful

✓ Unit tests passing

✓ Documentation updated

Only after all checks pass may the feature be merged.

---

# Summary

The dependency rules exist to ensure that the platform remains modular, maintainable and extensible as it grows.

Every developer and AI assistant must follow these rules before introducing new modules or modifying existing ones.

Architecture violations should always be treated as defects rather than implementation shortcuts.