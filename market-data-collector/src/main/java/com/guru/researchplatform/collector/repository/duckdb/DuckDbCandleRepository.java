package com.guru.researchplatform.collector.repository.duckdb;


import com.guru.researchplatform.collector.repository.CandleRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

import com.guru.researchplatform.collector.service.quality.InvalidOpenTimeCandle;

import com.guru.researchplatform.collector.service.quality.OpenTimeOffset;

import java.util.ArrayList;
import java.util.List;

import com.guru.researchplatform.common.domain.Candle;



import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.Instant;
import java.util.Objects;
import com.guru.researchplatform.common.domain.Asset;

import com.guru.researchplatform.collector.service.quality.TimeGap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DuckDbCandleRepository
        implements CandleRepository {

    private static final int BATCH_SIZE = 1_000;

    private final Path databasePath;

    public DuckDbCandleRepository(Path databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public void initialize() {

        try {

            Files.createDirectories(
                    databasePath.getParent()
            );

            String url =
                    "jdbc:duckdb:" + databasePath.toAbsolutePath();

            try (Connection connection =
                         DriverManager.getConnection(url)) {

                createCandlesTable(connection);
            }

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to initialize DuckDB: "
                            + databasePath,
                    e
            );
        }
    }

    private void createCandlesTable(
            Connection connection)
            throws SQLException {

        String sql = """
        CREATE TABLE IF NOT EXISTS candles (
            exchange VARCHAR NOT NULL,
            symbol VARCHAR NOT NULL,
            timeframe VARCHAR NOT NULL,

            open_time TIMESTAMP NOT NULL,
            close_time TIMESTAMP NOT NULL,

            open DECIMAL(20, 8) NOT NULL,
            high DECIMAL(20, 8) NOT NULL,
            low DECIMAL(20, 8) NOT NULL,
            close DECIMAL(20, 8) NOT NULL,

            volume DECIMAL(30, 12) NOT NULL,
            quote_asset_volume DECIMAL(30, 12) NOT NULL,

            taker_buy_base_volume DECIMAL(30, 12) NOT NULL,
            taker_buy_quote_volume DECIMAL(30, 12) NOT NULL,

            trade_count BIGINT NOT NULL,

            PRIMARY KEY (
                exchange,
                symbol,
                timeframe,
                open_time
            )
        )
        """;

        try (var statement =
                     connection.createStatement()) {

            statement.execute(sql);
        }
    }

    @Override
    public void saveAll(List<Candle> candles) {

        if (candles == null || candles.isEmpty()) {
            return;
        }

        initialize();

        String sql = """            
                INSERT INTO candles (
                exchange,
                symbol,
                timeframe,
                open_time,
                close_time,
                open,
                high,
                low,
                close,
                volume,
                quote_asset_volume,
                taker_buy_base_volume,
                taker_buy_quote_volume,
                trade_count
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String url =
                "jdbc:duckdb:" + databasePath.toAbsolutePath();

        try (Connection connection =
                     DriverManager.getConnection(url)) {

            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                int count = 0;

                for (Candle candle : candles) {

                    statement.setString(1, candle.exchange().name());
                    statement.setString(2, candle.asset().symbol());
                    statement.setString(3, candle.timeframe().apiValue());

                    statement.setTimestamp(
                            4,
                            Timestamp.from(candle.openTime())
                    );

                    statement.setTimestamp(
                            5,
                            Timestamp.from(candle.closeTime())
                    );

                    statement.setBigDecimal(6, candle.open());
                    statement.setBigDecimal(7, candle.high());
                    statement.setBigDecimal(8, candle.low());
                    statement.setBigDecimal(9, candle.close());
                    statement.setBigDecimal(10, candle.volume());
                    statement.setBigDecimal(11, candle.quoteAssetVolume());
                    statement.setBigDecimal(12, candle.takerBuyBaseVolume());
                    statement.setBigDecimal(13, candle.takerBuyQuoteVolume());
                    statement.setLong(14, candle.tradeCount());

                    statement.addBatch();
                    count++;

                    if (count % BATCH_SIZE == 0) {
                        statement.executeBatch();
                    }
                }

                // Insert remaining candles
                if (count % BATCH_SIZE != 0) {
                    statement.executeBatch();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to save candles to DuckDB",
                    e
            );
        }
    }

    @Override
    public boolean exists(
            Exchange exchange,
            Asset asset,
            Timeframe timeframe,
            Instant openTime) {

        Objects.requireNonNull(exchange, "exchange cannot be null");
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(openTime, "openTime cannot be null");

        initialize();

        String sql = """
            SELECT EXISTS (
                SELECT 1
                FROM candles
                WHERE exchange = ?
                  AND symbol = ?
                  AND timeframe = ?
                  AND open_time = ?
            )
            """;

        try (Connection connection = DriverManager.getConnection("jdbc:duckdb:" + databasePath.toAbsolutePath());
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, exchange.name());
            statement.setString(2, asset.symbol());
            statement.setString(3, timeframe.apiValue());
            statement.setTimestamp(4, Timestamp.from(openTime));

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check candle existence in DuckDB",
                    e
            );
        }
    }

    @Override
    public long countAll() {
        initialize();

        String sql = "SELECT COUNT(*) FROM candles";

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to count candles",
                    e
            );
        }
    }

    @Override
    public long countInvalidOhlc() {
        initialize();

        String sql = """
            SELECT COUNT(*)
            FROM candles
            WHERE low > high
               OR open < low
               OR open > high
               OR close < low
               OR close > high
            """;

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check invalid OHLC candles",
                    e
            );
        }
    }
    @Override
    public long countDuplicates() {
        initialize();

        String sql = """
            SELECT COUNT(*)
            FROM (
                SELECT exchange, symbol, timeframe, open_time
                FROM candles
                GROUP BY exchange, symbol, timeframe, open_time
                HAVING COUNT(*) > 1
            )
            """;

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check duplicate candles",
                    e
            );
        }
    }
    @Override
    public long countTimeGaps() {
        initialize();

        String sql = """
            SELECT COUNT(*)
            FROM (
                SELECT
                    open_time,
                    LAG(open_time) OVER (
                        PARTITION BY exchange, symbol, timeframe
                        ORDER BY open_time
                    ) AS previous_open_time
                FROM candles
            ) numbered_candles
            WHERE previous_open_time IS NOT NULL
              AND open_time > previous_open_time + INTERVAL 1 MINUTE
            """;

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check candle time gaps",
                    e
            );
        }
    }

    @Override
    public long countInvalidOpenTimes() {
        initialize();

        String sql = """
            SELECT COUNT(*)
            FROM candles
            WHERE timeframe = '1m'
              AND EXTRACT(SECOND FROM open_time) <> 0
            """;

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check invalid candle open times",
                    e
            );
        }
    }

    @Override
    public List<InvalidOpenTimeCandle> findInvalidOpenTimeSamples(
            int limit
    ) {
        initialize();

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "limit must be greater than zero"
            );
        }

        String sql = """
            SELECT open_time, close_time
            FROM candles
            WHERE timeframe = '1m'
              AND EXTRACT(SECOND FROM open_time) <> 0
            ORDER BY open_time
            LIMIT ?
            """;

        List<InvalidOpenTimeCandle> candles = new ArrayList<>();

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    candles.add(new InvalidOpenTimeCandle(
                            resultSet.getTimestamp("open_time").toInstant(),
                            resultSet.getTimestamp("close_time").toInstant()
                    ));
                }
            }

            return candles;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to find invalid open time samples",
                    e
            );
        }
    }

    @Override
    public List<OpenTimeOffset> findOpenTimeOffsetDistribution() {
        initialize();

        String sql = """
            SELECT
                EXTRACT(SECOND FROM open_time) AS second,
                COUNT(*) AS candle_count
            FROM candles
            WHERE timeframe = '1m'
            GROUP BY EXTRACT(SECOND FROM open_time)
            ORDER BY second
            """;

        List<OpenTimeOffset> offsets = new ArrayList<>();

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                offsets.add(new OpenTimeOffset(
                        resultSet.getInt("second"),
                        resultSet.getLong("candle_count")
                ));
            }

            return offsets;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to analyze candle open time offsets",
                    e
            );
        }
    }

    @Override
    public List<TimeGap> findTimeGaps() {
        initialize();

        String sql = """
            SELECT
                previous_open_time,
                open_time,
                CAST(
                    (epoch_ms(open_time) - epoch_ms(previous_open_time))
                    / 60000 - 1
                    AS BIGINT
                ) AS missing_candles
            FROM (
                SELECT
                    open_time,
                    LAG(open_time) OVER (
                        PARTITION BY exchange, symbol, timeframe
                        ORDER BY open_time
                    ) AS previous_open_time
                FROM candles
            ) numbered_candles
            WHERE previous_open_time IS NOT NULL
              AND epoch_ms(open_time) - epoch_ms(previous_open_time) > 60000
            ORDER BY previous_open_time
            """;

        List<TimeGap> gaps = new ArrayList<>();

        try (Connection connection =
                     DriverManager.getConnection(
                             "jdbc:duckdb:" + databasePath.toAbsolutePath()
                     );
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                gaps.add(new TimeGap(
                        resultSet.getTimestamp("previous_open_time").toInstant(),
                        resultSet.getTimestamp("open_time").toInstant(),
                        resultSet.getLong("missing_candles")
                ));
            }

            return gaps;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to find candle time gaps",
                    e
            );
        }
    }
}