package com.guru.researchplatform.marketdatacollector.integration;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceArchiveExtractor;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BinanceArchiveExtractorTest {

    @Test
    void shouldExtractCsvFromBinanceArchive() throws Exception {

        Path zipFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01.zip"
        );

        BinanceArchiveExtractor extractor =
                new BinanceArchiveExtractor();

        Path csvFile = extractor.extractCsv(zipFile);

        System.out.println("Extracted CSV: "
                + csvFile.toAbsolutePath());

        System.out.println("CSV size: "
                + Files.size(csvFile) + " bytes");

        assertTrue(Files.exists(csvFile));
        assertTrue(Files.size(csvFile) > 0);
    }
}
