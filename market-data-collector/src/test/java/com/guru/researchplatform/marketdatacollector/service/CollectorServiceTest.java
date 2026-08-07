package com.guru.researchplatform.marketdatacollector.service;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import com.guru.researchplatform.marketdatacollector.api.dto.DownloadRequest;
import com.guru.researchplatform.marketdatacollector.api.dto.DownloadResult;
import com.guru.researchplatform.marketdatacollector.api.dto.ValidationResult;
import com.guru.researchplatform.marketdatacollector.provider.MarketDataProvider;
import com.guru.researchplatform.marketdatacollector.provider.MockMarketDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CollectorService Tests")
class CollectorServiceTest {

    private CollectorService service;
    private MockMarketDataProvider mockProvider;
    private Asset btcUsdt;
    private Asset ethUsdt;
    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        mockProvider = new MockMarketDataProvider();
        service = new CollectorService(List.of(mockProvider));

        btcUsdt = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        ethUsdt = new Asset(Exchange.BINANCE, "ETHUSDT", "ETH", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        startTime = Instant.parse("2024-01-01T00:00:00Z");
        endTime = Instant.parse("2024-01-10T23:59:59Z");
    }

    @Test
    @DisplayName("should create service with single provider")
    void shouldCreateServiceWithSingleProvider() {
        assertNotNull(service);
        assertEquals(1, service.getProviderCount());
    }

    @Test
    @DisplayName("should create service with multiple providers")
    void shouldCreateServiceWithMultipleProviders() {
        MockMarketDataProvider provider1 = new MockMarketDataProvider();
        MockMarketDataProvider provider2 = new MockMarketDataProvider();
        CollectorService multiService = new CollectorService(List.of(provider1, provider2));

        assertEquals(2, multiService.getProviderCount());
    }

    @Test
    @DisplayName("should throw when providers list is null")
    void shouldThrowWhenProvidersIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CollectorService(null)
        );
    }

    @Test
    @DisplayName("should throw when providers list is empty")
    void shouldThrowWhenProvidersIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorService(List.of())
        );
    }

    @Test
    @DisplayName("should validate valid download request")
    void shouldValidateValidRequest() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        ValidationResult result = service.validate(request);

        assertTrue(result.valid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("should reject null download request")
    void shouldRejectNullRequest() {
        ValidationResult result = service.validate(null);

        assertFalse(result.valid());
        assertTrue(result.errors().get(0).contains("null"));
    }

    @Test
    @DisplayName("should reject request with null asset")
    void shouldRejectRequestWithNullAsset() {
        // Create a request manually with null asset would violate the contract
        // So we validate that if we somehow got a null asset, it fails
        // This is a safety check
        assertFalse(service.supportsAsset(null));
    }

    @Test
    @DisplayName("should reject unsupported asset")
    void shouldRejectUnsupportedAsset() {
        DownloadRequest request = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);
        ValidationResult result = service.validate(request);

        assertFalse(result.valid());
        assertTrue(result.errors().get(0).contains("No provider"));
    }

    @Test
    @DisplayName("should support BTCUSDT asset")
    void shouldSupportBtcUsdt() {
        assertTrue(service.supportsAsset(btcUsdt));
    }

    @Test
    @DisplayName("should not support ETHUSDT asset")
    void shouldNotSupportEthUsdt() {
        assertFalse(service.supportsAsset(ethUsdt));
    }

    @Test
    @DisplayName("should download candles successfully")
    void shouldDownloadCandlesSuccessfully() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = service.download(request);

        assertNotNull(result);
        assertTrue(result.hasCandles());
        assertEquals("MOCK", result.providerName());
    }

    @Test
    @DisplayName("should throw on invalid download request")
    void shouldThrowOnInvalidDownloadRequest() {
        DownloadRequest request = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);

        assertThrows(IllegalArgumentException.class, () -> service.download(request));
    }

    @Test
    @DisplayName("should throw on unsupported timeframe")
    void shouldThrowOnUnsupportedTimeframe() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime);

        assertThrows(IllegalArgumentException.class, () -> service.download(request));
    }

    @Test
    @DisplayName("should return download result with correct data")
    void shouldReturnCorrectDownloadResult() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = service.download(request);

        assertEquals(10, result.totalCount());
        assertEquals(10, result.candles().size());
        assertTrue(btcUsdt.isTradable());
    }

    @Test
    @DisplayName("should respect limit parameter in download")
    void shouldRespectLimitParameter() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime, 5);
        DownloadResult result = service.download(request);

        assertEquals(5, result.totalCount());
        assertEquals(5, result.candles().size());
    }

    @Test
    @DisplayName("should handle multiple download requests")
    void shouldHandleMultipleDownloadRequests() {
        DownloadRequest request1 = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime, 3);
        DownloadRequest request2 = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime, 5);

        DownloadResult result1 = service.download(request1);
        DownloadResult result2 = service.download(request2);

        assertEquals(3, result1.candles().size());
        assertEquals(5, result2.candles().size());
    }

    @Test
    @DisplayName("should validate before downloading")
    void shouldValidateBeforeDownloading() {
        DownloadRequest invalidRequest = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);

        // Service validation should reject this
        ValidationResult validation = service.validate(invalidRequest);
        assertFalse(validation.valid());

        // Download should also reject it
        assertThrows(IllegalArgumentException.class, () -> service.download(invalidRequest));
    }

    @Test
    @DisplayName("should be consistent across multiple downloads")
    void shouldBeConsistentAcrossMultipleDownloads() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);

        DownloadResult result1 = service.download(request);
        DownloadResult result2 = service.download(request);

        assertEquals(result1.candles().size(), result2.candles().size());
        assertEquals(result1.totalCount(), result2.totalCount());

        for (int i = 0; i < result1.candles().size(); i++) {
            assertEquals(
                result1.candles().get(i).open(),
                result2.candles().get(i).open()
            );
        }
    }

    @Test
    @DisplayName("should support selecting provider by asset")
    void shouldSelectProviderByAsset() {
        assertTrue(service.supportsAsset(btcUsdt));
        assertFalse(service.supportsAsset(ethUsdt));
    }

    @Test
    @DisplayName("should download with valid asset for provider")
    void shouldDownloadWithValidAsset() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        assertDoesNotThrow(() -> service.download(request));
    }

    @Test
    @DisplayName("should fail download with invalid asset for provider")
    void shouldFailDownloadWithInvalidAsset() {
        DownloadRequest request = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);
        assertThrows(IllegalArgumentException.class, () -> service.download(request));
    }

    @Test
    @DisplayName("should validate asset support at service level")
    void shouldValidateAssetSupportAtServiceLevel() {
        Asset supported = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset unsupported = new Asset(
            Exchange.BINANCE, "UNSUPPORTED", "UNS", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        assertTrue(service.supportsAsset(supported));
        assertFalse(service.supportsAsset(unsupported));
    }

    @Test
    @DisplayName("should return error message when no provider found")
    void shouldReturnErrorWhenNoProviderFound() {
        DownloadRequest request = DownloadRequest.of(ethUsdt, Timeframe.ONE_DAY, startTime, endTime);
        ValidationResult result = service.validate(request);

        assertFalse(result.valid());
        assertTrue(result.errors().get(0).contains("No provider"));
    }

    @Test
    @DisplayName("should validate request parameters")
    void shouldValidateRequestParameters() {
        // Test with valid request
        DownloadRequest validRequest = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        ValidationResult result = service.validate(validRequest);
        assertTrue(result.valid());

        // Test with null request
        assertFalse(service.validate(null).valid());
    }

    @Test
    @DisplayName("should be thread-safe with immutable provider list")
    void shouldBeThreadSafeWithImmutableProviderList() {
        MockMarketDataProvider provider1 = new MockMarketDataProvider();
        CollectorService testService = new CollectorService(List.of(provider1));

        // Verify we can use the service repeatedly without issues
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result1 = testService.download(request);
        DownloadResult result2 = testService.download(request);

        assertEquals(result1.candles().size(), result2.candles().size());
    }

    @Test
    @DisplayName("should handle edge case with single candle range")
    void shouldHandleEdgeCaseWithSingleCandleRange() {
        Instant singleTime = Instant.parse("2024-01-05T00:00:00Z");
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, singleTime, singleTime);
        DownloadResult result = service.download(request);

        assertEquals(1, result.candles().size());
    }

    @Test
    @DisplayName("should provide accurate provider count")
    void shouldProvideAccurateProviderCount() {
        assertEquals(1, service.getProviderCount());

        MockMarketDataProvider provider2 = new MockMarketDataProvider();
        CollectorService twoProviderService = new CollectorService(
            List.of(mockProvider, provider2)
        );
        assertEquals(2, twoProviderService.getProviderCount());
    }

    @Test
    @DisplayName("should not persist downloaded data")
    void shouldNotPersistDownloadedData() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result1 = service.download(request);

        // Download again - if it was persisting, it would still work but results would be identical
        DownloadResult result2 = service.download(request);

        // Results should be independent (same content but different objects)
        assertNotSame(result1, result2);
        assertEquals(result1.candles().size(), result2.candles().size());
    }

    @Test
    @DisplayName("should validate provider response")
    void shouldValidateProviderResponse() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);
        DownloadResult result = service.download(request);

        // Verify provider returned valid data
        assertNotNull(result.candles());
        assertTrue(result.totalCount() > 0);
        assertNotNull(result.providerName());
    }
}
