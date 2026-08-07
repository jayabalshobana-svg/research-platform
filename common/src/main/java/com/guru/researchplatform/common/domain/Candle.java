package com.guru.researchplatform.common.domain;

import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.Timeframe;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * An immutable record representing a candlestick (OHLCV) in financial markets.
 * All prices use BigDecimal for precision. Never uses double.
 */
public record Candle(
    Exchange exchange,
    Asset asset,
    Timeframe timeframe,
    Instant openTime,
    Instant closeTime,
    BigDecimal open,
    BigDecimal high,
    BigDecimal low,
    BigDecimal close,
    BigDecimal volume,
    BigDecimal quoteAssetVolume,
    BigDecimal takerBuyBaseVolume,
    BigDecimal takerBuyQuoteVolume,
    long tradeCount
) {
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * Compact constructor for validation.
     * Ensures all fields meet domain requirements.
     */
    public Candle {
        Objects.requireNonNull(exchange, "exchange cannot be null");
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(openTime, "openTime cannot be null");
        Objects.requireNonNull(closeTime, "closeTime cannot be null");
        Objects.requireNonNull(open, "open cannot be null");
        Objects.requireNonNull(high, "high cannot be null");
        Objects.requireNonNull(low, "low cannot be null");
        Objects.requireNonNull(close, "close cannot be null");
        Objects.requireNonNull(volume, "volume cannot be null");
        Objects.requireNonNull(quoteAssetVolume, "quoteAssetVolume cannot be null");
        Objects.requireNonNull(takerBuyBaseVolume, "takerBuyBaseVolume cannot be null");
        Objects.requireNonNull(takerBuyQuoteVolume, "takerBuyQuoteVolume cannot be null");

        // Time validation
        if (closeTime.isBefore(openTime)) {
            throw new IllegalArgumentException("closeTime cannot be before openTime");
        }

        // Price validation - no negative prices
        if (open.signum() < 0) {
            throw new IllegalArgumentException("open price cannot be negative");
        }
        if (high.signum() < 0) {
            throw new IllegalArgumentException("high price cannot be negative");
        }
        if (low.signum() < 0) {
            throw new IllegalArgumentException("low price cannot be negative");
        }
        if (close.signum() < 0) {
            throw new IllegalArgumentException("close price cannot be negative");
        }

        // OHLC rules validation
        if (high.compareTo(open) < 0) {
            throw new IllegalArgumentException("high must be >= open");
        }
        if (high.compareTo(close) < 0) {
            throw new IllegalArgumentException("high must be >= close");
        }
        if (high.compareTo(low) < 0) {
            throw new IllegalArgumentException("high must be >= low");
        }
        if (low.compareTo(open) > 0) {
            throw new IllegalArgumentException("low must be <= open");
        }
        if (low.compareTo(close) > 0) {
            throw new IllegalArgumentException("low must be <= close");
        }

        // Volume validation
        if (volume.signum() < 0) {
            throw new IllegalArgumentException("volume cannot be negative");
        }
        if (quoteAssetVolume.signum() < 0) {
            throw new IllegalArgumentException("quoteAssetVolume cannot be negative");
        }
        if (takerBuyBaseVolume.signum() < 0) {
            throw new IllegalArgumentException("takerBuyBaseVolume cannot be negative");
        }
        if (takerBuyQuoteVolume.signum() < 0) {
            throw new IllegalArgumentException("takerBuyQuoteVolume cannot be negative");
        }

        // Trade count validation
        if (tradeCount < 0) {
            throw new IllegalArgumentException("tradeCount cannot be negative");
        }
    }

    /**
     * Determines if the candle is bullish (close > open).
     * @return true if close is greater than open
     */
    public boolean isBullish() {
        return close.compareTo(open) > 0;
    }

    /**
     * Determines if the candle is bearish (close < open).
     * @return true if close is less than open
     */
    public boolean isBearish() {
        return close.compareTo(open) < 0;
    }

    /**
     * Determines if the candle is a doji (open == close).
     * @return true if open equals close
     */
    public boolean isDoji() {
        return open.compareTo(close) == 0;
    }

    /**
     * Calculates the body size (absolute difference between close and open).
     * @return abs(close - open)
     */
    public BigDecimal bodySize() {
        return close.subtract(open).abs();
    }

    /**
     * Calculates the total price range of the candle.
     * @return high - low
     */
    public BigDecimal range() {
        return high.subtract(low);
    }

    /**
     * Calculates the upper wick size.
     * @return high - max(open, close)
     */
    public BigDecimal upperWick() {
        BigDecimal maxOpenClose = open.compareTo(close) > 0 ? open : close;
        return high.subtract(maxOpenClose);
    }

    /**
     * Calculates the lower wick size.
     * @return min(open, close) - low
     */
    public BigDecimal lowerWick() {
        BigDecimal minOpenClose = open.compareTo(close) < 0 ? open : close;
        return minOpenClose.subtract(low);
    }

    /**
     * Calculates the typical price (often used in EMA and VWAP calculations).
     * @return (high + low + close) / 3
     */
    public BigDecimal typicalPrice() {
        return high.add(low).add(close).divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the mid price.
     * @return (high + low) / 2
     */
    public BigDecimal midPrice() {
        return high.add(low).divide(BigDecimal.TWO, 8, RoundingMode.HALF_UP);
    }

    /**
     * Checks if the candle has trading volume.
     * @return true if volume > 0
     */
    public boolean hasVolume() {
        return volume.compareTo(ZERO) > 0;
    }
}
