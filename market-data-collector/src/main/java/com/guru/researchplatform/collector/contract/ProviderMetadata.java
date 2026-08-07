package com.guru.researchplatform.collector.contract;

import java.util.Objects;

/**
 * Placeholder metadata about a market data provider.
 * 
 * Immutable record containing basic information about a provider's capabilities
 * and characteristics. This is a placeholder that can be extended with more
 * detailed information as the platform evolves.
 */
public record ProviderMetadata(
    String providerName,
    String version,
    String description
) {
    /**
     * Compact constructor for validation.
     * 
     * Ensures:
     * - providerName is non-null and non-blank
     * - version is non-null and non-blank
     * - description is non-null (can be empty)
     */
    public ProviderMetadata {
        Objects.requireNonNull(providerName, "providerName cannot be null");
        if (providerName.isBlank()) {
            throw new IllegalArgumentException("providerName cannot be blank");
        }
        
        Objects.requireNonNull(version, "version cannot be null");
        if (version.isBlank()) {
            throw new IllegalArgumentException("version cannot be blank");
        }
        
        Objects.requireNonNull(description, "description cannot be null");
    }

    /**
     * Creates provider metadata with a default empty description.
     * 
     * @param providerName the name of the provider
     * @param version the version of the provider implementation
     * @return ProviderMetadata with empty description
     */
    public static ProviderMetadata of(String providerName, String version) {
        return new ProviderMetadata(providerName, version, "");
    }

    /**
     * Creates provider metadata with all information.
     * 
     * @param providerName the name of the provider
     * @param version the version of the provider implementation
     * @param description a description of the provider's capabilities
     * @return ProviderMetadata with the specified description
     */
    public static ProviderMetadata of(String providerName, String version, String description) {
        Objects.requireNonNull(description, "description cannot be null");
        return new ProviderMetadata(providerName, version, description);
    }
}
