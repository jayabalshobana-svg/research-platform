package com.guru.researchplatform.marketdatacollector.api.dto;

import java.util.Objects;

/**
 * Represents the progress of an ongoing download operation.
 * 
 * Immutable record tracking how many candles have been retrieved
 * and the total expected count.
 */
public record DownloadProgress(
    long candlesDownloaded,
    long totalExpected,
    ProgressStatus status
) {
    /**
     * Represents the status of a download operation.
     */
    public enum ProgressStatus {
        /** Download has not started yet */
        PENDING,
        /** Download is actively retrieving data */
        IN_PROGRESS,
        /** Download has completed successfully */
        COMPLETED,
        /** Download failed or was cancelled */
        FAILED
    }

    /**
     * Compact constructor for validation.
     * 
     * Ensures:
     * - candlesDownloaded is not negative
     * - totalExpected is positive
     * - candlesDownloaded does not exceed totalExpected
     * - status is non-null
     */
    public DownloadProgress {
        if (candlesDownloaded < 0) {
            throw new IllegalArgumentException("candlesDownloaded cannot be negative");
        }
        
        if (totalExpected <= 0) {
            throw new IllegalArgumentException("totalExpected must be positive");
        }
        
        if (candlesDownloaded > totalExpected) {
            throw new IllegalArgumentException("candlesDownloaded cannot exceed totalExpected");
        }
        
        Objects.requireNonNull(status, "status cannot be null");
    }

    /**
     * Calculates the percentage progress.
     * 
     * @return progress as a percentage (0-100)
     */
    public double percentComplete() {
        if (totalExpected == 0) {
            return 0;
        }
        return (100.0 * candlesDownloaded) / totalExpected;
    }

    /**
     * Determines if the download has completed.
     * 
     * @return true if status is COMPLETED, false otherwise
     */
    public boolean isComplete() {
        return status == ProgressStatus.COMPLETED;
    }

    /**
     * Determines if the download is in progress.
     * 
     * @return true if status is IN_PROGRESS, false otherwise
     */
    public boolean isInProgress() {
        return status == ProgressStatus.IN_PROGRESS;
    }

    /**
     * Determines if the download has failed.
     * 
     * @return true if status is FAILED, false otherwise
     */
    public boolean isFailed() {
        return status == ProgressStatus.FAILED;
    }

    /**
     * Determines if the download is pending (not yet started).
     * 
     * @return true if status is PENDING, false otherwise
     */
    public boolean isPending() {
        return status == ProgressStatus.PENDING;
    }
}
