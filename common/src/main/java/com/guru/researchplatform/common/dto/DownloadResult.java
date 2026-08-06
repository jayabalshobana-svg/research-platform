package com.guru.researchplatform.common.dto;

import com.guru.researchplatform.common.enums.DownloadStatus;

import java.time.Duration;
import java.util.List;

/**
 * Summarizes the outcome of a market-data download operation.
 *
 * @param status the final download status
 * @param downloaded the number of downloaded candles
 * @param duplicates the number of duplicate candles encountered
 * @param skipped the number of skipped candles
 * @param duration the time taken by the operation
 * @param warnings non-fatal warnings raised during the operation
 */
public record DownloadResult(
        DownloadStatus status,
        long downloaded,
        long duplicates,
        long skipped,
        Duration duration,
        List<String> warnings) {
}
