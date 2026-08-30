package com.guru.researchplatform.collector.provider.binance.archive.validation;

public record BinanceKlineValidationResult(
        long totalRows,
        long validRows,
        long invalidRows,
        long duplicateTimestamps,
        long outOfOrderRows,
        long missingIntervals,
        long invalidOhlcRows,
        long negativeVolumeRows,
        long negativeTradeCountRows
) {
}