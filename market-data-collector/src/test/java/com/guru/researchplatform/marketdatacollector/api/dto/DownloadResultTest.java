package com.guru.researchplatform.marketdatacollector.api.dto;

import com.guru.researchplatform.collector.contract.DownloadResult;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DownloadResult Tests")
class DownloadResultTest {

    private Asset btcUsdt;
    private Candle candle;
    private Instant downloadedAt;

    @BeforeEach
    void setUp() {
        btcUsdt = new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE);
        downloadedAt = Instant.now();

        Instant openTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant closeTime = Instant.parse("2024-01-01T01:00:00Z");

        candle = new Candle(
            Exchange.BINANCE, btcUsdt, Timeframe.ONE_HOUR,
            openTime, closeTime,
            BigDecimal.valueOf(40000), BigDecimal.valueOf(41000),
            BigDecimal.valueOf(39500), BigDecimal.valueOf(40500),
            BigDecimal.valueOf(100), BigDecimal.valueOf(4000000),
            BigDecimal.valueOf(50), BigDecimal.valueOf(2000000),
            1000
        );
    }

    @Test
    @DisplayName("should create valid download result with candles")
    void shouldCreateValidDownloadResult() {
        List<Candle> candles = List.of(candle);
        DownloadResult result = new DownloadResult(candles, 1, downloadedAt, "BINANCE");

        assertEquals(1, result.totalCount());
        assertEquals(1, result.candles().size());
        assertEquals("BINANCE", result.providerName());
        assertEquals(downloadedAt, result.downloadedAt());
    }

    @Test
    @DisplayName("should create empty download result")
    void shouldCreateEmptyDownloadResult() {
        DownloadResult result = new DownloadResult(List.of(), 0, downloadedAt, "BINANCE");

        assertEquals(0, result.totalCount());
        assertTrue(result.candles().isEmpty());
        assertFalse(result.hasCandles());
    }

    @Test
    @DisplayName("should create empty result using factory method")
    void shouldCreateEmptyResultUsingFactory() {
        DownloadResult result = DownloadResult.empty("BINANCE");

        assertEquals(0, result.totalCount());
        assertFalse(result.hasCandles());
        assertEquals("BINANCE", result.providerName());
    }

    @Test
    @DisplayName("should create result with candles using factory method")
    void shouldCreateResultWithCandlesUsingFactory() {
        List<Candle> candles = List.of(candle, candle);
        DownloadResult result = DownloadResult.of(candles, "BINANCE");

        assertEquals(2, result.totalCount());
        assertTrue(result.hasCandles());
        assertEquals("BINANCE", result.providerName());
    }

    @Test
    @DisplayName("should throw when candles list is null")
    void shouldThrowWhenCandlesIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadResult(null, 1, downloadedAt, "BINANCE")
        );
    }

    @Test
    @DisplayName("should throw when downloadedAt is null")
    void shouldThrowWhenDownloadedAtIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadResult(List.of(candle), 1, null, "BINANCE")
        );
    }

    @Test
    @DisplayName("should throw when providerName is null")
    void shouldThrowWhenProviderNameIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadResult(List.of(candle), 1, downloadedAt, null)
        );
    }

    @Test
    @DisplayName("should throw when providerName is blank")
    void shouldThrowWhenProviderNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadResult(List.of(candle), 1, downloadedAt, "   ")
        );
    }

    @Test
    @DisplayName("should throw when totalCount is negative")
    void shouldThrowWhenTotalCountIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadResult(List.of(), -1, downloadedAt, "BINANCE")
        );
    }

    @Test
    @DisplayName("should throw when candles count doesn't match totalCount")
    void shouldThrowWhenCountsMismatch() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadResult(List.of(candle), 2, downloadedAt, "BINANCE")
        );

        assertThrows(IllegalArgumentException.class, () ->
            new DownloadResult(List.of(candle, candle), 1, downloadedAt, "BINANCE")
        );
    }

    @Test
    @DisplayName("should return immutable candles list")
    void shouldReturnImmutableCandlesList() {
        DownloadResult result = new DownloadResult(List.of(candle), 1, downloadedAt, "BINANCE");
        List<Candle> candles = result.candles();

        assertThrows(UnsupportedOperationException.class, () -> candles.add(candle));
    }

    @Test
    @DisplayName("should distinguish results with and without candles")
    void shouldDistinguishResultsWithAndWithoutCandles() {
        DownloadResult empty = DownloadResult.empty("BINANCE");
        DownloadResult withCandles = DownloadResult.of(List.of(candle), "BINANCE");

        assertFalse(empty.hasCandles());
        assertTrue(withCandles.hasCandles());
        assertNotEquals(empty, withCandles);
    }

    @Test
    @DisplayName("should support multiple candles")
    void shouldSupportMultipleCandles() {
        List<Candle> candles = List.of(candle, candle, candle);
        DownloadResult result = DownloadResult.of(candles, "BINANCE");

        assertEquals(3, result.totalCount());
        assertEquals(3, result.candles().size());
        assertTrue(result.hasCandles());
    }
}
