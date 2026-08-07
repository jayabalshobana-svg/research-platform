package com.guru.researchplatform.common.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MarketType Enum Tests")
class MarketTypeTest {

    @Test
    @DisplayName("should have all expected enum values")
    void testEnumValuesExist() {
        assertNotNull(MarketType.CRYPTO);
        assertNotNull(MarketType.STOCK);
        assertNotNull(MarketType.FOREX);
        assertNotNull(MarketType.COMMODITY);
        assertNotNull(MarketType.INDEX);
        assertNotNull(MarketType.ETF);
        assertNotNull(MarketType.FUTURES);
        assertNotNull(MarketType.OPTIONS);
    }

    @Test
    @DisplayName("should have exactly 8 enum values")
    void testEnumValuesCount() {
        MarketType[] values = MarketType.values();
        assertEquals(8, values.length, "MarketType should have exactly 8 values");
    }

    @Test
    @DisplayName("should convert string to enum using valueOf()")
    void testValueOf() {
        assertEquals(MarketType.CRYPTO, MarketType.valueOf("CRYPTO"));
        assertEquals(MarketType.STOCK, MarketType.valueOf("STOCK"));
        assertEquals(MarketType.FOREX, MarketType.valueOf("FOREX"));
        assertEquals(MarketType.COMMODITY, MarketType.valueOf("COMMODITY"));
        assertEquals(MarketType.INDEX, MarketType.valueOf("INDEX"));
        assertEquals(MarketType.ETF, MarketType.valueOf("ETF"));
        assertEquals(MarketType.FUTURES, MarketType.valueOf("FUTURES"));
        assertEquals(MarketType.OPTIONS, MarketType.valueOf("OPTIONS"));
    }

    @Test
    @DisplayName("should throw IllegalArgumentException for invalid enum constant")
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> MarketType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> MarketType.valueOf("crypto"));
        assertThrows(IllegalArgumentException.class, () -> MarketType.valueOf(""));
    }

    @Test
    @DisplayName("should have no duplicate values")
    void testNoDuplicateValues() {
        MarketType[] values = MarketType.values();
        Set<MarketType> uniqueValues = new HashSet<>(Arrays.asList(values));
        
        assertEquals(values.length, uniqueValues.size(), 
            "All enum values should be unique");
    }

    @Test
    @DisplayName("should have correct ordinal values")
    void testOrdinalValues() {
        MarketType[] values = MarketType.values();
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, values[i].ordinal(), 
                "Ordinal for " + values[i].name() + " should be " + i);
        }
    }

    @Test
    @DisplayName("valueOf() should return correct enum for all values")
    void testValueOfAllEnums() {
        for (MarketType marketType : MarketType.values()) {
            assertEquals(marketType, MarketType.valueOf(marketType.name()),
                "valueOf() should return the same enum for " + marketType.name());
        }
    }
}
