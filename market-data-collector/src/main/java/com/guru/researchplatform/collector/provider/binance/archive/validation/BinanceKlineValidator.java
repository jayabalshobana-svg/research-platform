package com.guru.researchplatform.collector.provider.binance.archive.validation;

import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BinanceKlineValidator {

    public BinanceKlineValidationResult validate(
            List<BinanceKlineRecord> records) {

        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException(
                    "Records cannot be null or empty"
            );
        }

        long totalRows = records.size();

        long duplicateTimestamps = 0;
        long outOfOrderRows = 0;
        long missingIntervals = 0;
        long invalidOhlcRows = 0;
        long negativeVolumeRows = 0;
        long negativeTradeCountRows = 0;

        Set<Long> timestamps = new HashSet<>();

        Long previousOpenTime = null;

        for (BinanceKlineRecord record : records) {

            long currentOpenTime = record.openTime();

            // 1. Duplicate timestamp
            if (!timestamps.add(currentOpenTime)) {
                duplicateTimestamps++;
            }

            // 2. Ordering / missing interval
            if (previousOpenTime != null) {

                if (currentOpenTime < previousOpenTime) {
                    outOfOrderRows++;
                }

                long difference =
                        currentOpenTime - previousOpenTime;

                long oneMinute =
                        Duration.ofMinutes(1).toMillis();

                if (difference > oneMinute) {
                    missingIntervals +=
                            (difference / oneMinute) - 1;
                }
            }

            previousOpenTime = currentOpenTime;

            // 3. OHLC validation
            boolean invalidOhlc =
                    record.high().compareTo(record.open()) < 0
                            || record.high().compareTo(record.close()) < 0
                            || record.high().compareTo(record.low()) < 0
                            || record.low().compareTo(record.open()) > 0
                            || record.low().compareTo(record.close()) > 0;

            if (invalidOhlc) {
                invalidOhlcRows++;
            }

            // 4. Volume validation
            if (record.volume().signum() < 0
                    || record.quoteAssetVolume().signum() < 0
                    || record.takerBuyBaseVolume().signum() < 0
                    || record.takerBuyQuoteVolume().signum() < 0) {

                negativeVolumeRows++;
            }

            // 5. Trade count validation
            if (record.tradeCount() < 0) {
                negativeTradeCountRows++;
            }
        }

        long invalidRows =
                duplicateTimestamps
                        + outOfOrderRows
                        + invalidOhlcRows
                        + negativeVolumeRows
                        + negativeTradeCountRows;

        long validRows = totalRows - invalidRows;

        return new BinanceKlineValidationResult(
                totalRows,
                validRows,
                invalidRows,
                duplicateTimestamps,
                outOfOrderRows,
                missingIntervals,
                invalidOhlcRows,
                negativeVolumeRows,
                negativeTradeCountRows
        );
    }
}