package com.guru.researchplatform.marketdatacollector.provider;

import com.guru.researchplatform.common.dto.DownloadRequest;
import com.guru.researchplatform.common.dto.DownloadResult;

/**
 * Provides the Binance-specific market-data provider contract implementation.
 */
public class BinanceMarketDataProvider implements MarketDataProvider {
    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException because Binance integration is not implemented
     */
    @Override
    public DownloadResult downloadHistoricalCandles(DownloadRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
