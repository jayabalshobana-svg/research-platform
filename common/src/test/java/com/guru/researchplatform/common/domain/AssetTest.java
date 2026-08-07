package com.guru.researchplatform.common.domain;

import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Asset Record Tests")
class AssetTest {

    @Test
    @DisplayName("should create a valid asset with all required fields")
    void shouldCreateValidAsset() {
        Asset asset = new Asset(
            Exchange.BINANCE,
            "BTCUSDT",
            "BTC",
            "USDT",
            MarketType.CRYPTO,
            AssetStatus.ACTIVE
        );

        assertNotNull(asset);
        assertEquals(Exchange.BINANCE, asset.exchange());
        assertEquals("BTCUSDT", asset.symbol());
        assertEquals("BTC", asset.baseAsset());
        assertEquals("USDT", asset.quoteAsset());
        assertEquals(MarketType.CRYPTO, asset.marketType());
        assertEquals(AssetStatus.ACTIVE, asset.status());
    }

    @Test
    @DisplayName("should normalize symbol to uppercase")
    void shouldNormalizeSymbolToUppercase() {
        Asset asset = new Asset(
            Exchange.BINANCE,
            "btcusdt",
            "BTC",
            "USDT",
            MarketType.CRYPTO,
            AssetStatus.ACTIVE
        );

        assertEquals("BTCUSDT", asset.symbol());
    }

    @Test
    @DisplayName("should normalize baseAsset to uppercase")
    void shouldNormalizeBaseAssetToUppercase() {
        Asset asset = new Asset(
            Exchange.BINANCE,
            "BTCUSDT",
            "btc",
            "USDT",
            MarketType.CRYPTO,
            AssetStatus.ACTIVE
        );

        assertEquals("BTC", asset.baseAsset());
    }

    @Test
    @DisplayName("should normalize quoteAsset to uppercase")
    void shouldNormalizeQuoteAssetToUppercase() {
        Asset asset = new Asset(
            Exchange.BINANCE,
            "BTCUSDT",
            "BTC",
            "usdt",
            MarketType.CRYPTO,
            AssetStatus.ACTIVE
        );

        assertEquals("USDT", asset.quoteAsset());
    }

    @Test
    @DisplayName("should normalize mixed case strings with Locale.ROOT")
    void shouldNormalizeMixedCaseStringsWithLocaleRoot() {
        Asset asset = new Asset(
            Exchange.NASDAQ,
            "aApL",
            "aApL",
            "uSd",
            MarketType.STOCK,
            AssetStatus.ACTIVE
        );

        assertEquals("AAPL", asset.symbol());
        assertEquals("AAPL", asset.baseAsset());
        assertEquals("USD", asset.quoteAsset());
    }

    @Test
    @DisplayName("should throw NullPointerException when exchange is null")
    void shouldThrowWhenExchangeIsNull() {
        assertThrows(NullPointerException.class, () ->
            new Asset(null, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw NullPointerException when symbol is null")
    void shouldThrowWhenSymbolIsNull() {
        assertThrows(NullPointerException.class, () ->
            new Asset(Exchange.BINANCE, null, "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw NullPointerException when baseAsset is null")
    void shouldThrowWhenBaseAssetIsNull() {
        assertThrows(NullPointerException.class, () ->
            new Asset(Exchange.BINANCE, "BTCUSDT", null, "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw NullPointerException when quoteAsset is null")
    void shouldThrowWhenQuoteAssetIsNull() {
        assertThrows(NullPointerException.class, () ->
            new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", null, MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw NullPointerException when marketType is null")
    void shouldThrowWhenMarketTypeIsNull() {
        assertThrows(NullPointerException.class, () ->
            new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", null, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw NullPointerException when status is null")
    void shouldThrowWhenStatusIsNull() {
        assertThrows(NullPointerException.class, () ->
            new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, null)
        );
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when symbol is blank")
    void shouldThrowWhenSymbolIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new Asset(Exchange.BINANCE, "   ", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when symbol is empty")
    void shouldThrowWhenSymbolIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            new Asset(Exchange.BINANCE, "", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when baseAsset is blank")
    void shouldThrowWhenBaseAssetIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new Asset(Exchange.BINANCE, "BTCUSDT", "   ", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when quoteAsset is blank")
    void shouldThrowWhenQuoteAssetIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            new Asset(Exchange.BINANCE, "BTCUSDT", "BTC", "   ", MarketType.CRYPTO, AssetStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("should correctly identify crypto assets")
    void shouldIdentifyCryptoAssets() {
        Asset crypto = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset stock = new Asset(
            Exchange.NASDAQ, "AAPL", "AAPL", "USD", MarketType.STOCK, AssetStatus.ACTIVE
        );

        assertTrue(crypto.isCrypto());
        assertFalse(stock.isCrypto());
    }

    @Test
    @DisplayName("should correctly identify tradable assets")
    void shouldIdentifyTradableAssets() {
        Asset tradable = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset notTradable = new Asset(
            Exchange.BINANCE, "OLDCOIN", "OLD", "USDT", MarketType.CRYPTO, AssetStatus.DELISTED
        );

        assertTrue(tradable.isTradable());
        assertFalse(notTradable.isTradable());
    }

    @Test
    @DisplayName("should return correct display name")
    void shouldReturnCorrectDisplayName() {
        Asset btcUsdt = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset aaplUsd = new Asset(
            Exchange.NASDAQ, "AAPL", "AAPL", "USD", MarketType.STOCK, AssetStatus.ACTIVE
        );
        Asset eurUsd = new Asset(
            Exchange.BINANCE, "EURUSD", "EUR", "USD", MarketType.FOREX, AssetStatus.ACTIVE
        );

        assertEquals("BTC/USDT", btcUsdt.displayName());
        assertEquals("AAPL/USD", aaplUsd.displayName());
        assertEquals("EUR/USD", eurUsd.displayName());
    }

    @Test
    @DisplayName("should return correct identifier")
    void shouldReturnCorrectIdentifier() {
        Asset binanceBtc = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset nasdaqAapl = new Asset(
            Exchange.NASDAQ, "AAPL", "AAPL", "USD", MarketType.STOCK, AssetStatus.ACTIVE
        );
        Asset nseInfy = new Asset(
            Exchange.NSE, "INFY", "INFY", "INR", MarketType.STOCK, AssetStatus.ACTIVE
        );

        assertEquals("BINANCE:BTCUSDT", binanceBtc.identifier());
        assertEquals("NASDAQ:AAPL", nasdaqAapl.identifier());
        assertEquals("NSE:INFY", nseInfy.identifier());
    }

    @Test
    @DisplayName("should maintain immutability")
    void shouldMaintainImmutability() {
        Asset asset1 = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset asset2 = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        // Records provide auto-generated equals() and hashCode()
        assertEquals(asset1, asset2);
        assertEquals(asset1.hashCode(), asset2.hashCode());
    }

    @Test
    @DisplayName("should provide auto-generated toString()")
    void shouldProvideAutoGeneratedToString() {
        Asset asset = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        String toString = asset.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("BTCUSDT"));
        assertTrue(toString.contains("BTC"));
        assertTrue(toString.contains("USDT"));
    }

    @Test
    @DisplayName("should distinguish different assets")
    void shouldDistinguishDifferentAssets() {
        Asset btcUsdt = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset ethUsdt = new Asset(
            Exchange.BINANCE, "ETHUSDT", "ETH", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        assertNotEquals(btcUsdt, ethUsdt);
        assertNotEquals(btcUsdt.identifier(), ethUsdt.identifier());
        assertNotEquals(btcUsdt.displayName(), ethUsdt.displayName());
    }

    @Test
    @DisplayName("should support various market types")
    void shouldSupportVariousMarketTypes() {
        Asset crypto = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset stock = new Asset(
            Exchange.NASDAQ, "MSFT", "MSFT", "USD", MarketType.STOCK, AssetStatus.ACTIVE
        );
        Asset forex = new Asset(
            Exchange.BINANCE, "EURUSD", "EUR", "USD", MarketType.FOREX, AssetStatus.ACTIVE
        );
        Asset commodity = new Asset(
            Exchange.BINANCE, "XAUUSD", "XAU", "USD", MarketType.COMMODITY, AssetStatus.ACTIVE
        );

        assertTrue(crypto.isCrypto());
        assertFalse(stock.isCrypto());
        assertFalse(forex.isCrypto());
        assertFalse(commodity.isCrypto());
    }

    @Test
    @DisplayName("should support various asset statuses")
    void shouldSupportVariousAssetStatuses() {
        Asset active = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );
        Asset inactive = new Asset(
            Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.INACTIVE
        );
        Asset delisted = new Asset(
            Exchange.BINANCE, "OLDCOIN", "OLD", "USDT", MarketType.CRYPTO, AssetStatus.DELISTED
        );
        Asset suspended = new Asset(
            Exchange.BINANCE, "SUSPY", "SUSP", "USDT", MarketType.CRYPTO, AssetStatus.SUSPENDED
        );

        assertTrue(active.isTradable());
        assertFalse(inactive.isTradable());
        assertFalse(delisted.isTradable());
        assertFalse(suspended.isTradable());
    }
}
