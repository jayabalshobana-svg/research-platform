package com.guru.researchplatform.collector.service;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceArchiveDownloader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceArchiveExtractor;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceCandleLoader;
import com.guru.researchplatform.collector.repository.CandleRepository;
import com.guru.researchplatform.common.domain.Candle;

import java.util.List;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Timeframe;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

public class BinanceHistoricalImportService
        implements MonthlyHistoricalDataImporter {

    private final BinanceArchiveDownloader archiveDownloader;
    private final BinanceArchiveExtractor archiveExtractor;
    private final BinanceCandleLoader candleLoader;
    private final HistoricalDataImportService historicalDataImportService;

    public BinanceHistoricalImportService(
            BinanceArchiveDownloader archiveDownloader,
            BinanceArchiveExtractor archiveExtractor,
            BinanceCandleLoader candleLoader,
            HistoricalDataImportService historicalDataImportService
    ) {
        this.archiveDownloader = Objects.requireNonNull(archiveDownloader);
        this.archiveExtractor = Objects.requireNonNull(archiveExtractor);
        this.candleLoader = Objects.requireNonNull(candleLoader);
        this.historicalDataImportService =
                Objects.requireNonNull(historicalDataImportService);
    }

    public Path downloadAndExtract(
            Asset asset,
            Timeframe timeframe,
            YearMonth month
    ) {
        Path zipFile = archiveDownloader.downloadMonthlyArchive(
                asset,
                timeframe,
                month
        );

        return archiveExtractor.extractCsv(zipFile);
    }

    /**
     * Downloads one month and converts its CSV data into Candle objects.
     */
    public List<Candle> downloadExtractAndLoad(
            Asset asset,
            Timeframe timeframe,
            YearMonth month
    ) {
        Path csvFile = downloadAndExtract(
                asset,
                timeframe,
                month
        );

        return candleLoader.load(
                csvFile,
                asset,
                timeframe
        );
    }

    public int downloadExtractLoadAndSave(
            Asset asset,
            Timeframe timeframe,
            YearMonth month
    ) {
        if (historicalDataImportService.isAlreadyImported(
                asset,
                timeframe,
                month
        )) {
            System.out.println(
                    "Month " + month + " already imported. Skipping download."
            );
            return 0;
        }

        Path csvFile = downloadAndExtract(
                asset,
                timeframe,
                month
        );

        return historicalDataImportService.importData(
                csvFile,
                asset,
                timeframe
        );
    }

    public ImportSummary importMonthlyRange(
            Asset asset,
            Timeframe timeframe,
            YearMonth startMonth,
            YearMonth endMonth
    ) {
        Objects.requireNonNull(asset, "asset cannot be null");
        Objects.requireNonNull(timeframe, "timeframe cannot be null");
        Objects.requireNonNull(startMonth, "startMonth cannot be null");
        Objects.requireNonNull(endMonth, "endMonth cannot be null");

        if (startMonth.isAfter(endMonth)) {
            throw new IllegalArgumentException(
                    "startMonth cannot be after endMonth"
            );
        }

        int importedMonths = 0;
        int skippedMonths = 0;
        int failedMonths = 0;
        int totalCandlesImported = 0;

        YearMonth currentMonth = startMonth;

        while (!currentMonth.isAfter(endMonth)) {

            System.out.println("Importing month: " + currentMonth);

            try {
                int imported = importMonth(
                        asset,
                        timeframe,
                        currentMonth
                );

                if (imported > 0) {
                    importedMonths++;
                    totalCandlesImported += imported;

                    System.out.println(
                            "Month " + currentMonth
                                    + ": imported " + imported + " candles."
                    );
                } else {
                    skippedMonths++;

                    System.out.println(
                            "Month " + currentMonth + ": skipped."
                    );
                }

            } catch (Exception e) {
                failedMonths++;

                System.err.println(
                        "Month " + currentMonth
                                + ": failed - " + e.getMessage()
                );
            }

            currentMonth = currentMonth.plusMonths(1);
        }

        return new ImportSummary(
                importedMonths,
                skippedMonths,
                failedMonths,
                totalCandlesImported
        );
    }

    @Override
    public int importMonth(
            Asset asset,
            Timeframe timeframe,
            YearMonth month
    ) {
        return downloadExtractLoadAndSave(
                asset,
                timeframe,
                month
        );
    }


}