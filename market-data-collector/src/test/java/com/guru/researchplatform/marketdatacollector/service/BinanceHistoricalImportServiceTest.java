package com.guru.researchplatform.marketdatacollector.service;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.JavaHttpExecutor;
import com.guru.researchplatform.collector.provider.binance.archive.*;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceDefaults;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.collector.service.BinanceHistoricalImportService;
import com.guru.researchplatform.collector.service.HistoricalDataImportService;
import com.guru.researchplatform.collector.service.HistoricalRangeImportService;
import com.guru.researchplatform.collector.service.ImportSummary;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;

import com.guru.researchplatform.collector.repository.CandleRepository;
import com.guru.researchplatform.collector.repository.duckdb.DuckDbCandleRepository;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class BinanceHistoricalImportServiceTest {

    @Test
    void shouldDownloadMonthlyArchive() throws Exception {

        // 1. Create HTTP infrastructure
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        // 2. Create Binance configuration
        BinanceProperties properties =
                BinanceDefaults.properties();

        // 3. Create existing downloader
        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(
                        httpExecutor,
                        properties
                );

        // 4. Create our new orchestration service
        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();
        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(fileReader,
                        validator,
                        mapper);

        Path tempDirectory = Files.createTempDirectory("binance-candles-");

        Path databasePath = tempDirectory.resolve("candles.duckdb");

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);
        HistoricalDataImportService historicalDataImportService = new HistoricalDataImportService(candleLoader,candleRepository);

        BinanceHistoricalImportService service =
                new BinanceHistoricalImportService(
                        archiveDownloader,
                        archiveExtractor,
                        candleLoader,
                        historicalDataImportService
                );


        List<Candle> candles = service.downloadExtractAndLoad(
                createBtcUsdtAsset(),
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1)
        );

        assertFalse(candles.isEmpty());
        assertEquals(44640, candles.size());

        System.out.println("Candles loaded: " + candles.size());
        System.out.println("First candle: " + candles.getFirst().openTime());
        System.out.println("Last candle : " + candles.getLast().openTime());

        // 5. Download January 2024 BTC data

        Path csvFile = service.downloadAndExtract(
                createBtcUsdtAsset(),
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1)
        );

        // 6. Verify file was downloaded
        assertTrue(Files.exists(csvFile));
        assertTrue(Files.size(csvFile) > 0);

        System.out.println("CSV file: "
                + csvFile.toAbsolutePath());

        System.out.println("CSV size: "
                + Files.size(csvFile) + " bytes");
    }

    @Test
    void shouldDownloadExtractLoadAndSaveMonthlyCandles() throws Exception {

        Path tempDirectory = Files.createTempDirectory("binance-candles-");

        Path databasePath = tempDirectory.resolve("candles.duckdb");

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        // 2. Create Binance configuration
        BinanceProperties properties =
                BinanceDefaults.properties();

        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(
                        httpExecutor,
                        properties
                );

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);
        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();
        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(fileReader,
                        validator,
                        mapper);

        BinanceHistoricalImportService service =
                new BinanceHistoricalImportService(
                        archiveDownloader,
                        archiveExtractor,
                        candleLoader,
                        new HistoricalDataImportService(candleLoader,candleRepository)
                );
        Asset asset = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        int imported = service.downloadExtractLoadAndSave(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1)
        );

        System.out.println("Candles imported: " + imported);

        assertEquals(44_640, imported);

        assertTrue(candleRepository.exists(
                Exchange.BINANCE,
                asset,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-01T00:00:00Z")
        ));

        assertTrue(candleRepository.exists(
                Exchange.BINANCE,
                asset,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-31T23:59:00Z")
        ));
    }

    @Test
    void shouldRejectInvalidMonthRange() throws IOException {

        Asset asset = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        // 1. Create HTTP infrastructure
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        // 2. Create Binance configuration
        BinanceProperties properties =
                BinanceDefaults.properties();

        // 3. Create existing downloader
        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(
                        httpExecutor,
                        properties
                );

        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();
        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(fileReader,
                        validator,
                        mapper);

        Path tempDirectory = Files.createTempDirectory("binance-candles-");

        Path databasePath = tempDirectory.resolve("candles.duckdb");

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);

        BinanceHistoricalImportService service =
                new BinanceHistoricalImportService(
                        archiveDownloader,
                        archiveExtractor,
                        candleLoader,
                        new HistoricalDataImportService(candleLoader,candleRepository)
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> new HistoricalRangeImportService(service).importMonthlyRange(
                        asset,
                        Timeframe.ONE_MINUTE,
                        YearMonth.of(2024, 3),
                        YearMonth.of(2024, 1)
                )
        );
    }

    @Test
    void shouldImportTwoMonths() throws Exception {

        Asset asset = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        // 1. Create HTTP infrastructure
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        // 2. Create Binance configuration
        BinanceProperties properties =
                BinanceDefaults.properties();

        // 3. Create existing downloader
        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(
                        httpExecutor,
                        properties
                );

        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();
        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(fileReader,
                        validator,
                        mapper);

        Path tempDirectory = Files.createTempDirectory("binance-candles-");

        Path databasePath = tempDirectory.resolve("candles.duckdb");

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);

        BinanceHistoricalImportService service =
                new BinanceHistoricalImportService(
                        archiveDownloader,
                        archiveExtractor,
                        candleLoader,
                        new HistoricalDataImportService(candleLoader,candleRepository)
                );

        HistoricalRangeImportService rangeImportService =
                new HistoricalRangeImportService(service);

        ImportSummary summary = rangeImportService.importMonthlyRange(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1),
                YearMonth.of(2024, 2)
        );

        assertEquals(2, summary.importedMonths());
        assertEquals(0, summary.skippedMonths());
        assertEquals(0, summary.failedMonths());
        assertEquals(86_400, summary.totalCandlesImported());
    }
    @Test
    void shouldSkipAlreadyImportedMonth() throws Exception {

        Asset asset = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );

        Path tempDirectory =
                Files.createTempDirectory("binance-candles-");

        Path databasePath =
                tempDirectory.resolve("candles.duckdb");

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);

        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(
                        fileReader,
                        validator,
                        mapper
                );

        HistoricalDataImportService historicalDataImportService =
                new HistoricalDataImportService(
                        candleLoader,
                        candleRepository
                );

        // First, import the month directly using the existing CSV.
        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

        int firstImport = historicalDataImportService.importData(
                csvFile,
                asset,
                Timeframe.ONE_MINUTE
        );

        assertEquals(44_640, firstImport);

        // Verify the month is now recognized as imported.
        assertTrue(historicalDataImportService.isAlreadyImported(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1)
        ));

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        BinanceProperties properties =
                BinanceDefaults.properties();

        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(
                        httpExecutor,
                        properties
                );

        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();

        BinanceHistoricalImportService service =
                new BinanceHistoricalImportService(
                        archiveDownloader,
                        archiveExtractor,
                        candleLoader,
                        historicalDataImportService
                );

        int secondImport = service.downloadExtractLoadAndSave(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1)
        );

        assertEquals(0, secondImport);
    }

    private BinanceHistoricalImportService createService(
            Path databasePath
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor httpExecutor =
                new JavaHttpExecutor(httpClient);

        BinanceProperties properties =
                BinanceDefaults.properties();

        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(
                        httpExecutor,
                        properties
                );

        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();

        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(
                        fileReader,
                        validator,
                        mapper
                );

        CandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);

        HistoricalDataImportService historicalDataImportService =
                new HistoricalDataImportService(
                        candleLoader,
                        candleRepository
                );

        return new BinanceHistoricalImportService(
                archiveDownloader,
                archiveExtractor,
                candleLoader,
                historicalDataImportService
        );
    }

    private Asset createBtcUsdtAsset() {
        return new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );
    }

    @Test
    void shouldReportSkippedMonthInSummary() throws Exception {

        Path tempDirectory =
                Files.createTempDirectory("binance-candles-");

        Path databasePath =
                tempDirectory.resolve("candles.duckdb");

        BinanceHistoricalImportService service =
                createService(databasePath);

        Asset asset = createBtcUsdtAsset();

        HistoricalRangeImportService rangeImportService =
                new HistoricalRangeImportService(service);

        ImportSummary firstSummary = rangeImportService.importMonthlyRange(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1),
                YearMonth.of(2024, 1)
        );


        assertEquals(1, firstSummary.importedMonths());
        assertEquals(0, firstSummary.skippedMonths());
        assertEquals(0, firstSummary.failedMonths());
        assertEquals(44_640, firstSummary.totalCandlesImported());

        ImportSummary secondSummary = rangeImportService.importMonthlyRange(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1),
                YearMonth.of(2024, 1)
        );

        assertEquals(0, secondSummary.importedMonths());
        assertEquals(1, secondSummary.skippedMonths());
        assertEquals(0, secondSummary.failedMonths());
        assertEquals(0, secondSummary.totalCandlesImported());
    }
}