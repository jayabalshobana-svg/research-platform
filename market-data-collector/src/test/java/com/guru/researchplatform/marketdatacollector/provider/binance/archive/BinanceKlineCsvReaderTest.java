package com.guru.researchplatform.marketdatacollector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BinanceKlineCsvReaderTest {

    @Test
    void shouldReadOneKlineRow() {

        String line =
                "1704067200000,42283.58000000,42298.62000000," +
                        "42261.02000000,42298.61000000,35.92724000," +
                        "1704067259999,1519031.69451920,1327," +
                        "23.18766000,980394.71034560,0";

        BinanceKlineCsvReader reader =
                new BinanceKlineCsvReader();

        BinanceKlineRecord record =
                reader.readLine(line);

        assertEquals(1704067200000L, record.openTime());

        assertEquals(
                new BigDecimal("42283.58000000"),
                record.open()
        );

        assertEquals(
                new BigDecimal("42298.62000000"),
                record.high()
        );

        assertEquals(
                new BigDecimal("42261.02000000"),
                record.low()
        );

        assertEquals(
                new BigDecimal("42298.61000000"),
                record.close()
        );

        assertEquals(
                new BigDecimal("35.92724000"),
                record.volume()
        );

        assertEquals(1704067259999L, record.closeTime());

        assertEquals(
                new BigDecimal("1519031.69451920"),
                record.quoteAssetVolume()
        );

        assertEquals(1327, record.tradeCount());

        assertEquals(
                new BigDecimal("23.18766000"),
                record.takerBuyBaseVolume()
        );

        assertEquals(
                new BigDecimal("980394.71034560"),
                record.takerBuyQuoteVolume()
        );
    }
}
