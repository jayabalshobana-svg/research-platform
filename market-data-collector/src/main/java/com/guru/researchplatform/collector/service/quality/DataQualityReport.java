package com.guru.researchplatform.collector.service.quality;

public record DataQualityReport(
        long totalCandles,
        long invalidOhlcCandles,
        long duplicateCandles,
        long timeGaps,
        long invalidOpenTimes,
        long totalMissingCandles
) {
}
