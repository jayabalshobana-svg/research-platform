package com.guru.researchplatform.collector.runner;

import com.guru.researchplatform.collector.repository.CandleRepository;
import com.guru.researchplatform.collector.repository.duckdb.DuckDbCandleRepository;
import com.guru.researchplatform.collector.service.quality.DataQualityReport;
import com.guru.researchplatform.collector.service.quality.DataQualityVerificationService;

import java.nio.file.Path;

public class DataQualityVerificationRunner {

    public static void main(String[] args) {

        Path databasePath =
                Path.of("data/database/market-data.duckdb");

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);

        DataQualityVerificationService verificationService =
                new DataQualityVerificationService(candleRepository);

        System.out.println("========================================");
        System.out.println("Starting Data Quality Verification");
        System.out.println("========================================");

        DataQualityReport report =
                verificationService.verify();

        System.out.println("Total candles       : "
                + report.totalCandles());
        System.out.println("Invalid OHLC candles: "
                + report.invalidOhlcCandles());

        System.out.println("Duplicate candles   : "
                + report.duplicateCandles());

        System.out.println("Time gaps           : "
                + report.timeGaps());

        System.out.println("Invalid open times  : "
                + report.invalidOpenTimes());

        System.out.println("Total missing candles: "
                + report.totalMissingCandles());

        System.out.println();
        System.out.println("Detected gaps:");

        verificationService.findTimeGaps()
                .forEach(gap -> System.out.println(
                        gap.previousOpenTime()
                                + " -> "
                                + gap.nextOpenTime()
                ));

        System.out.println();
        System.out.println("Invalid open time samples:");

        verificationService.findInvalidOpenTimeSamples(20)
                .forEach(candle -> System.out.println(
                        "Open: " + candle.openTime()
                                + " | Close: " + candle.closeTime()
                ));

        System.out.println();
        System.out.println("Open time offset distribution:");

        verificationService.findOpenTimeOffsetDistribution()
                .forEach(offset -> System.out.println(
                        "Second: " + offset.second()
                                + " | Candles: " + offset.count()
                ));

        System.out.println();
        System.out.println("Detected gaps:");

        verificationService.findTimeGaps()
                .forEach(gap -> System.out.println(
                        gap.previousOpenTime()
                                + " -> "
                                + gap.nextOpenTime()
                                + " | Missing candles: "
                                + gap.missingCandles()
                ));

        System.out.println("Total missing candles: "
                + report.totalMissingCandles());

        System.out.println("========================================");
        System.out.println("Data Quality Verification Completed");
        System.out.println("========================================");
    }
}