# Design Patterns

**Version:** 1.0

---

# Purpose

Defines approved design patterns used throughout the project.

Consistency is preferred over novelty.

---

# Strategy Pattern

Used for

Trading Strategies

Indicator Calculations

Backtesting Rules

Examples

StrategyEvaluator

IndicatorCalculator

---

# Factory Pattern

Used for

Provider creation.

Examples

ExchangeProviderFactory

IndicatorFactory

PatternFactory

---

# Provider Pattern

Used for

Exchange integrations.

Examples

MarketDataProvider

BinanceProvider

CoinbaseProvider

BybitProvider

---

# Repository Pattern

Used for

Persistence.

Examples

CandleRepository

TradeRepository

BacktestRepository

---

# Adapter Pattern

Used for

External APIs.

Example

Binance REST

↓

Internal Candle Model

---

# Builder Pattern

Used for

Complex immutable objects.

Examples

Backtest Configuration

Strategy Configuration

---

# Template Method

Used for

Shared download workflow.

Download

↓

Validate

↓

Normalize

↓

Persist

Exchange-specific behaviour overrides only required steps.

---

# Observer Pattern

Future

Live Market Data

Streaming

Notifications

---

# Dependency Injection

Constructor Injection only.

No field injection.

No service locator.

---

# SOLID

Mandatory.

Every module should satisfy

Single Responsibility

Open/Closed

Liskov

Interface Segregation

Dependency Inversion

---

# Anti Patterns

Avoid

God Classes

Utility Classes containing business logic

Static state

Circular dependencies

Deep inheritance

Hardcoded exchange logic

---

# Design Philosophy

Prefer

Composition

↓

Inheritance

Prefer

Interfaces

↓

Concrete classes

Prefer

Small reusable services

↓

Large monolithic services

---

# Future Extension

Every new feature should integrate using existing design patterns.

Introducing a new pattern requires:

- Architecture review
- ADR
- Documentation update