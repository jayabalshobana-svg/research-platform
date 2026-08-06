package com.guru.researchplatform.common.dto;

import com.guru.researchplatform.common.enums.DownloadStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies creation of the market-data download DTO contracts.
 */
class DownloadContractTest {
    @Test
    void createsDownloadRequest() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-01-02T00:00:00Z");

        DownloadRequest request = new DownloadRequest(
                Exchange.BINANCE, "BTCUSDT", Timeframe.ONE_HOUR, start, end);

        assertEquals(Exchange.BINANCE, request.exchange());
        assertEquals("BTCUSDT", request.symbol());
        assertEquals(Timeframe.ONE_HOUR, request.timeframe());
        assertEquals(start, request.startTime());
        assertEquals(end, request.endTime());
    }

    @Test
    void createsDownloadResult() {
        Duration duration = Duration.ofSeconds(4);
        List<String> warnings = List.of("Rate limit reached");

        DownloadResult result = new DownloadResult(
                DownloadStatus.PARTIAL_SUCCESS, 100L, 2L, 3L, duration, warnings);

        assertEquals(DownloadStatus.PARTIAL_SUCCESS, result.status());
        assertEquals(100L, result.downloaded());
        assertEquals(2L, result.duplicates());
        assertEquals(3L, result.skipped());
        assertEquals(duration, result.duration());
        assertEquals(warnings, result.warnings());
    }
}
