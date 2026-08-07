# Package Structure

**Version:** 1.0

**Status:** Architecture Frozen

---

# Purpose

This document defines the standard package structure for every module.

Consistency is more important than personal preference.

Every module should follow the same structure whenever applicable.

---

# Standard Module Layout

module

├── api

├── domain

├── service

├── model

├── validation

├── mapper

├── infrastructure

├── configuration

├── repository

├── exception

└── util

---

# Package Responsibilities

## api

Contains

- REST Controllers
- Request DTOs
- Response DTOs

Never contains business logic.

---

## domain

Contains

Business concepts.

Examples

Asset

Candle

Trade

Pattern

Indicator

Domain objects should remain independent from Spring Boot.

---

## service

Contains

Business orchestration.

Responsibilities

- Use cases
- Business workflow
- Coordination

Never communicates directly with REST requests.

---

## model

Contains

Immutable models.

Prefer Java Records where appropriate.

---

## validation

Contains

Business validations.

Examples

Request validation

Domain validation

Business rule validation

---

## mapper

Responsible for object transformation.

Examples

DTO → Domain

Domain → Entity

Entity → DTO

---

## infrastructure

Contains

External integrations.

Examples

Binance API

DuckDB

File System

HTTP Clients

---

## configuration

Spring Boot configuration.

Beans

Properties

Configuration classes

No business logic.

---

## repository

Persistence abstraction.

Prefer interfaces over implementations.

---

## exception

Custom exceptions.

Organize by module.

---

## util

Only generic reusable helpers.

No business logic.

Avoid creating utility classes unless genuinely reusable.

---

# Naming Conventions

Packages

lowercase

Classes

PascalCase

Methods

camelCase

Constants

UPPER_SNAKE_CASE

Interfaces

Meaningful nouns.

Example

MarketDataProvider

NOT

IMarketDataProvider

---

# Module Independence

Modules must never expose internal packages.

Only public interfaces may be consumed by other modules.

---

# Recommended Example

market-data-collector

api

service

domain

provider

validation

mapper

configuration

repository

exception

---

# Rules

Every module should look familiar.

A developer should immediately understand where code belongs without reading documentation.

Consistency is mandatory.