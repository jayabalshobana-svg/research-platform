package com.guru.researchplatform.collector.service;

public record ImportSummary(
        int importedMonths,
        int skippedMonths,
        int failedMonths,
        int totalCandlesImported
) {
}