package com.guru.researchplatform.common.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the public {@link Exchange} contract.
 */
class ExchangeTest {
    @Test
    void containsTheRequestedExchanges() {
        assertEquals(6, Exchange.values().length);
        assertEquals(Exchange.BINANCE, Exchange.valueOf("BINANCE"));
        assertEquals(Exchange.NASDAQ, Exchange.valueOf("NASDAQ"));
    }
}
