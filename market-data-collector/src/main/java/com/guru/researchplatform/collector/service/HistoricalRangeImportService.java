package com.guru.researchplatform.collector.service;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.YearMonth;
import java.util.Objects;

public class HistoricalRangeImportService {

    private final MonthlyHistoricalDataImporter monthlyImporter;

    public HistoricalRangeImportService(
            MonthlyHistoricalDataImporter monthlyImporter
    ) {
        this.monthlyImporter =
                Objects.requireNonNull(
                        monthlyImporter,
                        "monthlyImporter cannot be null"
                );
    }

    public ImportSummary importMonthlyRange(
            Asset asset,
            Timeframe timeframe,
            YearMonth startMonth,
            YearMonth endMonth
    ) {
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(startMonth, "startMonth cannot be null");
        Objects.requireNonNull(endMonth, "endMonth cannot be null");

        if (startMonth.isAfter(endMonth)) {
            throw new IllegalArgumentException(
                    "startMonth cannot be after endMonth"
            );
        }

        int importedMonths = 0;
        int skippedMonths = 0;
        int failedMonths = 0;
        int totalCandlesImported = 0;

        YearMonth currentMonth = startMonth;

        while (!currentMonth.isAfter(endMonth)) {

            System.out.println("Importing month: " + currentMonth);

            try {
                int imported = monthlyImporter.importMonth(
                        asset,
                        timeframe,
                        currentMonth
                );

                if (imported > 0) {
                    importedMonths++;
                    totalCandlesImported += imported;

                    System.out.println(
                            "Month " + currentMonth
                                    + ": imported "
                                    + imported + " candles."
                    );
                } else {
                    skippedMonths++;

                    System.out.println(
                            "Month " + currentMonth + ": skipped."
                    );
                }

            } catch (Exception e) {
                failedMonths++;

                System.err.println(
                        "Month " + currentMonth
                                + ": failed - "
                                + e.getMessage()
                );
            }

            currentMonth = currentMonth.plusMonths(1);
        }

        return new ImportSummary(
                importedMonths,
                skippedMonths,
                failedMonths,
                totalCandlesImported
        );
    }
}