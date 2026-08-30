package com.guru.researchplatform.marketdatacollector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BinanceCandleLoaderTest {

    @Test
    void shouldLoadEntireJanuaryDatasetIntoCandles() {

        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

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

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.SPOT,
                AssetStatus.ACTIVE
        );

        List<Candle> candles =
                loader.load(
                        csvFile,
                        btc,
                        Timeframe.ONE_MINUTE
                );

        System.out.println();
        System.out.println("==============================");
        System.out.println("Candle Loading Result");
        System.out.println("==============================");
        System.out.println("Total candles : " + candles.size());

        assertFalse(candles.isEmpty());

        assertEquals(
                44640,
                candles.size()
        );

        Candle first = candles.get(0);

        assertEquals(
                Instant.ofEpochMilli(1704067200000L),
                first.openTime()
        );

        assertEquals(
                new BigDecimal("42283.58000000"),
                first.open()
        );

        assertEquals(
                new BigDecimal("42298.62000000"),
                first.high()
        );

        assertEquals(
                new BigDecimal("42261.02000000"),
                first.low()
        );

        assertEquals(
                new BigDecimal("42298.61000000"),
                first.close()
        );

        Candle last =
                candles.get(candles.size() - 1);

        System.out.println(
                "First candle : " + first.openTime()
        );

        System.out.println(
                "Last candle  : " + last.openTime()
        );
        assertEquals(
                btc,
                first.asset()
        );

        assertEquals(
                Timeframe.ONE_MINUTE,
                first.timeframe()
        );

        assertEquals(
                Exchange.BINANCE,
                first.exchange()
        );
    }
}