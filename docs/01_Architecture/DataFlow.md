# Data Flow

**Version:** 1.0

---

# Purpose

Defines how information moves through the platform.

The flow should remain predictable.

Every module receives data, transforms it and passes it forward.

---

# High-Level Flow

Exchange

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

Indicators

↓

Patterns

↓

Market Structure

↓

Strategy

↓

Backtest

↓

Analytics

↓

REST API

↓

Frontend

---

# Detailed Flow

## Step 1

Historical data download.

Input

Exchange REST API

Output

Raw Candles

---

## Step 2

Validation

Remove

- Invalid candles
- Missing timestamps
- Duplicates

Output

Validated Candles

---

## Step 3

Normalization

Convert

Different exchange formats

↓

Common Candle model

---

## Step 4

Persistence

Store

DuckDB

Future

Other databases

---

## Step 5

Indicator Engine

Input

Candles

Output

Indicators

Examples

RSI

EMA

MACD

ATR

Volume

---

## Step 6

Pattern Engine

Input

Indicators

Candles

Output

Pattern signals

Examples

Gartley

Bat

Butterfly

ABCD

Cypher

---

## Step 7

Market Structure

Input

Candles

Output

Swing High

Swing Low

BOS

CHoCH

Trend

Liquidity

---

## Step 8

Strategy Engine

Input

Indicators

Patterns

Structure

Output

Trade Opportunities

---

## Step 9

Backtest

Input

Historical Signals

Output

Trade History

Performance

---

## Step 10

Analytics

Input

Backtest

Output

Reports

Statistics

Equity Curve

Risk Metrics

---

## Step 11

Frontend

Displays

Charts

Reports

Trades

Indicators

Patterns

Analytics

---

# Rules

Data always flows forward.

Modules never bypass intermediate layers.

Every transformation should be documented.

Every transformation should be testable.