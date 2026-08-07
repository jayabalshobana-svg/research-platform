package com.guru.researchplatform.marketdatacollector.api.dto;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DownloadRequest Tests")
class DownloadRequestTest {
    
    private Asset btcUsdt;
    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        btcUsdt = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        startTime = Instant.parse("2024-01-01T00:00:00Z");
        endTime = Instant.parse("2024-12-31T23:59:59Z");
    }

    @Test
    @DisplayName("should create valid download request")
    void shouldCreateValidDownloadRequest() {
        DownloadRequest request = new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime, null);

        assertEquals(btcUsdt, request.asset());
        assertEquals(Timeframe.ONE_HOUR, request.timeframe());
        assertEquals(startTime, request.startTime());
        assertEquals(endTime, request.endTime());
        assertNull(request.limit());
    }

    @Test
    @DisplayName("should create download request with limit")
    void shouldCreateDownloadRequestWithLimit() {
        DownloadRequest request = new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime, 1000);

        assertEquals(1000, request.limit());
        assertTrue(request.hasLimit());
    }

    @Test
    @DisplayName("should create request without limit using factory method")
    void shouldCreateRequestWithoutLimitUsingFactory() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime);

        assertNull(request.limit());
        assertFalse(request.hasLimit());
    }

    @Test
    @DisplayName("should create request with limit using factory method")
    void shouldCreateRequestWithLimitUsingFactory() {
        DownloadRequest request = DownloadRequest.of(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime, 500);

        assertEquals(500, request.limit());
        assertTrue(request.hasLimit());
    }

    @Test
    @DisplayName("should throw when asset is null")
    void shouldThrowWhenAssetIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadRequest(null, Timeframe.ONE_HOUR, startTime, endTime, null)
        );
    }

    @Test
    @DisplayName("should throw when timeframe is null")
    void shouldThrowWhenTimeframeIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadRequest(btcUsdt, null, startTime, endTime, null)
        );
    }

    @Test
    @DisplayName("should throw when startTime is null")
    void shouldThrowWhenStartTimeIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, null, endTime, null)
        );
    }

    @Test
    @DisplayName("should throw when endTime is null")
    void shouldThrowWhenEndTimeIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, startTime, null, null)
        );
    }

    @Test
    @DisplayName("should throw when startTime is after endTime")
    void shouldThrowWhenStartTimeIsAfterEndTime() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, endTime, startTime, null)
        );
    }

    @Test
    @DisplayName("should throw when limit is zero or negative")
    void shouldThrowWhenLimitIsNonPositive() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime, 0)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime, -1)
        );
    }

    @Test
    @DisplayName("should allow startTime equal to endTime")
    void shouldAllowStartTimeEqualToEndTime() {
        DownloadRequest request = new DownloadRequest(btcUsdt, Timeframe.ONE_HOUR, startTime, startTime, null);

        assertEquals(startTime, request.startTime());
        assertEquals(startTime, request.endTime());
    }

    @Test
    @DisplayName("should support various timeframes")
    void shouldSupportVariousTimeframes() {
        DownloadRequest oneMin = DownloadRequest.of(btcUsdt, Timeframe.ONE_MINUTE, startTime, endTime);
        DownloadRequest oneHour = DownloadRequest.of(btcUsdt, Timeframe.ONE_HOUR, startTime, endTime);
        DownloadRequest oneDay = DownloadRequest.of(btcUsdt, Timeframe.ONE_DAY, startTime, endTime);

        assertEquals(Timeframe.ONE_MINUTE, oneMin.timeframe());
        assertEquals(Timeframe.ONE_HOUR, oneHour.timeframe());
        assertEquals(Timeframe.ONE_DAY, oneDay.timeframe());
    }
}
