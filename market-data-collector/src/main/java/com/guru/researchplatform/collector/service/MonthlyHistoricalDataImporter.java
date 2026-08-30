package com.guru.researchplatform.collector.service;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.YearMonth;

@FunctionalInterface
public interface MonthlyHistoricalDataImporter {

    int importMonth(
            Asset asset,
            Timeframe timeframe,
            YearMonth month
    );
}