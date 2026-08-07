package com.guru.researchplatform.marketdatacollector.api.dto;

import com.guru.researchplatform.collector.contract.DownloadProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DownloadProgress Tests")
class DownloadProgressTest {

    @Test
    @DisplayName("should create valid download progress")
    void shouldCreateValidDownloadProgress() {
        DownloadProgress progress = new DownloadProgress(50, 100, DownloadProgress.ProgressStatus.IN_PROGRESS);

        assertEquals(50, progress.candlesDownloaded());
        assertEquals(100, progress.totalExpected());
        assertEquals(DownloadProgress.ProgressStatus.IN_PROGRESS, progress.status());
    }

    @Test
    @DisplayName("should calculate correct percentage")
    void shouldCalculateCorrectPercentage() {
        DownloadProgress progress = new DownloadProgress(25, 100, DownloadProgress.ProgressStatus.IN_PROGRESS);

        assertEquals(25.0, progress.percentComplete(), 0.01);
    }

    @Test
    @DisplayName("should calculate percentage for partial progress")
    void shouldCalculatePercentageForPartialProgress() {
        DownloadProgress progress = new DownloadProgress(33, 100, DownloadProgress.ProgressStatus.IN_PROGRESS);

        assertEquals(33.0, progress.percentComplete(), 0.01);
    }

    @Test
    @DisplayName("should identify completed status")
    void shouldIdentifyCompletedStatus() {
        DownloadProgress completed = new DownloadProgress(100, 100, DownloadProgress.ProgressStatus.COMPLETED);
        DownloadProgress pending = new DownloadProgress(0, 100, DownloadProgress.ProgressStatus.PENDING);

        assertTrue(completed.isComplete());
        assertFalse(pending.isComplete());
    }

    @Test
    @DisplayName("should identify in-progress status")
    void shouldIdentifyInProgressStatus() {
        DownloadProgress progress = new DownloadProgress(50, 100, DownloadProgress.ProgressStatus.IN_PROGRESS);
        DownloadProgress failed = new DownloadProgress(50, 100, DownloadProgress.ProgressStatus.FAILED);

        assertTrue(progress.isInProgress());
        assertFalse(failed.isInProgress());
    }

    @Test
    @DisplayName("should identify failed status")
    void shouldIdentifyFailedStatus() {
        DownloadProgress failed = new DownloadProgress(50, 100, DownloadProgress.ProgressStatus.FAILED);
        DownloadProgress completed = new DownloadProgress(100, 100, DownloadProgress.ProgressStatus.COMPLETED);

        assertTrue(failed.isFailed());
        assertFalse(completed.isFailed());
    }

    @Test
    @DisplayName("should identify pending status")
    void shouldIdentifyPendingStatus() {
        DownloadProgress pending = new DownloadProgress(0, 100, DownloadProgress.ProgressStatus.PENDING);
        DownloadProgress progress = new DownloadProgress(50, 100, DownloadProgress.ProgressStatus.IN_PROGRESS);

        assertTrue(pending.isPending());
        assertFalse(progress.isPending());
    }

    @Test
    @DisplayName("should throw when candlesDownloaded is negative")
    void shouldThrowWhenCandlesDownloadedIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadProgress(-1, 100, DownloadProgress.ProgressStatus.IN_PROGRESS)
        );
    }

    @Test
    @DisplayName("should throw when totalExpected is zero or negative")
    void shouldThrowWhenTotalExpectedIsNonPositive() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadProgress(0, 0, DownloadProgress.ProgressStatus.IN_PROGRESS)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new DownloadProgress(0, -1, DownloadProgress.ProgressStatus.IN_PROGRESS)
        );
    }

    @Test
    @DisplayName("should throw when candlesDownloaded exceeds totalExpected")
    void shouldThrowWhenCandlesExceedTotal() {
        assertThrows(IllegalArgumentException.class, () ->
            new DownloadProgress(101, 100, DownloadProgress.ProgressStatus.IN_PROGRESS)
        );
    }

    @Test
    @DisplayName("should throw when status is null")
    void shouldThrowWhenStatusIsNull() {
        assertThrows(NullPointerException.class, () ->
            new DownloadProgress(50, 100, null)
        );
    }

    @Test
    @DisplayName("should allow zero candles downloaded")
    void shouldAllowZeroCandlesDownloaded() {
        DownloadProgress progress = new DownloadProgress(0, 100, DownloadProgress.ProgressStatus.PENDING);

        assertEquals(0, progress.candlesDownloaded());
        assertEquals(0.0, progress.percentComplete());
        assertTrue(progress.isPending());
    }

    @Test
    @DisplayName("should allow equal candles and total")
    void shouldAllowEqualCandlesAndTotal() {
        DownloadProgress progress = new DownloadProgress(100, 100, DownloadProgress.ProgressStatus.COMPLETED);

        assertEquals(100, progress.candlesDownloaded());
        assertEquals(100.0, progress.percentComplete());
        assertTrue(progress.isComplete());
    }
}
