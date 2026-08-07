# Research Platform - Project Guide

**Version:** 1.0

**Status:** Active

**Last Updated:** August 2026

---

# Table of Contents

1. Purpose
2. Vision
3. Mission
4. Project Scope
5. Non Goals
6. Engineering Values
7. Engineering Philosophy
8. Documentation First Development
9. System Architecture
10. Module Architecture
11. Technology Stack
12. Development Workflow
13. Git Workflow
14. AI Collaboration Model
15. Quality Gates
16. Definition of Done
17. Documentation Standards
18. Coding Standards
19. Release Strategy
20. Long-Term Vision

---

# 1. Purpose

Research Platform is a generic quantitative research platform designed for collecting, processing, analysing and backtesting financial market data.

The project is intentionally designed to remain independent of any specific financial instrument.

Bitcoin is only the first supported asset.

The platform must eventually support:

- Cryptocurrency
- Stocks
- Forex
- Commodities
- Indices

without architectural redesign.

The repository should evolve into a reusable engineering framework rather than a collection of trading scripts.

---

# 2. Vision

Build a production-quality quantitative research platform with enterprise-grade software engineering standards.

The platform should become:

- Generic
- Modular
- Maintainable
- Extensible
- Well documented
- AI friendly

Every engineering decision should optimise for long-term maintainability rather than short-term implementation speed.

---

# 3. Mission

Provide a reusable platform capable of

Collect

↓

Store

↓

Transform

↓

Analyse

↓

Detect Patterns

↓

Backtest

↓

Visualise

financial market behaviour.

The project must support multiple exchanges, multiple asset classes and multiple research methodologies.

---

# 4. Project Scope

## Version 1

The first release includes:

- Historical market data collection
- Generic market data model
- Indicator Engine
- Market Structure Detection
- Harmonic Pattern Detection
- Strategy Engine
- Backtesting Engine
- Analytics Dashboard
- React Web Application

## Future Releases

Future versions may include:

- Live streaming market data
- Paper trading
- Portfolio analytics
- Machine learning research
- AI-assisted strategy generation
- Cloud deployment
- Multi-user collaboration

---

# 5. Non Goals

The following are intentionally outside the scope of Version 1.

- Automated trading
- Broker integration
- Exchange wallet management
- Portfolio management
- High-frequency trading
- Order execution

Research Platform focuses on research rather than execution.

---

# 6. Engineering Values

The following values guide every engineering decision.

Correctness over speed.

Quality over quantity.

Architecture over convenience.

Documentation over assumptions.

Consistency over shortcuts.

Automation over repetition.

Learning over copy-paste.

Simple solutions over unnecessary complexity.

These values should be preserved throughout the lifetime of the project.

---

# 7. Engineering Philosophy

The project follows Documentation-First Development.

Every feature begins with documentation.

The engineering lifecycle is:

Requirement

↓

Architecture

↓

Documentation

↓

Review

↓

Implementation

↓

Testing

↓

Documentation Update

↓

Merge

Architecture is never created during implementation.

Implementation follows approved documentation.

---

# 8. Documentation First Development

Documentation is treated as a first-class engineering artifact.

Every completed feature must update documentation.

Every sprint should produce:

- Architecture updates
- API documentation
- Decision records
- Review notes
- Changelog
- Prompt history
- Testing notes

Code without documentation is considered incomplete.

---

# 9. System Architecture

Architecture Style

Modular Monolith

Core Principles

- Module isolation
- Interface-driven design
- High cohesion
- Loose coupling
- Testability
- Maintainability

The architecture should allow future migration to microservices without redesigning business logic.

---

# 10. Module Architecture

Current Modules

application

Spring Boot entry point

common

Shared DTOs, enums, interfaces and exceptions

market-data

Core market data domain

market-data-collector

Historical data collection

Future Modules

indicator-engine

pattern-engine

market-structure

strategy-engine

backtest-engine

analytics-engine

frontend

Each module owns a single responsibility.

---

# 11. Technology Stack

Backend

- Java 21
- Spring Boot
- Maven

Frontend

- React

Database

- DuckDB

IDE

- IntelliJ IDEA

Version Control

- Git
- GitHub

Testing

- JUnit
- Mockito

Documentation

- Markdown

Architecture

- Modular Monolith

---

# 12. Development Workflow

Every feature follows the same lifecycle.

Requirement

↓

Architecture

↓

RFC

↓

Documentation

↓

Implementation

↓

Testing

↓

Review

↓

Documentation Update

↓

Merge

Skipping steps is not permitted.

---

# 13. Git Workflow

Main Branch

main

Integration Branch

develop

Feature Branches

feature/*

Hotfix Branches

hotfix/*

No direct commits to main.

Every feature should be developed on an independent feature branch.

---

# 14. AI Collaboration Model

Artificial Intelligence is treated as an engineering assistant rather than an architecture owner.

Before writing code, every AI assistant must read:

- PROJECT_GUIDE.md
- CONTRIBUTING.md
- CODE_STYLE.md
- DECISIONS.md
- Current RFC
- Current Sprint Prompt

AI assistants must:

- Respect architecture
- Avoid unrelated modifications
- Update documentation
- Produce tests
- Follow SOLID principles

---

# 15. Quality Gates

Before merging, the following conditions must be satisfied.

✓ Build passes

✓ Tests pass

✓ Documentation updated

✓ No architecture violations

✓ No circular dependencies

✓ Review completed

Only then may a feature be merged.

---

# 16. Definition of Done

A feature is considered complete only when:

- Requirements implemented
- Unit tests added
- Documentation updated
- Changelog updated
- Code reviewed
- Build successful
- Architecture preserved

---

# 17. Documentation Standards

Every module should contain:

README.md

Requirements.md

Design.md

Testing.md

CHANGELOG.md

Large design decisions belong inside RFC documents.

Architecture decisions belong inside ADR documents.

---

# 18. Coding Standards

Follow:

- SOLID
- Clean Architecture
- Constructor Injection
- Immutable DTOs
- Java Records where appropriate
- Public Javadoc

Avoid:

- Field Injection
- Static business logic
- Hardcoded exchange names
- Hardcoded asset names

---

# 19. Release Strategy

Version 0.x

Rapid development

Version 1.0

Stable architecture

Version 2.x

Feature expansion

Version 3.x

Cloud deployment

Version 4.x

AI-assisted research

Architecture changes become increasingly restricted after Version 1.0.

---

# 20. Long-Term Vision

The long-term objective is to build a professional research platform suitable for:

- Quantitative research
- Strategy development
- Financial market analysis
- Educational purposes
- Open-source collaboration

The repository should be understandable by both software engineers and AI assistants without relying on external conversation history.

Every engineering decision should improve the long-term quality, maintainability and extensibility of the platform.