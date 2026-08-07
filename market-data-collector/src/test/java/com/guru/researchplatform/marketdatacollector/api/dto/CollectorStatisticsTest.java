package com.guru.researchplatform.marketdatacollector.api.dto;

import com.guru.researchplatform.collector.contract.CollectorStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CollectorStatistics Tests")
class CollectorStatisticsTest {

    @Test
    @DisplayName("should create valid collector statistics")
    void shouldCreateValidCollectorStatistics() {
        CollectorStatistics stats = new CollectorStatistics(100, 90, 10, 5000, 0.5, Instant.now());

        assertEquals(100, stats.totalDownloads());
        assertEquals(90, stats.successfulDownloads());
        assertEquals(10, stats.failedDownloads());
        assertEquals(5000, stats.totalCandlesCollected());
        assertEquals(0.5, stats.averageDownloadTimeSeconds());
    }

    @Test
    @DisplayName("should calculate correct success rate")
    void shouldCalculateCorrectSuccessRate() {
        CollectorStatistics stats = new CollectorStatistics(100, 90, 10, 5000, 0.5, Instant.now());

        assertEquals(90.0, stats.successRatePercent(), 0.01);
    }

    @Test
    @DisplayName("should calculate correct failure rate")
    void shouldCalculateCorrectFailureRate() {
        CollectorStatistics stats = new CollectorStatistics(100, 90, 10, 5000, 0.5, Instant.now());

        assertEquals(10.0, stats.failureRatePercent(), 0.01);
    }

    @Test
    @DisplayName("should calculate average candles per download")
    void shouldCalculateAverageCandles() {
        CollectorStatistics stats = new CollectorStatistics(100, 50, 50, 5000, 0.5, Instant.now());

        assertEquals(100.0, stats.averageCandlesPerDownload(), 0.01);
    }

    @Test
    @DisplayName("should return 0% success rate when no downloads")
    void shouldReturn0SuccessWhenNoDownloads() {
        CollectorStatistics stats = CollectorStatistics.empty();

        assertEquals(0.0, stats.successRatePercent());
        assertEquals(0.0, stats.averageCandlesPerDownload());
    }

    @Test
    @DisplayName("should create empty statistics")
    void shouldCreateEmptyStatistics() {
        CollectorStatistics stats = CollectorStatistics.empty();

        assertEquals(0, stats.totalDownloads());
        assertEquals(0, stats.successfulDownloads());
        assertEquals(0, stats.failedDownloads());
        assertEquals(0, stats.totalCandlesCollected());
        assertEquals(0.0, stats.averageDownloadTimeSeconds());
    }

    @Test
    @DisplayName("should throw when successful + failed != total")
    void shouldThrowWhenCountsMismatch() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorStatistics(100, 90, 5, 5000, 0.5, Instant.now())
        );
    }

    @Test
    @DisplayName("should throw when totalDownloads is negative")
    void shouldThrowWhenTotalDownloadsIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorStatistics(-1, 0, 0, 0, 0.0, Instant.now())
        );
    }

    @Test
    @DisplayName("should throw when successfulDownloads is negative")
    void shouldThrowWhenSuccessfulDownloadsIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorStatistics(100, -1, 100, 5000, 0.5, Instant.now())
        );
    }

    @Test
    @DisplayName("should throw when failedDownloads is negative")
    void shouldThrowWhenFailedDownloadsIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorStatistics(100, 90, -1, 5000, 0.5, Instant.now())
        );
    }

    @Test
    @DisplayName("should throw when totalCandlesCollected is negative")
    void shouldThrowWhenTotalCandlesIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorStatistics(100, 90, 10, -1, 0.5, Instant.now())
        );
    }

    @Test
    @DisplayName("should throw when averageDownloadTimeSeconds is negative")
    void shouldThrowWhenAverageTimeIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectorStatistics(100, 90, 10, 5000, -0.1, Instant.now())
        );
    }

    @Test
    @DisplayName("should throw when collectedAt is null")
    void shouldThrowWhenCollectedAtIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CollectorStatistics(100, 90, 10, 5000, 0.5, null)
        );
    }

    @Test
    @DisplayName("should handle perfect success rate")
    void shouldHandlePerfectSuccessRate() {
        CollectorStatistics stats = new CollectorStatistics(100, 100, 0, 10000, 1.0, Instant.now());

        assertEquals(100.0, stats.successRatePercent());
        assertEquals(0.0, stats.failureRatePercent());
    }

    @Test
    @DisplayName("should handle zero candles collected")
    void shouldHandleZeroCandlesCollected() {
        CollectorStatistics stats = new CollectorStatistics(10, 10, 0, 0, 0.1, Instant.now());

        assertEquals(0.0, stats.averageCandlesPerDownload());
    }

    @Test
    @DisplayName("should allow zero successful downloads with zero candles")
    void shouldAllowZeroSuccessfulDownloads() {
        CollectorStatistics stats = new CollectorStatistics(10, 0, 10, 0, 0.5, Instant.now());

        assertEquals(0.0, stats.successRatePercent());
        assertEquals(100.0, stats.failureRatePercent());
        assertEquals(0.0, stats.averageCandlesPerDownload());
    }
}
