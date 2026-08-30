package com.guru.researchplatform.collector.configuration;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceArchiveDownloader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceArchiveExtractor;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.collector.repository.duckdb.DuckDbCandleRepository;
import com.guru.researchplatform.collector.service.BinanceHistoricalImportService;
import com.guru.researchplatform.collector.service.HistoricalDataImportService;
import com.guru.researchplatform.collector.service.HistoricalRangeImportService;

import java.nio.file.Path;
import java.util.Objects;

public class MarketDataCollectorConfiguration {

    private final Path databasePath;
    private final HttpExecutor httpExecutor;
    private final BinanceProperties properties;
    private final BinanceKlineFileReader fileReader;
    private final BinanceKlineValidator validator;
    private final BinanceCandleMapper mapper;

    public MarketDataCollectorConfiguration(
            Path databasePath,
            HttpExecutor httpExecutor,
            BinanceProperties properties,
            BinanceKlineFileReader fileReader,
            BinanceKlineValidator validator,
            BinanceCandleMapper mapper
    ) {
        this.databasePath = databasePath;
        this.httpExecutor = Objects.requireNonNull(httpExecutor);
        this.properties = Objects.requireNonNull(properties);
        this.fileReader = Objects.requireNonNull(fileReader);
        this.validator = Objects.requireNonNull(validator);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public HistoricalRangeImportService historicalRangeImportService() {

        BinanceArchiveDownloader archiveDownloader =
                new BinanceArchiveDownloader(httpExecutor, properties);

        BinanceArchiveExtractor archiveExtractor =
                new BinanceArchiveExtractor();

        BinanceCandleLoader candleLoader =
                new BinanceCandleLoader(fileReader, validator, mapper);

        DuckDbCandleRepository candleRepository =
                new DuckDbCandleRepository(databasePath);

        HistoricalDataImportService historicalDataImportService =
                new HistoricalDataImportService(
                        candleLoader,
                        candleRepository
                );

        BinanceHistoricalImportService monthlyImporter =
                new BinanceHistoricalImportService(
                        archiveDownloader,
                        archiveExtractor,
                        candleLoader,
                        historicalDataImportService
                );

        return new HistoricalRangeImportService(monthlyImporter);
    }
}