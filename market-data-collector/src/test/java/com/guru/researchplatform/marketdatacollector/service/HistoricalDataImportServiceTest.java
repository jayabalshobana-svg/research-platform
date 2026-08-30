package com.guru.researchplatform.marketdatacollector.service;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.collector.repository.CandleRepository;
import com.guru.researchplatform.collector.service.HistoricalDataImportService;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricalDataImportServiceTest {

    @Test
    void shouldLoadCandlesAndPassThemToRepository() {

        // 1. Use our existing CSV
        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

        // 2. Build the existing candle loading pipeline
        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceCandleMapper mapper =
                new BinanceCandleMapper();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(
                        fileReader,
                        validator,
                        mapper
                );

        // 3. Use a fake repository instead of DuckDB
        FakeCandleRepository repository =
                new FakeCandleRepository();

        HistoricalDataImportService service =
                new HistoricalDataImportService(
                        candleLoader,
                        repository
                );

        Asset btc = new Asset(
                Exchange.BINANCE, "BTCUSDT", "BTC", "USDT", MarketType.CRYPTO, AssetStatus.ACTIVE
        );

        // 4. Execute the service
        service.importData(
                csvFile,
                btc,
                Timeframe.ONE_MINUTE
        );

        // 5. Verify all candles reached the repository
        assertEquals(
                44640,
                repository.savedCandles.size()
        );
    }

    /**
     * Simple test fake.
     * Stores candles in memory so we can verify what was passed to it.
     */
    private static class FakeCandleRepository
            implements CandleRepository {

        private final List<Candle> savedCandles =
                new ArrayList<>();

        @Override
        public void initialize() {
            // Not needed for this test
        }

        @Override
        public void saveAll(List<Candle> candles) {
            savedCandles.addAll(candles);
        }

        @Override
        public boolean exists(Exchange exchange, Asset asset, Timeframe timeframe, Instant openTime) {
            return false;
        }
    }
}