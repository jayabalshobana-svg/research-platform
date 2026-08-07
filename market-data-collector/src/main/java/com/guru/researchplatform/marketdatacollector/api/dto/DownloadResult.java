package com.guru.researchplatform.marketdatacollector.api.dto;

import com.guru.researchplatform.common.domain.Candle;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a successful market data download operation.
 * 
 * Immutable record containing the downloaded candles and metadata about
 * the download operation.
 */
public record DownloadResult(
    List<Candle> candles,
    int totalCount,
    Instant downloadedAt,
    String providerName
) {
    /**
     * Compact constructor for validation.
     * 
     * Ensures:
     * - candles list is non-null
     * - totalCount is non-negative
     * - candles count matches totalCount
     * - downloadedAt is non-null
     * - providerName is non-null and non-blank
     */
    public DownloadResult {
        Objects.requireNonNull(candles, "candles list cannot be null");
        
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount cannot be negative");
        }
        
        if (candles.size() != totalCount) {
            throw new IllegalArgumentException(
                "candles size (" + candles.size() + ") must match totalCount (" + totalCount + ")"
            );
        }
        
        Objects.requireNonNull(downloadedAt, "downloadedAt cannot be null");
        
        Objects.requireNonNull(providerName, "providerName cannot be null");
        if (providerName.isBlank()) {
            throw new IllegalArgumentException("providerName cannot be blank");
        }
        
        // Make defensive copy to preserve immutability
        candles = new ArrayList<>(candles);
    }

    /**
     * Returns an unmodifiable copy of the candles list.
     * 
     * @return an immutable view of the candles
     */
    @Override
    public List<Candle> candles() {
        return List.copyOf(candles);
    }

    /**
     * Determines if the download returned any candles.
     * 
     * @return true if candles list is not empty, false otherwise
     */
    public boolean hasCandles() {
        return totalCount > 0;
    }

    /**
     * Creates a successful download result with the specified candles.
     * 
     * @param candles the list of downloaded candles
     * @param providerName the name of the provider that fetched the data
     * @return a DownloadResult with current timestamp
     * @throws NullPointerException if candles or providerName is null
     * @throws IllegalArgumentException if providerName is blank
     */
    public static DownloadResult of(List<Candle> candles, String providerName) {
        Objects.requireNonNull(candles, "candles list cannot be null");
        Objects.requireNonNull(providerName, "providerName cannot be null");
        
        return new DownloadResult(candles, candles.size(), Instant.now(), providerName);
    }

    /**
     * Creates an empty successful download result.
     * 
     * @param providerName the name of the provider that was queried
     * @return a DownloadResult with no candles
     */
    public static DownloadResult empty(String providerName) {
        Objects.requireNonNull(providerName, "providerName cannot be null");
        
        return new DownloadResult(List.of(), 0, Instant.now(), providerName);
    }
}
