package com.guru.researchplatform.collector.provider.binance.archive.model;

import java.math.BigDecimal;

public record BinanceKlineRecord(
        long openTime,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        BigDecimal volume,
        long closeTime,
        BigDecimal quoteAssetVolume,
        long tradeCount,
        BigDecimal takerBuyBaseVolume,
        BigDecimal takerBuyQuoteVolume
) {
}
