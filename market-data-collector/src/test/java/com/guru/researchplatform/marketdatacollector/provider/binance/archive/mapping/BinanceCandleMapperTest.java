package com.guru.researchplatform.marketdatacollector.provider.binance.archive.mapping;

import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinanceCandleMapperTest {

    @Test
    void shouldMapBinanceKlineRecordToCandle() {

        BinanceKlineRecord record =
                new BinanceKlineRecord(
                        1704067200000L,
                        new BigDecimal("42283.58000000"),
                        new BigDecimal("42298.62000000"),
                        new BigDecimal("42261.02000000"),
                        new BigDecimal("42298.61000000"),
                        new BigDecimal("35.92724000"),
                        1704067259999L,
                        new BigDecimal("1519031.69451920"),
                        1327L,
                        new BigDecimal("23.18766000"),
                        new BigDecimal("980394.71034560")
                );

        Asset asset = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.SPOT,
                AssetStatus.ACTIVE
        );

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        Candle candle =
                mapper.map(
                        record,
                        asset,
                        Timeframe.ONE_MINUTE
                );

        assertEquals(
                Exchange.BINANCE,
                candle.exchange()
        );

        assertEquals(
                asset,
                candle.asset()
        );

        assertEquals(
                Timeframe.ONE_MINUTE,
                candle.timeframe()
        );

        assertEquals(
                Instant.ofEpochMilli(1704067200000L),
                candle.openTime()
        );

        assertEquals(
                Instant.ofEpochMilli(1704067259999L),
                candle.closeTime()
        );

        assertEquals(
                new BigDecimal("42283.58000000"),
                candle.open()
        );

        assertEquals(
                new BigDecimal("42298.62000000"),
                candle.high()
        );

        assertEquals(
                new BigDecimal("42261.02000000"),
                candle.low()
        );

        assertEquals(
                new BigDecimal("42298.61000000"),
                candle.close()
        );

        assertEquals(
                new BigDecimal("35.92724000"),
                candle.volume()
        );

        assertEquals(
                new BigDecimal("1519031.69451920"),
                candle.quoteAssetVolume()
        );

        assertEquals(
                new BigDecimal("23.18766000"),
                candle.takerBuyBaseVolume()
        );

        assertEquals(
                new BigDecimal("980394.71034560"),
                candle.takerBuyQuoteVolume()
        );

        assertEquals(
                1327L,
                candle.tradeCount()
        );

        assertEquals(true, candle.isBullish());
        assertEquals(false, candle.isBearish());
        assertEquals(false, candle.isDoji());

        assertEquals(
                new BigDecimal("37.60000000"),
                candle.range()
        );
    }
}
