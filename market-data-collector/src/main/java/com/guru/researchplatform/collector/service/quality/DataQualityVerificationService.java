package com.guru.researchplatform.collector.service.quality;

import com.guru.researchplatform.collector.repository.CandleRepository;

import java.util.List;
import java.util.Objects;

public class DataQualityVerificationService {

    private final CandleRepository candleRepository;

    public DataQualityVerificationService(
            CandleRepository candleRepository
    ) {
        this.candleRepository = Objects.requireNonNull(
                candleRepository,
                "candleRepository cannot be null"
        );
    }

    public DataQualityReport verify() {

        long totalCandles = candleRepository.countAll();

        long invalidOhlcCount =
                candleRepository.countInvalidOhlc();

        long duplicateCount =
                candleRepository.countDuplicates();

        long invalidOpenTimeCount =
                candleRepository.countInvalidOpenTimes();

        // Load gaps once and reuse the result
        List<TimeGap> timeGaps =
                candleRepository.findTimeGaps();

        long totalMissingCandles = timeGaps.stream()
                .mapToLong(TimeGap::missingCandles)
                .sum();

        return new DataQualityReport(
                totalCandles,
                invalidOhlcCount,
                duplicateCount,
                timeGaps.size(),
                invalidOpenTimeCount,
                totalMissingCandles
        );
    }

    public List<TimeGap> findTimeGaps() {
        return candleRepository.findTimeGaps();
    }

    public List<InvalidOpenTimeCandle> findInvalidOpenTimeSamples(
            int limit
    ) {
        return candleRepository.findInvalidOpenTimeSamples(limit);
    }

    public List<OpenTimeOffset> findOpenTimeOffsetDistribution() {
        return candleRepository.findOpenTimeOffsetDistribution();
    }
}