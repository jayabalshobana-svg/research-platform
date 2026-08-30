package com.guru.researchplatform.collector.service.quality;

import java.time.Instant;

public record InvalidOpenTimeCandle(
        Instant openTime,
        Instant closeTime
) {
}