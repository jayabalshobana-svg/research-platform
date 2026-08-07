package com.guru.researchplatform.marketdatacollector.provider;

import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import com.guru.researchplatform.marketdatacollector.api.dto.DownloadRequest;
import com.guru.researchplatform.marketdatacollector.api.dto.DownloadResult;
import com.guru.researchplatform.marketdatacollector.api.dto.ValidationResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of MarketDataProvider for testing and architecture validation.
 * 
 * Generates deterministic BTCUSDT daily candles without network access or external dependencies.
 * This provider is designed purely for testing the collector framework and validating
 * the architecture without requiring actual exchange data.
 * 
 * All candles are generated deterministically based on the request parameters,
 * ensuring reproducible test results. No random values are used.
 */
public class MockMarketDataProvider implements MarketDataProvider {

    private static final String PROVIDER_NAME = "MOCK";
    private static final String PROVIDER_VERSION = "1.0.0";
    private static final ProviderMetadata METADATA = ProviderMetadata.of(
        PROVIDER_NAME,
        PROVIDER_VERSION,
        "Mock provider for testing and architecture validation"
    );

    // Reference BTCUSDT asset for this provider
    private static final Asset MOCK_BTCUSDT = new Asset(
        Exchange.BINANCE,
        "BTCUSDT",
        "BTC",
        "USDT",
        MarketType.CRYPTO,
        AssetStatus.ACTIVE
    );

    /**
     * Returns BINANCE as the exchange this mock provider serves.
     * 
     * @return Exchange.BINANCE
     */
    @Override
    public Exchange exchange() {
        return Exchange.BINANCE;
    }

    /**
     * Determines if this provider supports the specified asset.
     * 
     * Currently only supports BTCUSDT for testing purposes.
     * 
     * @param asset the asset to check
     * @return true only if asset is BTCUSDT, false otherwise
     */
    @Override
    public boolean supports(Asset asset) {
        // Only support BTCUSDT for testing
        return asset != null && "BTCUSDT".equals(asset.symbol());
    }

    /**
     * Validates a download request.
     * 
     * Requirements:
     * - Asset must be BTCUSDT
     * - Timeframe must be ONE_DAY
     * - startTime must not be after endTime
     * 
     * @param request the download request to validate
     * @return ValidationResult indicating if the request is valid
     */
    @Override
    public ValidationResult validate(DownloadRequest request) {
        if (request == null) {
            return ValidationResult.failure("Request cannot be null");
        }

        if (!supports(request.asset())) {
            return ValidationResult.failure("Mock provider only supports BTCUSDT");
        }

        if (request.timeframe() != Timeframe.ONE_DAY) {
            return ValidationResult.failure("Mock provider only supports ONE_DAY timeframe");
        }

        if (request.startTime().isAfter(request.endTime())) {
            return ValidationResult.failure("startTime cannot be after endTime");
        }

        return ValidationResult.success();
    }

    /**
     * Downloads mock BTCUSDT daily candles for the specified time range.
     * 
     * Generates approximately 10 deterministic candles based on the request parameters.
     * All prices are realistic for BTCUSDT trading. The candles follow a realistic
     * price pattern with high >= {open, close, low} and low <= {open, close}.
     * 
     * @param request the download request specifying the time range
     * @return a DownloadResult containing generated candles
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public DownloadResult download(DownloadRequest request) {
        ValidationResult validation = validate(request);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Invalid request: " + validation.errors().get(0));
        }

        List<Candle> candles = generateCandles(request);
        return DownloadResult.of(candles, PROVIDER_NAME);
    }

    /**
     * Returns metadata about this mock provider.
     * 
     * @return ProviderMetadata with name, version, and description
     */
    @Override
    public ProviderMetadata metadata() {
        return METADATA;
    }

    /**
     * Generates deterministic mock candles for the specified request.
     * 
     * Creates daily candles with realistic BTCUSDT price movements.
     * Each candle is generated based on the day number, ensuring reproducibility.
     * 
     * @param request the download request
     * @return list of generated Candle objects
     */
    private List<Candle> generateCandles(DownloadRequest request) {
        List<Candle> candles = new ArrayList<>();
        
        // Generate up to 10 candles
        Instant currentTime = request.startTime();
        Instant endTime = request.endTime();
        int maxCandles = request.hasLimit() ? request.limit() : 10;
        int candleCount = 0;

        // Include candle if currentTime equals or is before endTime
        while (!currentTime.isAfter(endTime) && candleCount < maxCandles) {
            Candle candle = generateCandle(currentTime, candleCount);
            candles.add(candle);
            currentTime = currentTime.plus(1, ChronoUnit.DAYS);
            candleCount++;
        }

        return candles;
    }

    /**
     * Generates a single deterministic mock candle for the specified date.
     * 
     * Prices are based on a base price adjusted by the candle index.
     * Open price starts at $40,000 and increases by $100 per candle.
     * Each candle has a high, low, and close price following realistic patterns.
     * Volume and trade data are realistic but also deterministic.
     * 
     * @param time the opening time of the candle (assumed to be UTC midnight)
     * @param index the candle index (0-based), used to generate deterministic values
     * @return a generated Candle
     */
    private Candle generateCandle(Instant time, int index) {
        // Base price starts at 40,000 USDT
        long basePriceUsdt = 40000 + (index * 100);

        // Open price
        BigDecimal open = BigDecimal.valueOf(basePriceUsdt);

        // High price: add a percentage based on candle index
        // Pattern: alternating gains/drops for realistic movement
        long highPriceUsdt = basePriceUsdt + 300 + (index % 2 == 0 ? 200 : -50);
        BigDecimal high = BigDecimal.valueOf(highPriceUsdt);

        // Low price: subtract from open
        long lowPriceUsdt = basePriceUsdt - 200 + (index % 3) * 50;
        BigDecimal low = BigDecimal.valueOf(lowPriceUsdt);

        // Close price: typically between open and high
        long closePriceUsdt = basePriceUsdt + 150 + (index % 2 == 0 ? 100 : -75);
        BigDecimal close = BigDecimal.valueOf(closePriceUsdt);

        // Volume: realistic amount in BTC
        // Volume decreases slightly over time (deterministic pattern)
        BigDecimal volume = BigDecimal.valueOf(10 + (10 - index) * 0.5);

        // Quote asset volume: volume * close price
        BigDecimal quoteAssetVolume = volume.multiply(close);

        // Taker buy base volume: ~40% of total volume
        BigDecimal takerBuyBaseVolume = volume.multiply(BigDecimal.valueOf(0.4));

        // Taker buy quote volume: taker volume * close price
        BigDecimal takerBuyQuoteVolume = takerBuyBaseVolume.multiply(close);

        // Trade count: realistic daily trades
        long tradeCount = 5000L + (index * 100);

        // Candle times: daily candle from midnight to midnight UTC
        Instant openTime = time;
        Instant closeTime = time.plus(1, ChronoUnit.DAYS);

        return new Candle(
            Exchange.BINANCE,
            MOCK_BTCUSDT,
            Timeframe.ONE_DAY,
            openTime,
            closeTime,
            open,
            high,
            low,
            close,
            volume,
            quoteAssetVolume,
            takerBuyBaseVolume,
            takerBuyQuoteVolume,
            tradeCount
        );
    }
}
