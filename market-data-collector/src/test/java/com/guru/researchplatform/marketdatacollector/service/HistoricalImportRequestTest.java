package com.guru.researchplatform.marketdatacollector.service;

import com.guru.researchplatform.collector.service.HistoricalImportRequest;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class HistoricalImportRequestTest {

    @Test
    void shouldCreateRequestForValidMonthRange() {

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );

        assertDoesNotThrow(() ->
                new HistoricalImportRequest(
                        btc,
                        Timeframe.ONE_MINUTE,
                        YearMonth.of(2024, 1),
                        YearMonth.of(2024, 3)
                )
        );
    }

    @Test
    void shouldRejectWhenFromMonthIsAfterToMonth() {

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new HistoricalImportRequest(
                        btc,
                        Timeframe.ONE_MINUTE,
                        YearMonth.of(2024, 3),
                        YearMonth.of(2024, 1)
                )
        );
    }

    @Test
    void shouldGenerateAllMonthsInRange() {

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );

        HistoricalImportRequest request =
                new HistoricalImportRequest(
                        btc,
                        Timeframe.ONE_MINUTE,
                        YearMonth.of(2024, 1),
                        YearMonth.of(2024, 3)
                );

        List<YearMonth> months = request.months();

        assertEquals(
                List.of(
                        YearMonth.of(2024, 1),
                        YearMonth.of(2024, 2),
                        YearMonth.of(2024, 3)
                ),
                months
        );
    }
}