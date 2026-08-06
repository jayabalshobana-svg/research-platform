package com.guru.researchplatform.common.dto;

import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.Instant;

/**
 * Describes the requested historical candle download interval.
 *
 * @param exchange the exchange supplying the market data
 * @param symbol the exchange-specific market symbol
 * @param timeframe the requested candle aggregation interval
 * @param startTime the inclusive start of the download range
 * @param endTime the inclusive end of the download range
 */
public record DownloadRequest(
        Exchange exchange,
        String symbol,
        Timeframe timeframe,
        Instant startTime,
        Instant endTime) {
}
