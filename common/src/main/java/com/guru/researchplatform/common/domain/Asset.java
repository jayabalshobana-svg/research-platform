package com.guru.researchplatform.common.domain;

import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import java.util.Locale;
import java.util.Objects;

/**
 * An immutable record representing a tradable financial instrument.
 * 
 * All string fields (symbol, baseAsset, quoteAsset) are normalized to uppercase
 * using Locale.ROOT to ensure consistency across different locales.
 * 
 * Examples: BTCUSDT, ETHUSDT, AAPL, MSFT, EURUSD, XAUUSD
 */
public record Asset(
    Exchange exchange,
    String symbol,
    String baseAsset,
    String quoteAsset,
    MarketType marketType,
    AssetStatus status
) {
    /**
     * Compact constructor for validation and normalization.
     * 
     * Validates that:
     * - All required fields are non-null
     * - All string fields are non-blank
     * 
     * Normalizes:
     * - symbol, baseAsset, quoteAsset to uppercase using Locale.ROOT
     */
    public Asset {
        Objects.requireNonNull(exchange, "exchange cannot be null");
        Objects.requireNonNull(marketType, "marketType cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        
        // Normalize and validate strings
        symbol = normalizeString(symbol, "symbol");
        baseAsset = normalizeString(baseAsset, "baseAsset");
        quoteAsset = normalizeString(quoteAsset, "quoteAsset");
    }

    /**
     * Normalizes a string field by ensuring it is non-null, non-blank, and uppercase.
     * 
     * @param value the string to normalize
     * @param fieldName the name of the field (for error messages)
     * @return the normalized (uppercase) string
     * @throws NullPointerException if value is null
     * @throws IllegalArgumentException if value is blank
     */
    private static String normalizeString(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        
        return value.toUpperCase(Locale.ROOT);
    }

    /**
     * Determines if this asset is a cryptocurrency.
     * 
     * @return true if marketType is CRYPTO, false otherwise
     */
    public boolean isCrypto() {
        return marketType == MarketType.CRYPTO;
    }

    /**
     * Determines if this asset is currently tradable.
     * 
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isTradable() {
        return status == AssetStatus.ACTIVE;
    }

    /**
     * Returns a human-readable display name for this asset.
     * 
     * Combines baseAsset and quoteAsset in the format "BASE/QUOTE".
     * Both components are already normalized to uppercase.
     * 
     * Examples: "BTC/USDT", "AAPL/USD", "EUR/USD"
     * 
     * @return the display name in base/quote format
     */
    public String displayName() {
        return baseAsset + "/" + quoteAsset;
    }

    /**
     * Returns the unique identifier for this asset combining exchange and symbol.
     * 
     * The format is "EXCHANGE:SYMBOL" where both components are already normalized.
     * This identifier is suitable for use as a unique key across the platform.
     * 
     * Examples: "BINANCE:BTCUSDT", "NASDAQ:AAPL", "NSE:INFY"
     * 
     * @return the identifier in exchange:symbol format
     */
    public String identifier() {
        return exchange + ":" + symbol;
    }
}
