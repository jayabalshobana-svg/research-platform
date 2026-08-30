package com.guru.researchplatform.marketdatacollector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinanceKlineFileReaderTest {

    @Test
    void shouldReadFirstRecordFromCsvFile() {

        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

        System.out.println("CSV path: " + csvFile.toAbsolutePath());
        System.out.println("Exists: " + Files.exists(csvFile));

        assertTrue(
                Files.exists(csvFile),
                "CSV file does not exist: " + csvFile.toAbsolutePath()
        );

        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        BinanceKlineRecord record =
                fileReader.readFirstRecord(csvFile);

        assertEquals(
                1704067200000L,
                record.openTime()
        );

        assertEquals(
                new BigDecimal("42283.58000000"),
                record.open()
        );

        assertEquals(
                new BigDecimal("42298.62000000"),
                record.high()
        );

        assertEquals(
                new BigDecimal("42261.02000000"),
                record.low()
        );

        assertEquals(
                new BigDecimal("42298.61000000"),
                record.close()
        );

        assertEquals(
                new BigDecimal("35.92724000"),
                record.volume()
        );

        assertEquals(
                1704067259999L,
                record.closeTime()
        );

        assertEquals(
                new BigDecimal("1519031.69451920"),
                record.quoteAssetVolume()
        );

        assertEquals(
                1327,
                record.tradeCount()
        );
    }

    @Test
    void shouldReadAllRecordsFromCsvFile() {

        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2024-01",
                "BTCUSDT-1m-2024-01.csv"
        );

        BinanceKlineCsvReader csvReader =
                new BinanceKlineCsvReader();

        BinanceKlineFileReader fileReader =
                new BinanceKlineFileReader(csvReader);

        List<BinanceKlineRecord> records =
                fileReader.readAll(csvFile);

        System.out.println("Total records: " + records.size());

        assertFalse(records.isEmpty());

        BinanceKlineRecord first = records.get(0);
        BinanceKlineRecord last = records.get(records.size() - 1);

        System.out.println(
                "First open time: " + first.openTime()
        );

        System.out.println(
                "Last open time: " + last.openTime()
        );

        assertEquals(
                1704067200000L,
                first.openTime()
        );
    }
}