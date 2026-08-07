package com.guru.researchplatform.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AssetStatus Enum Tests")
class AssetStatusTest {

    @Test
    @DisplayName("should have all expected enum values")
    void testEnumValuesExist() {
        assertNotNull(AssetStatus.ACTIVE);
        assertNotNull(AssetStatus.INACTIVE);
        assertNotNull(AssetStatus.DELISTED);
        assertNotNull(AssetStatus.SUSPENDED);
    }

    @Test
    @DisplayName("should have exactly 4 enum values")
    void testEnumValuesCount() {
        MarketType[] values = MarketType.values();
        assertEquals(4, values.length, "AssetStatus should have exactly 4 values");
    }

    @Test
    @DisplayName("should convert string to enum using valueOf()")
    void testValueOf() {
        assertEquals(AssetStatus.ACTIVE, AssetStatus.valueOf("ACTIVE"));
        assertEquals(AssetStatus.INACTIVE, AssetStatus.valueOf("INACTIVE"));
        assertEquals(AssetStatus.DELISTED, AssetStatus.valueOf("DELISTED"));
        assertEquals(AssetStatus.SUSPENDED, AssetStatus.valueOf("SUSPENDED"));

    }

    @Test
    @DisplayName("should throw IllegalArgumentException for invalid enum constant")
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> MarketType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> MarketType.valueOf("EXPIRED"));
        assertThrows(IllegalArgumentException.class, () -> MarketType.valueOf(""));
    }

    @Test
    @DisplayName("should have no duplicate values")
    void testNoDuplicateValues() {
        AssetStatus[] values = AssetStatus.values();
        Set<AssetStatus> uniqueValues = new HashSet<>(Arrays.asList(values));
        
        assertEquals(values.length, uniqueValues.size(), 
            "All enum values should be unique");
    }

    @Test
    @DisplayName("should have correct ordinal values")
    void testOrdinalValues() {
        AssetStatus[] values = AssetStatus.values();
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, values[i].ordinal(), 
                "Ordinal for " + values[i].name() + " should be " + i);
        }
    }

    @Test
    @DisplayName("valueOf() should return correct enum for all values")
    void testValueOfAllEnums() {
        for (AssetStatus assetStatus : AssetStatus.values()) {
            assertEquals(assetStatus, AssetStatus.valueOf(assetStatus.name()),
                "valueOf() should return the same enum for " + assetStatus.name());
        }
    }
}
