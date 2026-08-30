package com.guru.rearchplatform.collector.repository.duckdb;

import com.guru.researchplatform.collector.repository.duckdb.DuckDbCandleRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuckDbCandleRepositoryTest {

    @Test
    void shouldInitializeDatabase() throws Exception {

        Path databasePath =
                Path.of(
                        "data",
                        "database",
                        "test-market-data.duckdb"
                );

        Files.deleteIfExists(databasePath);

        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.initialize();

        assertTrue(
                Files.exists(databasePath)
        );
    }

    @Test
    void shouldSaveOneCandle() throws Exception {

        Path databasePath = Path.of(
                "data",
                "database",
                "test-market-data.duckdb"
        );

        Files.deleteIfExists(databasePath);

        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.initialize();

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.SPOT,
                AssetStatus.ACTIVE
        );

        Candle candle = new Candle(
                Exchange.BINANCE,
                btc,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:59.999Z"),
                new BigDecimal("42283.58000000"),
                new BigDecimal("42298.62000000"),
                new BigDecimal("42261.02000000"),
                new BigDecimal("42298.61000000"),
                new BigDecimal("35.92724000"),
                new BigDecimal("1519031.69451920"),
                new BigDecimal("23.18766000"),
                new BigDecimal("980394.71034560"),
                1327
        );

        repository.saveAll(List.of(candle));

        String url = "jdbc:duckdb:" + databasePath.toAbsolutePath();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery("SELECT COUNT(*) FROM candles")) {

            resultSet.next();

            long count = resultSet.getLong(1);

            System.out.println("Candles in database: " + count);

            assertEquals(1, count);
        }
    }
    @Test
    void shouldStoreCandleValuesCorrectly() throws Exception {

        Path databasePath = Path.of(
                "data",
                "database",
                "test-market-data.duckdb"
        );

        Files.deleteIfExists(databasePath);

        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.initialize();

        Asset btc = new Asset(
                Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        Candle candle = new Candle(
                Exchange.BINANCE,
                btc,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:59.999Z"),
                new BigDecimal("42283.58000000"),
                new BigDecimal("42298.62000000"),
                new BigDecimal("42261.02000000"),
                new BigDecimal("42298.61000000"),
                new BigDecimal("35.92724000"),
                new BigDecimal("1519031.69451920"),
                new BigDecimal("23.18766000"),
                new BigDecimal("980394.71034560"),
                1327
        );

        repository.saveAll(List.of(candle));

        String url = "jdbc:duckdb:" + databasePath.toAbsolutePath();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("""
                 SELECT
                     exchange,
                     symbol,
                     timeframe,
                     open_time,
                     open,
                     high,
                     low,
                     close,
                     volume,
                     trade_count
                 FROM candles
                 """)) {

            assertTrue(resultSet.next());

            assertEquals(
                    "BINANCE",
                    resultSet.getString("exchange")
            );

            assertEquals(
                    "BTCUSDT",
                    resultSet.getString("symbol")
            );

            assertEquals(
                    "1m",
                    resultSet.getString("timeframe")
            );

            // Compare timestamp as Instant
            assertEquals(
                    candle.openTime(),
                    resultSet.getTimestamp("open_time").toInstant()
            );

            // Use compareTo for BigDecimal because database scale may differ
            assertEquals(
                    0,
                    candle.open().compareTo(
                            resultSet.getBigDecimal("open")
                    )
            );

            assertEquals(
                    0,
                    candle.high().compareTo(
                            resultSet.getBigDecimal("high")
                    )
            );

            assertEquals(
                    0,
                    candle.low().compareTo(
                            resultSet.getBigDecimal("low")
                    )
            );

            assertEquals(
                    0,
                    candle.close().compareTo(
                            resultSet.getBigDecimal("close")
                    )
            );

            assertEquals(
                    0,
                    candle.volume().compareTo(
                            resultSet.getBigDecimal("volume")
                    )
            );

            assertEquals(
                    1327L,
                    resultSet.getLong("trade_count")
            );

            assertFalse(resultSet.next());
        }
    }

    @Test
    void shouldRejectDuplicateCandle() throws Exception {

        Path databasePath = Path.of(
                "data",
                "database",
                "duplicate-test.duckdb"
        );

        Files.createDirectories(databasePath.getParent());
        Files.deleteIfExists(databasePath);

        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.initialize();

        Asset btc = new Asset(
                Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        Candle candle = new Candle(
                Exchange.BINANCE,
                btc,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:59.999Z"),
                new BigDecimal("42283.58000000"),
                new BigDecimal("42298.62000000"),
                new BigDecimal("42261.02000000"),
                new BigDecimal("42298.61000000"),
                new BigDecimal("35.92724000"),
                new BigDecimal("1519031.69451920"),
                new BigDecimal("23.18766000"),
                new BigDecimal("980394.71034560"),
                1327
        );

        repository.saveAll(List.of(candle));

        assertThrows(
                IllegalStateException.class,
                () -> repository.saveAll(List.of(candle))
        );
    }

    @Test
    void shouldReturnTrueWhenCandleExists() throws Exception {

        Path databasePath = Path.of(
                "data",
                "database",
                "exists-test.duckdb"
        );

        Files.createDirectories(databasePath.getParent());
        Files.deleteIfExists(databasePath);

        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.initialize();

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.SPOT,
                AssetStatus.ACTIVE
        );

        Candle candle = new Candle(
                Exchange.BINANCE,
                btc,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:59.999Z"),
                new BigDecimal("42283.58000000"),
                new BigDecimal("42298.62000000"),
                new BigDecimal("42261.02000000"),
                new BigDecimal("42298.61000000"),
                new BigDecimal("35.92724000"),
                new BigDecimal("1519031.69451920"),
                new BigDecimal("23.18766000"),
                new BigDecimal("980394.71034560"),
                1327
        );

        // Save first
        repository.saveAll(List.of(candle));

        // Then check
        boolean exists = repository.exists(
                candle.exchange(),
                candle.asset(),
                candle.timeframe(),
                candle.openTime()
        );

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenCandleDoesNotExist() throws Exception {

        Path databasePath = Path.of(
                "data",
                "database",
                "not-exists-test.duckdb"
        );

        Files.createDirectories(databasePath.getParent());
        Files.deleteIfExists(databasePath);

        DuckDbCandleRepository repository =
                new DuckDbCandleRepository(databasePath);

        repository.initialize();

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.SPOT,
                AssetStatus.ACTIVE
        );

        Candle candle = new Candle(
                Exchange.BINANCE,
                btc,
                Timeframe.ONE_MINUTE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:59.999Z"),
                new BigDecimal("42283.58000000"),
                new BigDecimal("42298.62000000"),
                new BigDecimal("42261.02000000"),
                new BigDecimal("42298.61000000"),
                new BigDecimal("35.92724000"),
                new BigDecimal("1519031.69451920"),
                new BigDecimal("23.18766000"),
                new BigDecimal("980394.71034560"),
                1327
        );

        // Don't save the candle
        boolean exists = repository.exists(
                candle.exchange(),
                candle.asset(),
                candle.timeframe(),
                candle.openTime()
        );

        assertFalse(exists);
    }
}