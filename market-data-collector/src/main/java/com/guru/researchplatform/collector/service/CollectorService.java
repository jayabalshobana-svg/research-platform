package com.guru.researchplatform.collector.service;

import com.guru.researchplatform.collector.contract.DownloadRequest;
import com.guru.researchplatform.collector.contract.DownloadResult;
import com.guru.researchplatform.collector.contract.ValidationResult;
import com.guru.researchplatform.collector.provider.MarketDataProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service for collecting market data from various providers.
 * 
 * Responsibilities:
 * - Validating download requests
 * - Selecting the appropriate provider for the requested exchange
 * - Invoking the provider's download method
 * - Returning download results
 * 
 * Uses constructor injection to receive a list of available providers.
 * Depends only on the MarketDataProvider interface, not on concrete implementations.
 * No persistence, HTTP, or Spring framework annotations are used in the domain logic.
 */
public class CollectorService {

    private final List<MarketDataProvider> providers;

    /**
     * Constructs a CollectorService with the specified providers.
     * 
     * @param providers a non-empty list of market data providers
     * @throws NullPointerException if providers is null
     * @throws IllegalArgumentException if providers list is empty
     */
    public CollectorService(List<MarketDataProvider> providers) {
        Objects.requireNonNull(providers, "providers list cannot be null");
        if (providers.isEmpty()) {
            throw new IllegalArgumentException("providers list cannot be empty");
        }
        this.providers = List.copyOf(providers);
    }

    /**
     * Validates a download request without invoking any provider.
     * 
     * Checks:
     * - Request is not null
     * - Asset is not null
     * - At least one provider supports the asset
     * 
     * @param request the download request to validate
     * @return a ValidationResult indicating if the request is valid
     */
    public ValidationResult validate(DownloadRequest request) {
        if (request == null) {
            return ValidationResult.failure("Download request cannot be null");
        }

        if (request.asset() == null) {
            return ValidationResult.failure("Asset cannot be null");
        }

        if (!supportsAsset(request.asset())) {
            return ValidationResult.failure(
                "No provider available for asset: " + request.asset().symbol()
            );
        }

        return ValidationResult.success();
    }

    /**
     * Downloads market data for the specified request.
     * 
     * Process:
     * 1. Validates the request
     * 2. Selects an appropriate provider
     * 3. Validates the request with the selected provider
     * 4. Invokes the provider's download method
     * 5. Returns the result
     * 
     * @param request the download request
     * @return a DownloadResult containing the fetched data
     * @throws IllegalArgumentException if validation fails or no suitable provider exists
     */
    public DownloadResult download(DownloadRequest request) {
        // First validate at service level
        ValidationResult validation = validate(request);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Invalid request: " + validation.errors().get(0));
        }

        // Find a suitable provider for this asset
        Optional<MarketDataProvider> provider = findProvider(request.asset());
        if (provider.isEmpty()) {
            throw new IllegalArgumentException(
                "No provider found for asset: " + request.asset().symbol()
            );
        }

        MarketDataProvider selectedProvider = provider.get();

        // Validate with the selected provider
        ValidationResult providerValidation = selectedProvider.validate(request);
        if (!providerValidation.valid()) {
            throw new IllegalArgumentException(
                "Provider validation failed: " + providerValidation.errors().get(0)
            );
        }

        // Download and return result
        return selectedProvider.download(request);
    }

    /**
     * Checks if any provider supports the specified asset.
     * 
     * @param asset the asset to check for support
     * @return true if at least one provider supports this asset, false otherwise
     */
    public boolean supportsAsset(com.guru.researchplatform.common.domain.Asset asset) {
        return providers.stream().anyMatch(provider -> provider.supports(asset));
    }

    /**
     * Finds a provider that supports the specified asset.
     * 
     * Returns the first provider that supports the asset, without guarantees
     * about the order.
     * 
     * @param asset the asset to find a provider for
     * @return an Optional containing a suitable provider, or empty if none found
     */
    private Optional<MarketDataProvider> findProvider(
        com.guru.researchplatform.common.domain.Asset asset
    ) {
        return providers.stream()
            .filter(provider -> provider.supports(asset))
            .findFirst();
    }

    /**
     * Returns the number of available providers.
     * 
     * @return the count of providers registered with this service
     */
    public int getProviderCount() {
        return providers.size();
    }
}
