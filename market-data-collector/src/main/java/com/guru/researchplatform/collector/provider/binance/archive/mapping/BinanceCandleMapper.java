package com.guru.researchplatform.collector.provider.binance.archive.mapping;

import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;

import java.time.Instant;
import java.util.Objects;

public class BinanceCandleMapper {

    public Candle map(
            BinanceKlineRecord record,
            Asset asset,
            Timeframe timeframe
    ) {
        Objects.requireNonNull(record, "record cannot be null");
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");

        Instant openTime = toInstant(record.openTime());

        Instant closeTime = openTime
                .plus(timeframe.duration())
                .minusMillis(1);

        return new Candle(
                Exchange.BINANCE,
                asset,
                timeframe,
                openTime,
                closeTime,
                record.open(),
                record.high(),
                record.low(),
                record.close(),
                record.volume(),
                record.quoteAssetVolume(),
                record.takerBuyBaseVolume(),
                record.takerBuyQuoteVolume(),
                record.tradeCount()
        );
    }

    private Instant toInstant(long timestamp) {

        // Millisecond timestamps are approximately 13 digits.
        // Microsecond timestamps are approximately 16 digits.
        if (timestamp >= 100_000_000_000_000L) {
            return Instant.ofEpochSecond(
                    timestamp / 1_000_000,
                    (timestamp % 1_000_000) * 1_000
            );
        }

        return Instant.ofEpochMilli(timestamp);
    }
}