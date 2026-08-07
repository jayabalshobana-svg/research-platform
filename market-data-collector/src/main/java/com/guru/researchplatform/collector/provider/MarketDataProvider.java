package com.guru.researchplatform.collector.provider;

import com.guru.researchplatform.collector.contract.ProviderMetadata;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.collector.contract.DownloadRequest;
import com.guru.researchplatform.collector.contract.DownloadResult;
import com.guru.researchplatform.collector.contract.ValidationResult;

/**
 * Contract for a market data provider that fetches candlestick data from an exchange.
 * 
 * Implementations are responsible for:
 * - Validating download requests before attempting to fetch data
 * - Fetching market data from the provider's data source
 * - Transforming provider-specific data formats into Candle records
 * - Handling provider-specific errors and rate limiting
 * 
 * All implementations must be thread-safe and immutable.
 */
public interface MarketDataProvider {

    /**
     * Returns the exchange this provider serves.
     * 
     * @return the Exchange enum value representing this provider's data source
     */
    Exchange exchange();

    /**
     * Determines if this provider supports downloading data for the specified asset.
     * 
     * Implementations should check asset characteristics such as:
     * - Market type (crypto, stock, forex, etc.)
     * - Whether the asset is tradable
     * - Exchange-specific listing requirements
     * 
     * @param asset the asset to check for support
     * @return true if this provider can fetch data for the asset, false otherwise
     */
    boolean supports(Asset asset);

    /**
     * Validates a download request before attempting to fetch data.
     * 
     * Implementations should verify:
     * - The asset is supported by this provider
     * - Time ranges are reasonable
     * - Request limits are within provider constraints
     * 
     * @param request the download request to validate
     * @return a ValidationResult indicating if the request is valid
     */
    ValidationResult validate(DownloadRequest request);

    /**
     * Downloads market data according to the specified request.
     * 
     * This method should only be called after validate() returns true.
     * Implementations handle:
     * - API communication with the data source
     * - Pagination for large date ranges
     * - Transformation of provider-specific data formats to Candle records
     * 
     * @param request the download request specifying what data to fetch
     * @return a DownloadResult containing the fetched candles
     * @throws IllegalArgumentException if request validation fails
     * @throws RuntimeException for provider-specific errors (rate limiting, network issues, etc.)
     */
    DownloadResult download(DownloadRequest request);

    /**
     * Returns metadata describing this provider's capabilities and version information.
     * 
     * @return ProviderMetadata containing name, version, and description
     */
    ProviderMetadata metadata();
}
