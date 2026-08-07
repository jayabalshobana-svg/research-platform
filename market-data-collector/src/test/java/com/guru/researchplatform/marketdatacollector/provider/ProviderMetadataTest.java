package com.guru.researchplatform.marketdatacollector.provider;

import com.guru.researchplatform.collector.contract.ProviderMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProviderMetadata Tests")
class ProviderMetadataTest {

    @Test
    @DisplayName("should create provider metadata with description")
    void shouldCreateProviderMetadata() {
        ProviderMetadata metadata = new ProviderMetadata("BINANCE", "1.0.0", "Binance provider implementation");

        assertEquals("BINANCE", metadata.providerName());
        assertEquals("1.0.0", metadata.version());
        assertEquals("Binance provider implementation", metadata.description());
    }

    @Test
    @DisplayName("should create metadata with empty description using factory")
    void shouldCreateMetadataWithEmptyDescription() {
        ProviderMetadata metadata = ProviderMetadata.of("BINANCE", "1.0.0");

        assertEquals("BINANCE", metadata.providerName());
        assertEquals("1.0.0", metadata.version());
        assertEquals("", metadata.description());
    }

    @Test
    @DisplayName("should create metadata with description using factory")
    void shouldCreateMetadataWithDescriptionUsingFactory() {
        ProviderMetadata metadata = ProviderMetadata.of("BINANCE", "1.0.0", "Binance provider");

        assertEquals("BINANCE", metadata.providerName());
        assertEquals("1.0.0", metadata.version());
        assertEquals("Binance provider", metadata.description());
    }

    @Test
    @DisplayName("should throw when providerName is null")
    void shouldThrowWhenProviderNameIsNull() {
        assertThrows(NullPointerException.class, () ->
            new ProviderMetadata(null, "1.0.0", "Description")
        );
    }

    @Test
    @DisplayName("should throw when providerName is blank")
    void shouldThrowWhenProviderNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new ProviderMetadata("   ", "1.0.0", "Description")
        );
    }

    @Test
    @DisplayName("should throw when version is null")
    void shouldThrowWhenVersionIsNull() {
        assertThrows(NullPointerException.class, () ->
            new ProviderMetadata("BINANCE", null, "Description")
        );
    }

    @Test
    @DisplayName("should throw when version is blank")
    void shouldThrowWhenVersionIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new ProviderMetadata("BINANCE", "   ", "Description")
        );
    }

    @Test
    @DisplayName("should throw when description is null")
    void shouldThrowWhenDescriptionIsNull() {
        assertThrows(NullPointerException.class, () ->
            new ProviderMetadata("BINANCE", "1.0.0", null)
        );
    }

    @Test
    @DisplayName("should allow empty description")
    void shouldAllowEmptyDescription() {
        ProviderMetadata metadata = new ProviderMetadata("BINANCE", "1.0.0", "");

        assertEquals("", metadata.description());
    }

    @Test
    @DisplayName("should distinguish different providers")
    void shouldDistinguishDifferentProviders() {
        ProviderMetadata binance = ProviderMetadata.of("BINANCE", "1.0.0", "Binance");
        ProviderMetadata kraken = ProviderMetadata.of("KRAKEN", "2.0.0", "Kraken");

        assertNotEquals(binance, kraken);
        assertNotEquals(binance.providerName(), kraken.providerName());
    }

    @Test
    @DisplayName("should support various version formats")
    void shouldSupportVariousVersionFormats() {
        ProviderMetadata v1 = ProviderMetadata.of("BINANCE", "1.0.0");
        ProviderMetadata v2 = ProviderMetadata.of("BINANCE", "1.0.0-beta");
        ProviderMetadata v3 = ProviderMetadata.of("BINANCE", "2024.01.01");

        assertEquals("1.0.0", v1.version());
        assertEquals("1.0.0-beta", v2.version());
        assertEquals("2024.01.01", v3.version());
    }

    @Test
    @DisplayName("should support various provider names")
    void shouldSupportVariousProviderNames() {
        ProviderMetadata binance = ProviderMetadata.of("BINANCE", "1.0.0");
        ProviderMetadata kraken = ProviderMetadata.of("KRAKEN", "1.0.0");
        ProviderMetadata nasdaq = ProviderMetadata.of("NASDAQ", "1.0.0");

        assertEquals("BINANCE", binance.providerName());
        assertEquals("KRAKEN", kraken.providerName());
        assertEquals("NASDAQ", nasdaq.providerName());
    }
}
