package com.guru.researchplatform.collector.service;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.YearMonth;
import java.util.Objects;
import java.util.List;


public record HistoricalImportRequest(
        Asset asset,
        Timeframe timeframe,
        YearMonth from,
        YearMonth to
) {

    public HistoricalImportRequest {
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(from, "from cannot be null");
        Objects.requireNonNull(to, "to cannot be null");

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "from month cannot be after to month"
            );
        }
    }

    public List<YearMonth> months() {

        List<YearMonth> months = new java.util.ArrayList<>();

        YearMonth current = from;

        while (!current.isAfter(to)) {
            months.add(current);
            current = current.plusMonths(1);
        }

        return List.copyOf(months);
    }
}