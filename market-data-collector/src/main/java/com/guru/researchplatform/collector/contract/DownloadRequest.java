package com.guru.researchplatform.collector.contract;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Timeframe;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a request to download market data for an asset.
 * 
 * Immutable record containing all parameters needed to fetch candlestick data
 * from a market data provider.
 */
public record DownloadRequest(
    Asset asset,
    Timeframe timeframe,
    Instant startTime,
    Instant endTime,
    Integer limit
) {
    /**
     * Compact constructor for validation.
     * 
     * Ensures:
     * - All required fields (asset, timeframe, startTime, endTime) are non-null
     * - startTime is not after endTime
     * - limit is positive if provided
     */
    public DownloadRequest {
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(startTime, "startTime cannot be null");
        Objects.requireNonNull(endTime, "endTime cannot be null");
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime cannot be after endTime");
        }
        
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
    }

    /**
     * Creates a download request without a limit.
     * 
     * @param asset the asset to download data for
     * @param timeframe the candlestick aggregation period
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (inclusive)
     * @return a DownloadRequest with no limit
     */
    public static DownloadRequest of(Asset asset, Timeframe timeframe, Instant startTime, Instant endTime) {
        return new DownloadRequest(asset, timeframe, startTime, endTime, null);
    }

    /**
     * Creates a download request with a limit.
     * 
     * @param asset the asset to download data for
     * @param timeframe the candlestick aggregation period
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (inclusive)
     * @param limit the maximum number of candles to retrieve
     * @return a DownloadRequest with the specified limit
     */
    public static DownloadRequest of(Asset asset, Timeframe timeframe, Instant startTime, Instant endTime, int limit) {
        return new DownloadRequest(asset, timeframe, startTime, endTime, limit);
    }

    /**
     * Determines if this request has a limit on the number of candles.
     * 
     * @return true if a limit is specified, false otherwise
     */
    public boolean hasLimit() {
        return limit != null;
    }
}
