# RFC-0001: Market Data Collector

**RFC ID:** RFC-0001

**Title:** Market Data Collector

**Status:** Accepted

**Version:** 1.0

**Created:** August 2026

**Owner:** Research Platform Team

---

# Table of Contents

1. Executive Summary
2. Motivation
3. Problem Statement
4. Goals
5. Non Goals
6. Functional Requirements
7. Non Functional Requirements
8. Module Responsibilities
9. Supported Exchanges
10. Supported Assets
11. Supported Timeframes
12. Public API
13. Domain Model
14. Download Workflow
15. Validation Rules
16. Retry Strategy
17. Rate Limit Handling
18. Error Handling
19. Persistence Requirements
20. Testing Strategy
21. Acceptance Criteria
22. Future Enhancements

---

# 1. Executive Summary

The Market Data Collector is responsible for downloading, validating,
normalising and persisting historical market data.

It provides a generic mechanism for collecting historical data from one or
more exchanges.

The collector is intentionally independent of any specific exchange.

Exchange-specific behaviour is implemented using provider implementations.

---

# 2. Motivation

Every quantitative research platform requires reliable historical market data.

Instead of tightly coupling the system to one exchange, the platform introduces
an abstraction layer that allows multiple providers.

Benefits

- Multiple exchanges
- Easier testing
- Easier extension
- Cleaner architecture

---

# 3. Problem Statement

Without a dedicated Market Data Collector,

every future module would need to understand

- Exchange APIs
- Authentication
- Rate limits
- Download logic
- Validation
- Retry handling

This creates duplication and tight coupling.

The Market Data Collector centralises these concerns.

---

# 4. Goals

Version 1 should support

✓ Historical candle download

✓ Binance provider

✓ Download validation

✓ Retry mechanism

✓ Duplicate detection

✓ Data normalisation

✓ Persistence

✓ Progress reporting

Future versions should support additional exchanges.

---

# 5. Non Goals

The collector is NOT responsible for

- Indicator calculation
- Pattern detection
- Strategy execution
- Trading
- Portfolio management
- Analytics

---

# 6. Functional Requirements

The collector shall

- Download historical candles
- Resume interrupted downloads
- Validate downloaded data
- Skip duplicate candles
- Normalise provider responses
- Persist validated candles
- Report download statistics

---

# 7. Non Functional Requirements

The collector should be

Reliable

Restartable

Extensible

Provider independent

Well tested

Memory efficient

Observable

Maintainable

---

# 8. Module Responsibilities

The Market Data Collector owns

- Download orchestration
- Provider selection
- Validation
- Retry logic
- Rate limit handling
- Progress reporting

It does NOT own

- Indicator calculation
- Trading logic
- REST APIs
- Analytics

---

# 9. Supported Exchanges

Version 1

Binance

Future

Coinbase

Kraken

Bybit

OKX

Bitfinex

Each exchange must implement

MarketDataProvider.

---

# 10. Supported Assets

The collector is generic.

Examples

BTCUSDT

ETHUSDT

SOLUSDT

AAPL

MSFT

EURUSD

XAUUSD

No asset-specific code.

---

# 11. Supported Timeframes

Initial support

1m

5m

15m

30m

1h

4h

1d

1w

1M

Timeframes should be represented using a shared enum.

---

# 12. Public API

Core Interface

MarketDataProvider

Responsibilities

Download historical candles.

Collector Service

Responsibilities

Coordinate downloads.

Validation Service

Responsibilities

Validate downloaded data.

Persistence Service

Responsibilities

Store market data.

---

# 13. Domain Model

Core Objects

Exchange

Asset

Timeframe

DownloadRequest

DownloadResult

Candle

ValidationResult

DownloadStatistics

ProviderResponse

---

# 14. Download Workflow

User Request

↓

Validate Request

↓

Select Provider

↓

Download Data

↓

Retry if necessary

↓

Validate Candles

↓

Normalise Data

↓

Persist Data

↓

Generate Summary

---

# 15. Validation Rules

Every downloaded candle must satisfy

Open Time present

Close Time present

OHLC values valid

Volume ≥ 0

No duplicate timestamp

Correct interval

Chronological order

Invalid candles should be rejected.

---

# 16. Retry Strategy

Transient failures

↓

Retry

Maximum retries

3

Backoff

Exponential

Permanent failures

↓

Abort

↓

Report

---

# 17. Rate Limit Handling

Providers should expose

Current limits

Remaining requests

Retry after

Collector should automatically throttle requests.

No hardcoded delays.

---

# 18. Error Handling

Example exception hierarchy

CollectorException

↓

ProviderException

↓

ValidationException

↓

PersistenceException

↓

RateLimitException

↓

DownloadFailedException

Avoid generic RuntimeException.

---

# 19. Persistence Requirements

Version 1

DuckDB

Future

PostgreSQL

ClickHouse

Parquet

Persistence implementation must remain replaceable.

---

# 20. Testing Strategy

Required

Unit Tests

Provider Tests

Integration Tests

Validation Tests

Retry Tests

Performance Tests

Acceptance Tests

Target

High business logic coverage.

---

# 21. Acceptance Criteria

The module is complete when

✓ Historical download succeeds

✓ Validation works

✓ Retry works

✓ Duplicate detection works

✓ Persistence works

✓ Tests pass

✓ Documentation updated

✓ Build passes

---

# 22. Future Enhancements

Planned

Incremental downloads

Realtime WebSocket support

Parallel downloads

Compression

Caching

Automatic data repair

Multiple persistence providers

Cloud storage

---

# Out of Scope (Version 1)

The following features are explicitly excluded from this RFC.

- WebSocket streaming
- Live trading
- Order management
- Portfolio management
- Machine learning
- Distributed execution

These features will be addressed through future RFCs.

---

# Related Documents

PROJECT_GUIDE.md

SystemArchitecture.md

ModuleArchitecture.md

DependencyRules.md

PackageStructure.md

DataFlow.md

DesignPatterns.md

ADR-0001

---

# Implementation Plan

Phase 1

Contracts

Phase 2

Provider Abstraction

Phase 3

Binance Provider

Phase 4

Validation

Phase 5

Persistence

Phase 6

REST API

Phase 7

Testing

Phase 8

Documentation

---

# Approval

Status

Accepted

Architecture Version

1.0

This RFC becomes the implementation blueprint for the Market Data Collector module.

Future modifications should be proposed through an updated RFC or an additional RFC if they introduce significant behavioural changes.