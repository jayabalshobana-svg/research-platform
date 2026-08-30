package com.guru.researchplatform.collector.runner;

import com.guru.researchplatform.collector.configuration.MarketDataCollectorConfiguration;
import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.JavaHttpExecutor;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.collector.service.HistoricalRangeImportService;
import com.guru.researchplatform.collector.service.ImportSummary;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.Duration;
import java.time.YearMonth;

public class BtcHistoricalImportRunner {

    public static void main(String[] args) {

        // ------------------------------------------------
        // Infrastructure
        // ------------------------------------------------

        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        // ------------------------------------------------
        // Binance configuration
        // ------------------------------------------------

        BinanceProperties properties = new BinanceProperties(
                URI.create("https://api.binance.com"),
                URI.create("https://data.binance.vision"),
                Duration.ofSeconds(10),
                Duration.ofSeconds(60),
                "MarketDataCollector/1.0"
        );

        // ------------------------------------------------
        // CSV processing
        // ------------------------------------------------

        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        // ------------------------------------------------
        // Application configuration
        // ------------------------------------------------

        Path databasePath = Path.of(
                "data",
                "database",
                "market-data.duckdb"
        );

        MarketDataCollectorConfiguration configuration =
                new MarketDataCollectorConfiguration(
                        databasePath,
                        httpExecutor,
                        properties,
                        fileReader,
                        validator,
                        mapper
                );

        HistoricalRangeImportService rangeImportService =
                configuration.historicalRangeImportService();

        // ------------------------------------------------
        // BTCUSDT asset
        // ------------------------------------------------

        Asset btcUsdt = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );

        // ------------------------------------------------
        // Import range
        // Start small for the first production run
        // ------------------------------------------------

        /*YearMonth startMonth = YearMonth.of(2024, 1);
        YearMonth endMonth = YearMonth.of(2024, 2);*/
        // Start from the first full month of BTCUSDT spot history.
        YearMonth startMonth = YearMonth.of(2017, 9);

// Import only completed months.
// The current month is intentionally excluded.
        YearMonth endMonth = YearMonth.now().minusMonths(1);

        System.out.println("========================================");
        System.out.println("Starting BTCUSDT historical data import");
        System.out.println("Timeframe: " + Timeframe.ONE_MINUTE.apiValue());
        System.out.println(
                "Range: " + startMonth + " to " + endMonth
        );
        System.out.println("========================================");

        ImportSummary summary =
                rangeImportService.importMonthlyRange(
                        btcUsdt,
                        Timeframe.ONE_MINUTE,
                        startMonth,
                        endMonth
                );

        // ------------------------------------------------
        // Final summary
        // ------------------------------------------------

        System.out.println("========================================");
        System.out.println("Import completed");
        System.out.println(
                "Imported months: " + summary.importedMonths()
        );
        System.out.println(
                "Skipped months: " + summary.skippedMonths()
        );
        System.out.println(
                "Failed months: " + summary.failedMonths()
        );
        System.out.println(
                "Total candles imported: "
                        + summary.totalCandlesImported()
        );
        System.out.println("========================================");
    }
}