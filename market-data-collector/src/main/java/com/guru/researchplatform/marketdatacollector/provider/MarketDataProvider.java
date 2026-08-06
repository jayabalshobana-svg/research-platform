package com.guru.researchplatform.marketdatacollector.provider;

import com.guru.researchplatform.common.dto.DownloadRequest;
import com.guru.researchplatform.common.dto.DownloadResult;

/**
 * Defines the public contract for downloading historical market candles.
 */
public interface MarketDataProvider {
    /**
     * Downloads historical candles for a requested market and time range.
     *
     * @param request the download request
     * @return a summary of the download outcome
     */
    DownloadResult downloadHistoricalCandles(DownloadRequest request);
}
