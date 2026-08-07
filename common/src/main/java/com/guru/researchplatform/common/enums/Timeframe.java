package com.guru.researchplatform.common.enums;

import java.time.Duration;
import java.util.Objects;

/**
 * Enumerates the candle aggregation intervals supported by market-data requests.
 * 
 * Each timeframe provides:
 * - An API value (e.g., "1m" for ONE_MINUTE) used in external integrations
 * - A display name (e.g., "1 Minute") for user-facing interfaces
 * - A duration (e.g., Duration.ofMinutes(1)) for time calculations
 */
public enum Timeframe {
    ONE_MINUTE("1m", "1 Minute", Duration.ofMinutes(1)),
    THREE_MINUTES("3m", "3 Minutes", Duration.ofMinutes(3)),
    FIVE_MINUTES("5m", "5 Minutes", Duration.ofMinutes(5)),
    FIFTEEN_MINUTES("15m", "15 Minutes", Duration.ofMinutes(15)),
    THIRTY_MINUTES("30m", "30 Minutes", Duration.ofMinutes(30)),
    ONE_HOUR("1h", "1 Hour", Duration.ofHours(1)),
    FOUR_HOURS("4h", "4 Hours", Duration.ofHours(4)),
    ONE_DAY("1d", "1 Day", Duration.ofDays(1)),
    ONE_WEEK("1w", "1 Week", Duration.ofDays(7));

    private final String apiValue;
    private final String displayName;
    private final Duration duration;

    /**
     * Constructs a Timeframe with the specified API value, display name, and duration.
     * 
     * @param apiValue the API representation (e.g., "1m", "1h", "1d")
     * @param displayName the human-readable name (e.g., "1 Minute", "1 Hour")
     * @param duration the time duration this timeframe represents
     * @throws NullPointerException if any parameter is null
     */
    Timeframe(String apiValue, String displayName, Duration duration) {
        this.apiValue = Objects.requireNonNull(apiValue, "apiValue cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null");
        this.duration = Objects.requireNonNull(duration, "duration cannot be null");
    }

    /**
     * Returns the API representation of this timeframe.
     * 
     * @return the API value (e.g., "1m", "1h", "1d")
     */
    public String apiValue() {
        return apiValue;
    }

    /**
     * Returns the human-readable display name for this timeframe.
     * 
     * @return the display name (e.g., "1 Minute", "1 Hour", "1 Week")
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Returns the duration this timeframe represents.
     * 
     * @return the duration (e.g., Duration.ofMinutes(1), Duration.ofHours(1))
     */
    public Duration duration() {
        return duration;
    }

    /**
     * Determines if this timeframe is intraday (less than 1 day).
     * 
     * @return true if duration is less than 1 day, false otherwise
     */
    public boolean isIntraday() {
        return duration.compareTo(Duration.ofDays(1)) < 0;
    }

    /**
     * Determines if this timeframe is hourly.
     * 
     * @return true if this is ONE_HOUR or FOUR_HOURS, false otherwise
     */
    public boolean isHourly() {
        return this == ONE_HOUR || this == FOUR_HOURS;
    }

    /**
     * Determines if this timeframe is daily.
     * 
     * @return true if this is ONE_DAY, false otherwise
     */
    public boolean isDaily() {
        return this == ONE_DAY;
    }

    /**
     * Determines if this timeframe is weekly.
     * 
     * @return true if this is ONE_WEEK, false otherwise
     */
    public boolean isWeekly() {
        return this == ONE_WEEK;
    }

    /**
     * Determines if this timeframe is monthly.
     * 
     * @return false - monthly timeframes are not yet supported
     */
    public boolean isMonthly() {
        return false;
    }
}
