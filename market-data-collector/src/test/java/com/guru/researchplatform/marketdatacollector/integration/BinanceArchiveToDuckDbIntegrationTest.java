package com.guru.researchplatform.marketdatacollector.integration;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.collector.repository.duckdb.DuckDbCandleRepository;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinanceArchiveToDuckDbIntegrationTest {

    @Test
    void shouldLoadJanuaryArchiveIntoDuckDb() throws Exception {

        // 1. Locate the CSV file
        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

        // 2. Create a fresh test database
        Path databasePath = Path.of(
                "data",
                "database",
                "january-integration-test.duckdb"
        );

        Files.createDirectories(databasePath.getParent());
        Files.deleteIfExists(databasePath);

        // 3. Create our Asset
        Asset btc = new Asset(
                Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        // 4. Build the existing loading pipeline
        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader loader =
                new BinanceCandleLoader(
                        fileReader,
                        validator,
                        mapper
                );

        // 5. Load CSV → Candle objects
        List<Candle> candles = loader.load(
                csvFile,
                btc,
                Timeframe.ONE_MINUTE
        );

        System.out.println("Candles loaded: " + candles.size());

        // 6. Save Candles → DuckDB
        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.saveAll(candles);

        System.out.println("Candles saved successfully.");
        String url =
                "jdbc:duckdb:" + databasePath.toAbsolutePath();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery("SELECT COUNT(*) FROM candles")) {

            resultSet.next();

            long databaseCount = resultSet.getLong(1);

            System.out.println("Candles in database: " + databaseCount);

            assertEquals(candles.size(), databaseCount);
        }

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            // Verify first candle
            try (ResultSet resultSet = statement.executeQuery("""
            SELECT open_time, open, high, low, close
            FROM candles
            ORDER BY open_time ASC
            LIMIT 1
            """)) {

                assertTrue(resultSet.next());

                Candle firstFromDatabase = candles.get(0);

                assertEquals(
                        firstFromDatabase.openTime(),
                        resultSet.getTimestamp("open_time").toInstant()
                );

                assertEquals(
                        0,
                        firstFromDatabase.open().compareTo(
                                resultSet.getBigDecimal("open")
                        )
                );

                assertEquals(
                        0,
                        firstFromDatabase.high().compareTo(
                                resultSet.getBigDecimal("high")
                        )
                );

                assertEquals(
                        0,
                        firstFromDatabase.low().compareTo(
                                resultSet.getBigDecimal("low")
                        )
                );

                assertEquals(
                        0,
                        firstFromDatabase.close().compareTo(
                                resultSet.getBigDecimal("close")
                        )
                );

                System.out.println("First candle verified successfully.");
            }

            // Verify last candle
            try (ResultSet resultSet = statement.executeQuery("""
            SELECT open_time, open, high, low, close
            FROM candles
            ORDER BY open_time DESC
            LIMIT 1
            """)) {

                assertTrue(resultSet.next());

                Candle lastFromDatabase =
                        candles.get(candles.size() - 1);

                assertEquals(
                        lastFromDatabase.openTime(),
                        resultSet.getTimestamp("open_time").toInstant()
                );

                assertEquals(
                        0,
                        lastFromDatabase.open().compareTo(
                                resultSet.getBigDecimal("open")
                        )
                );

                assertEquals(
                        0,
                        lastFromDatabase.high().compareTo(
                                resultSet.getBigDecimal("high")
                        )
                );

                assertEquals(
                        0,
                        lastFromDatabase.low().compareTo(
                                resultSet.getBigDecimal("low")
                        )
                );

                assertEquals(
                        0,
                        lastFromDatabase.close().compareTo(
                                resultSet.getBigDecimal("close")
                        )
                );

                System.out.println("Last candle verified successfully.");
            }
        }
    }
}