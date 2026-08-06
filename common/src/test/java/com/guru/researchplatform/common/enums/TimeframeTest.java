package com.guru.researchplatform.common.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the public {@link Timeframe} contract.
 */
class TimeframeTest {
    @Test
    void containsTheRequestedTimeframes() {
        assertEquals(9, Timeframe.values().length);
        assertEquals(Timeframe.ONE_MINUTE, Timeframe.valueOf("ONE_MINUTE"));
        assertEquals(Timeframe.ONE_WEEK, Timeframe.valueOf("ONE_WEEK"));
    }
}
