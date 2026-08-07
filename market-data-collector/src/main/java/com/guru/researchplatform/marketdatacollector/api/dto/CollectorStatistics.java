package com.guru.researchplatform.marketdatacollector.api.dto;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents statistical information about the market data collector.
 * 
 * Immutable record tracking metrics such as total downloads, success rate,
 * and performance statistics.
 */
public record CollectorStatistics(
    long totalDownloads,
    long successfulDownloads,
    long failedDownloads,
    long totalCandlesCollected,
    double averageDownloadTimeSeconds,
    Instant collectedAt
) {
    /**
     * Compact constructor for validation.
     * 
     * Ensures:
     * - Counts are non-negative
     * - successfulDownloads + failedDownloads equals totalDownloads
     * - averageDownloadTimeSeconds is non-negative
     * - collectedAt is non-null
     */
    public CollectorStatistics {
        if (totalDownloads < 0) {
            throw new IllegalArgumentException("totalDownloads cannot be negative");
        }
        
        if (successfulDownloads < 0) {
            throw new IllegalArgumentException("successfulDownloads cannot be negative");
        }
        
        if (failedDownloads < 0) {
            throw new IllegalArgumentException("failedDownloads cannot be negative");
        }
        
        if (successfulDownloads + failedDownloads != totalDownloads) {
            throw new IllegalArgumentException(
                "successfulDownloads (" + successfulDownloads + ") + failedDownloads (" 
                + failedDownloads + ") must equal totalDownloads (" + totalDownloads + ")"
            );
        }
        
        if (totalCandlesCollected < 0) {
            throw new IllegalArgumentException("totalCandlesCollected cannot be negative");
        }
        
        if (averageDownloadTimeSeconds < 0) {
            throw new IllegalArgumentException("averageDownloadTimeSeconds cannot be negative");
        }
        
        Objects.requireNonNull(collectedAt, "collectedAt cannot be null");
    }

    /**
     * Calculates the success rate as a percentage.
     * 
     * @return success rate (0-100), or 0 if no downloads have been recorded
     */
    public double successRatePercent() {
        if (totalDownloads == 0) {
            return 0;
        }
        return (100.0 * successfulDownloads) / totalDownloads;
    }

    /**
     * Calculates the failure rate as a percentage.
     * 
     * @return failure rate (0-100), or 0 if no downloads have been recorded
     */
    public double failureRatePercent() {
        return 100.0 - successRatePercent();
    }

    /**
     * Calculates the average number of candles per successful download.
     * 
     * @return average candles per download, or 0 if no successful downloads
     */
    public double averageCandlesPerDownload() {
        if (successfulDownloads == 0) {
            return 0;
        }
        return (double) totalCandlesCollected / successfulDownloads;
    }

    /**
     * Creates collector statistics with all values initialized to zero.
     * 
     * @return empty CollectorStatistics
     */
    public static CollectorStatistics empty() {
        return new CollectorStatistics(0, 0, 0, 0, 0.0, Instant.now());
    }
}
