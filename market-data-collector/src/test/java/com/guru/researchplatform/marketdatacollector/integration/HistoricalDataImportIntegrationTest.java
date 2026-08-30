package com.guru.researchplatform.marketdatacollector.integration;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.collector.repository.duckdb.DuckDbCandleRepository;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import com.guru.researchplatform.collector.service.HistoricalDataImportService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricalDataImportIntegrationTest {

    @Test
    void shouldImportHistoricalDataIntoDuckDb() throws Exception {

        // 1. CSV source
        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

        // 2. Fresh test database
        Path databasePath = Path.of(
                "data",
                "database",
                "historical-import-test.duckdb"
        );

        Files.createDirectories(databasePath.getParent());
        Files.deleteIfExists(databasePath);

        // 3. Build the existing loader
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

        // 4. Real repository
        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        // 5. Application service
        HistoricalDataImportService service =
                new HistoricalDataImportService(
                        candleLoader,
                        repository
                );

        Asset btc = new Asset(
                Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        // 6. Import
        /*service.importData(
                csvFile,
                btc,
                Timeframe.ONE_MINUTE
        );*/

        int firstImportCount = service.importData(
                csvFile,
                btc,
                Timeframe.ONE_MINUTE
        );

        System.out.println("First import: " + firstImportCount);

        int secondImportCount = service.importData(
                csvFile,
                btc,
                Timeframe.ONE_MINUTE
        );

        System.out.println("Second import: " + secondImportCount);


        assertEquals(0, secondImportCount);

        // 7. Verify database count
        String url =
                "jdbc:duckdb:" + databasePath.toAbsolutePath();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery(
                             "SELECT COUNT(*) FROM candles"
                     )) {

            resultSet.next();

            long count = resultSet.getLong(1);

            System.out.println(
                    "Candles imported: " + count
            );

            assertEquals(44640, count);
        }
    }
}