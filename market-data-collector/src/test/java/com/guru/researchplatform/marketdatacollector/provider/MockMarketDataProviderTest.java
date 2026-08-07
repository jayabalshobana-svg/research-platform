package com.guru.researchplatform.marketdatacollector.provider;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import com.guru.researchplatform.marketdatacollector.api.dto.DownloadRequest;
import com.guru.researchplatform.marketdatacollector.api.dto.DownloadResult;
import com.guru.researchplatform.marketdatacollector.api.dto.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MockMarketDataProvider Tests")
class MockMarketDataProviderTest {

    private MockMarketDataProvider provider;
    private Asset btcUsdt;
    private Asset ethUsdt;
    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        provider = new MockMarketDataProvider();
        btcUsdt = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        ethUsdt = new Asset(Exchange.BINANCE, "ETHUSDT", "ETH", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        startTime = Instant.parse("2024-01-01T00:00:00Z");
        endTime = Instant.parse("2024-01-10T23:59:59Z");
    }

    @Test
    @DisplayName("should return BINANCE as exchange")
    void shouldReturnBinanceExchange() {
        assertEquals(Exchange.BINANCE, provider.exchange());
    }

    @Test
    @DisplayName("should support BTCUSDT asset")
    void shouldSupportBtcUsdt() {
        assertTrue(provider.supports(btcUsdt));
    }

    @Test
    @DisplayName("should not support ETHUSDT asset")
    void shouldNotSupportEthUsdt() {
        assertFalse(provider.supports(ethUsdt));
    }

    @Test
    @DisplayName("should not support null asset")
    void shouldNotSupportNullAsset() {
        assertFalse(provider.supports(null));
    }

    @Test
    @DisplayName("should validate valid download request")
    void shouldValidateValidRequest() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        ValidationResult result = provider.validate(request);

        assertTrue(result.valid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("should reject null request")
    void shouldRejectNullRequest() {
        ValidationResult result = provider.validate(null);

        assertFalse(result.valid());
        assertFalse(result.errors().isEmpty());
        assertTrue(result.errors().get(0).contains("null"));
    }

    @Test
    @DisplayName("should reject unsupported asset")
    void shouldRejectUnsupportedAsset() {
        DownloadRequest request = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);
        ValidationResult result = provider.validate(request);

        assertFalse(result.valid());
        assertTrue(result.errors().get(0).contains("BTCUSDT"));
    }

    @Test
    @DisplayName("should reject non-daily timeframe")
    void shouldRejectNonDailyTimeframe() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime);
        ValidationResult result = provider.validate(request);

        assertFalse(result.valid());
        assertTrue(result.errors().get(0).contains("ONE_DAY"));
    }

    @Test
    @DisplayName("should reject invalid time range")
    void shouldRejectInvalidTimeRange() {
        // Create a request that will pass constructor but should be rejected
        // Actually, the constructor already validates this, so we test that it fails
        assertThrows(IllegalArgumentException.class, () ->
            DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, endTime, startTime)
        );
    }

    @Test
    @DisplayName("should download candles successfully")
    void shouldDownloadCandlesSuccessfully() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        assertNotNull(result);
        assertTrue(result.hasCandles());
        assertTrue(result.candles().size() <= 10);
        assertEquals("MOCK", result.providerName());
    }

    @Test
    @DisplayName("should generate approximately 10 candles")
    void shouldGenerateApproximately10Candles() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-20T23:59:59Z");
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, start, end);
        DownloadResult result = provider.download(request);

        assertEquals(10, result.candles().size());
    }

    @Test
    @DisplayName("should respect limit parameter")
    void shouldRespectLimitParameter() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-20T23:59:59Z");
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, start, end, 5);
        DownloadResult result = provider.download(request);

        assertEquals(5, result.candles().size());
    }

    @Test
    @DisplayName("should generate deterministic candles")
    void shouldGenerateDeterministicCandles() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);

        DownloadResult result1 = provider.download(request);
        DownloadResult result2 = provider.download(request);

        assertEquals(result1.candles().size(), result2.candles().size());
        
        for (int i = 0; i < result1.candles().size(); i++) {
            Candle candle1 = result1.candles().get(i);
            Candle candle2 = result2.candles().get(i);
            
            assertEquals(candle1.open(), candle2.open());
            assertEquals(candle1.high(), candle2.high());
            assertEquals(candle1.low(), candle2.low());
            assertEquals(candle1.close(), candle2.close());
            assertEquals(candle1.volume(), candle2.volume());
        }
    }

    @Test
    @DisplayName("should generate realistic candle structure")
    void shouldGenerateRealisticCandleStructure() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        for (Candle candle : result.candles()) {
            // OHLC rules
            assertTrue(candle.high().compareTo(candle.open()) >= 0, "high should be >= open");
            assertTrue(candle.high().compareTo(candle.close()) >= 0, "high should be >= close");
            assertTrue(candle.high().compareTo(candle.low()) >= 0, "high should be >= low");
            assertTrue(candle.low().compareTo(candle.open()) <= 0, "low should be <= open");
            assertTrue(candle.low().compareTo(candle.close()) <= 0, "low should be <= close");

            // Prices should be positive and reasonable for BTCUSDT
            assertTrue(candle.open().signum() > 0, "open should be positive");
            assertTrue(candle.close().signum() > 0, "close should be positive");
            
            // Price should be roughly in the 40k-50k range for mock data
            assertTrue(candle.open().doubleValue() > 30000, "open price should be > 30k");
            assertTrue(candle.open().doubleValue() < 60000, "open price should be < 60k");
            
            // Volume should be positive
            assertTrue(candle.volume().signum() > 0, "volume should be positive");
            
            // Trade count should be positive
            assertTrue(candle.tradeCount() > 0, "tradeCount should be positive");
        }
    }

    @Test
    @DisplayName("should have BTCUSDT asset in candles")
    void shouldHaveBtcUsdtInCandles() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        for (Candle candle : result.candles()) {
            assertEquals("BTCUSDT", candle.asset().symbol());
            assertEquals("BTC", candle.asset().baseAsset());
            assertEquals("USDT", candle.asset().quoteAsset());
        }
    }

    @Test
    @DisplayName("should have ONE_DAY timeframe in all candles")
    void shouldHaveOneDayTimeframe() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        for (Candle candle : result.candles()) {
            assertEquals(Timeframe.ONE_DAY, candle.timeframe());
        }
    }

    @Test
    @DisplayName("should have sequential daily candles")
    void shouldHaveSequentialDailyCandles() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        List<Candle> candles = result.candles();
        for (int i = 1; i < candles.size(); i++) {
            Instant previousClose = candles.get(i - 1).closeTime();
            Instant currentOpen = candles.get(i).openTime();
            
            assertEquals(previousClose, currentOpen, 
                "Candles should be sequential with no gaps");
        }
    }

    @Test
    @DisplayName("should throw on invalid download request")
    void shouldThrowOnInvalidDownloadRequest() {
        DownloadRequest request = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);

        assertThrows(IllegalArgumentException.class, () -> provider.download(request));
    }

    @Test
    @DisplayName("should return provider metadata")
    void shouldReturnProviderMetadata() {
        ProviderMetadata metadata = provider.metadata();

        assertNotNull(metadata);
        assertEquals("MOCK", metadata.providerName());
        assertEquals("1.0.0", metadata.version());
        assertFalse(metadata.description().isBlank());
    }

    @Test
    @DisplayName("should generate increasing prices over time")
    void shouldGenerateIncreasingPricesOverTime() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        List<Candle> candles = result.candles();
        if (candles.size() > 1) {
            // Each candle's base price increases by 100 USDT
            for (int i = 1; i < candles.size(); i++) {
                assertTrue(candles.get(i).open().doubleValue() > 
                           candles.get(i - 1).open().doubleValue());
            }
        }
    }

    @Test
    @DisplayName("should have consistent trade count pattern")
    void shouldHaveConsistentTradeCountPattern() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = provider.download(request);

        List<Candle> candles = result.candles();
        for (int i = 1; i < candles.size(); i++) {
            long expectedTradeCount = 5000 + (i * 100);
            assertEquals(expectedTradeCount, candles.get(i).tradeCount());
        }
    }

    @Test
    @DisplayName("should support empty time range")
    void shouldSupportEmptyTimeRange() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, startTime);
        DownloadResult result = provider.download(request);

        // Should return 1 candle for the same day
        assertEquals(1, result.candles().size());
    }

    @Test
    @DisplayName("should respect start time boundary")
    void shouldRespectStartTimeBoundary() {
        Instant mid = Instant.parse("2024-01-05T12:00:00Z");
        Instant end = Instant.parse("2024-01-10T23:59:59Z");
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, mid, end);
        DownloadResult result = provider.download(request);

        assertTrue(result.candles().get(0).openTime().equals(mid) || 
                  result.candles().get(0).openTime().isAfter(mid));
    }

    @Test
    @DisplayName("should be thread-safe and stateless")
    void shouldBeThreadSafeAndStateless() {
        MockMarketDataProvider provider1 = new MockMarketDataProvider();
        MockMarketDataProvider provider2 = new MockMarketDataProvider();

        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        
        DownloadResult result1 = provider1.download(request);
        DownloadResult result2 = provider2.download(request);

        assertEquals(result1.candles().size(), result2.candles().size());
        
        for (int i = 0; i < result1.candles().size(); i++) {
            assertEquals(result1.candles().get(i).open(), result2.candles().get(i).open());
        }
    }
}
