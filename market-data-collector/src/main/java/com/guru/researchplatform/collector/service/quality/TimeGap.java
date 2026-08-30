package com.guru.researchplatform.collector.service.quality;

import java.time.Instant;

public record TimeGap(
        Instant previousOpenTime,
        Instant nextOpenTime,
        long missingCandles
) {
}