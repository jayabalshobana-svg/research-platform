package com.guru.researchplatform.common.domain;

import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import java.util.Objects;

/**
 * An immutable record representing a tradable financial instrument.
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
     * Compact constructor for validation.
     * Ensures all fields meet domain requirements.
     */
    public Asset {
        Objects.requireNonNull(exchange, "exchange cannot be null");
        Objects.requireNonNull(symbol, "symbol cannot be null");
        Objects.requireNonNull(baseAsset, "baseAsset cannot be null");
        Objects.requireNonNull(quoteAsset, "quoteAsset cannot be null");
        Objects.requireNonNull(marketType, "marketType cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        
        if (symbol.isBlank()) {
            throw new IllegalArgumentException("symbol cannot be blank");
        }
        if (baseAsset.isBlank()) {
            throw new IllegalArgumentException("baseAsset cannot be blank");
        }
        if (quoteAsset.isBlank()) {
            throw new IllegalArgumentException("quoteAsset cannot be blank");
        }
    }

    /**
     * Check if this asset is a cryptocurrency.
     * @return true if marketType is CRYPTO, false otherwise
     */
    public boolean isCrypto() {
        return marketType == MarketType.CRYPTO;
    }

    /**
     * Check if this asset is currently tradable.
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isTradable() {
        return status == AssetStatus.ACTIVE;
    }

    /**
     * Get a human-readable display name for this asset.
     * Example: "BTC/USDT"
     * @return base/quote format
     */
    public String displayName() {
        return baseAsset + "/" + quoteAsset;
    }

    /**
     * Get the unique identifier combining exchange and symbol.
     * Example: "BINANCE:BTCUSDT"
     * @return exchange:symbol format
     */
    public String identifier() {
        return exchange + ":" + symbol;
    }
}
