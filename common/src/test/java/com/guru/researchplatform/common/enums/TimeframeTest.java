package com.guru.researchplatform.common.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the public {@link Timeframe} contract.
 */
@DisplayName("Timeframe Enum Tests")
class TimeframeTest {
    
    @Test
    @DisplayName("should contain exactly 9 timeframe values")
    void containsTheRequestedTimeframes() {
        assertEquals(9, Timeframe.values().length);
        assertEquals(Timeframe.ONE_MINUTE, Timeframe.valueOf("ONE_MINUTE"));
        assertEquals(Timeframe.ONE_WEEK, Timeframe.valueOf("ONE_WEEK"));
    }

    @Test
    @DisplayName("should have correct apiValue for each timeframe")
    void shouldHaveCorrectApiValues() {
        assertEquals("1m", Timeframe.ONE_MINUTE.apiValue());
        assertEquals("3m", Timeframe.THREE_MINUTES.apiValue());
        assertEquals("5m", Timeframe.FIVE_MINUTES.apiValue());
        assertEquals("15m", Timeframe.FIFTEEN_MINUTES.apiValue());
        assertEquals("30m", Timeframe.THIRTY_MINUTES.apiValue());
        assertEquals("1h", Timeframe.ONE_HOUR.apiValue());
        assertEquals("4h", Timeframe.FOUR_HOURS.apiValue());
        assertEquals("1d", Timeframe.ONE_DAY.apiValue());
        assertEquals("1w", Timeframe.ONE_WEEK.apiValue());
    }

    @Test
    @DisplayName("should have correct displayName for each timeframe")
    void shouldHaveCorrectDisplayNames() {
        assertEquals("1 Minute", Timeframe.ONE_MINUTE.displayName());
        assertEquals("3 Minutes", Timeframe.THREE_MINUTES.displayName());
        assertEquals("5 Minutes", Timeframe.FIVE_MINUTES.displayName());
        assertEquals("15 Minutes", Timeframe.FIFTEEN_MINUTES.displayName());
        assertEquals("30 Minutes", Timeframe.THIRTY_MINUTES.displayName());
        assertEquals("1 Hour", Timeframe.ONE_HOUR.displayName());
        assertEquals("4 Hours", Timeframe.FOUR_HOURS.displayName());
        assertEquals("1 Day", Timeframe.ONE_DAY.displayName());
        assertEquals("1 Week", Timeframe.ONE_WEEK.displayName());
    }

    @Test
    @DisplayName("should have correct duration for each timeframe")
    void shouldHaveCorrectDurations() {
        assertEquals(Duration.ofMinutes(1), Timeframe.ONE_MINUTE.duration());
        assertEquals(Duration.ofMinutes(3), Timeframe.THREE_MINUTES.duration());
        assertEquals(Duration.ofMinutes(5), Timeframe.FIVE_MINUTES.duration());
        assertEquals(Duration.ofMinutes(15), Timeframe.FIFTEEN_MINUTES.duration());
        assertEquals(Duration.ofMinutes(30), Timeframe.THIRTY_MINUTES.duration());
        assertEquals(Duration.ofHours(1), Timeframe.ONE_HOUR.duration());
        assertEquals(Duration.ofHours(4), Timeframe.FOUR_HOURS.duration());
        assertEquals(Duration.ofDays(1), Timeframe.ONE_DAY.duration());
        assertEquals(Duration.ofDays(7), Timeframe.ONE_WEEK.duration());
    }

    @Test
    @DisplayName("should correctly identify intraday timeframes")
    void shouldIdentifyIntradayTimeframes() {
        assertTrue(Timeframe.ONE_MINUTE.isIntraday());
        assertTrue(Timeframe.THREE_MINUTES.isIntraday());
        assertTrue(Timeframe.FIVE_MINUTES.isIntraday());
        assertTrue(Timeframe.FIFTEEN_MINUTES.isIntraday());
        assertTrue(Timeframe.THIRTY_MINUTES.isIntraday());
        assertTrue(Timeframe.ONE_HOUR.isIntraday());
        assertTrue(Timeframe.FOUR_HOURS.isIntraday());
        assertFalse(Timeframe.ONE_DAY.isIntraday());
        assertFalse(Timeframe.ONE_WEEK.isIntraday());
    }

    @Test
    @DisplayName("should correctly identify hourly timeframes")
    void shouldIdentifyHourlyTimeframes() {
        assertFalse(Timeframe.ONE_MINUTE.isHourly());
        assertFalse(Timeframe.THREE_MINUTES.isHourly());
        assertFalse(Timeframe.FIVE_MINUTES.isHourly());
        assertFalse(Timeframe.FIFTEEN_MINUTES.isHourly());
        assertFalse(Timeframe.THIRTY_MINUTES.isHourly());
        assertTrue(Timeframe.ONE_HOUR.isHourly());
        assertTrue(Timeframe.FOUR_HOURS.isHourly());
        assertFalse(Timeframe.ONE_DAY.isHourly());
        assertFalse(Timeframe.ONE_WEEK.isHourly());
    }

    @Test
    @DisplayName("should correctly identify daily timeframe")
    void shouldIdentifyDailyTimeframe() {
        assertFalse(Timeframe.ONE_MINUTE.isDaily());
        assertFalse(Timeframe.THREE_MINUTES.isDaily());
        assertFalse(Timeframe.FIVE_MINUTES.isDaily());
        assertFalse(Timeframe.FIFTEEN_MINUTES.isDaily());
        assertFalse(Timeframe.THIRTY_MINUTES.isDaily());
        assertFalse(Timeframe.ONE_HOUR.isDaily());
        assertFalse(Timeframe.FOUR_HOURS.isDaily());
        assertTrue(Timeframe.ONE_DAY.isDaily());
        assertFalse(Timeframe.ONE_WEEK.isDaily());
    }

    @Test
    @DisplayName("should correctly identify weekly timeframe")
    void shouldIdentifyWeeklyTimeframe() {
        assertFalse(Timeframe.ONE_MINUTE.isWeekly());
        assertFalse(Timeframe.THREE_MINUTES.isWeekly());
        assertFalse(Timeframe.FIVE_MINUTES.isWeekly());
        assertFalse(Timeframe.FIFTEEN_MINUTES.isWeekly());
        assertFalse(Timeframe.THIRTY_MINUTES.isWeekly());
        assertFalse(Timeframe.ONE_HOUR.isWeekly());
        assertFalse(Timeframe.FOUR_HOURS.isWeekly());
        assertFalse(Timeframe.ONE_DAY.isWeekly());
        assertTrue(Timeframe.ONE_WEEK.isWeekly());
    }

    @Test
    @DisplayName("should return false for isMonthly() as monthly not supported")
    void shouldReturnFalseForMonthly() {
        for (Timeframe timeframe : Timeframe.values()) {
            assertFalse(timeframe.isMonthly(), 
                timeframe.name() + " should return false for isMonthly()");
        }
    }

    @Test
    @DisplayName("should maintain backward compatibility with valueOf")
    void shouldMaintainBackwardCompatibility() {
        for (Timeframe timeframe : Timeframe.values()) {
            assertEquals(timeframe, Timeframe.valueOf(timeframe.name()),
                "valueOf() should work for " + timeframe.name());
        }
    }
}
