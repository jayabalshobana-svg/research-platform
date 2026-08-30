package com.guru.researchplatform.collector.service;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.repository.CandleRepository;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class HistoricalDataImportService {

    private final BinanceCandleLoader candleLoader;
    private final CandleRepository candleRepository;

    public HistoricalDataImportService(
            BinanceCandleLoader candleLoader,
            CandleRepository candleRepository) {

        this.candleLoader = Objects.requireNonNull(candleLoader);
        this.candleRepository = Objects.requireNonNull(candleRepository);
    }

    public int importData(
            Path csvFile,
            Asset asset,
            Timeframe timeframe) {

        Objects.requireNonNull(csvFile);
        Objects.requireNonNull(asset);
        Objects.requireNonNull(timeframe);

        long loadStart = System.currentTimeMillis();

        List<Candle> candles = candleLoader.load(
                csvFile,
                asset,
                timeframe
        );

        System.out.println(
                "Loading completed in: "
                        + (System.currentTimeMillis() - loadStart)
                        + " ms"
        );

        System.out.println(
                "Saving " + candles.size() + " candles..."
        );

        long saveStart = System.currentTimeMillis();

        if (candles.isEmpty()) {
            return 0;
        }

        Candle firstCandle = candles.getFirst();

        boolean alreadyImported = candleRepository.exists(
                firstCandle.exchange(),
                firstCandle.asset(),
                firstCandle.timeframe(),
                firstCandle.openTime()
        );

        if (alreadyImported) {
            System.out.println("Data already exists. Skipping import.");
            return 0;
        }

        candleRepository.saveAll(candles);

        System.out.println(
                "Saving completed in: "
                        + (System.currentTimeMillis() - saveStart)
                        + " ms"
        );
        return candles.size();
    }

    public boolean isAlreadyImported(
            Asset asset,
            Timeframe timeframe,
            YearMonth month
    ) {
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(month, "month cannot be null");

        Instant firstCandleOpenTime = month
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        return candleRepository.exists(
                asset.exchange(),
                asset,
                timeframe,
                firstCandleOpenTime
        );
    }

}