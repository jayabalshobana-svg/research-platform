package com.guru.researchplatform.collector.repository;

import com.guru.researchplatform.collector.service.quality.InvalidOpenTimeCandle;
import com.guru.researchplatform.collector.service.quality.OpenTimeOffset;
import com.guru.researchplatform.collector.service.quality.TimeGap;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.Instant;
import java.util.List;

public interface CandleRepository {

    void initialize();

    void saveAll(List<Candle> candles);

    boolean exists(
            Exchange exchange,
            Asset asset,
            Timeframe timeframe,
            Instant openTime
    );

    long countAll();

    long countInvalidOhlc();

    long countDuplicates();

    long countTimeGaps();

    List<TimeGap> findTimeGaps();

    long countInvalidOpenTimes();

    List<InvalidOpenTimeCandle> findInvalidOpenTimeSamples(
            int limit
    );

    List<OpenTimeOffset> findOpenTimeOffsetDistribution();
}