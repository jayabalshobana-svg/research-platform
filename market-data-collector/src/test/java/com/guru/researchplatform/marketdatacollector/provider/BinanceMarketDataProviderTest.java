package com.guru.researchplatform.marketdatacollector.provider;

import com.guru.researchplatform.common.dto.DownloadRequest;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies that the Binance provider satisfies the public provider contract.
 */
class BinanceMarketDataProviderTest {
    @Test
    void implementsTheMarketDataProviderContract() {
        MarketDataProvider provider = new BinanceMarketDataProvider();

        assertInstanceOf(MarketDataProvider.class, provider);
        assertThrows(UnsupportedOperationException.class, () -> provider.downloadHistoricalCandles(
                new DownloadRequest(
                        Exchange.BINANCE,
                        "BTCUSDT",
                        Timeframe.ONE_HOUR,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-01-01T01:00:00Z"))));
    }
}
