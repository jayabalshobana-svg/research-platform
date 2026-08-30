package com.guru.researchplatform.marketdatacollector.provider.binance.archive.validation;

import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineCsvReader;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceKlineFileReader;
import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;

import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidationResult;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BinanceKlineValidatorTest {

    @Test
    void shouldValidateJanuary2024Dataset() {

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

        BinanceKlineValidator validator =
                new BinanceKlineValidator();

        BinanceKlineValidationResult result =
                validator.validate(records);

        System.out.println();
        System.out.println("================================");
        System.out.println("Binance Kline Validation");
        System.out.println("================================");
        System.out.println("Total rows        : " + result.totalRows());
        System.out.println("Valid rows        : " + result.validRows());
        System.out.println("Invalid rows      : " + result.invalidRows());
        System.out.println("Duplicate times   : " + result.duplicateTimestamps());
        System.out.println("Out of order      : " + result.outOfOrderRows());
        System.out.println("Missing intervals : " + result.missingIntervals());
        System.out.println("Invalid OHLC      : " + result.invalidOhlcRows());
        System.out.println("Negative volume   : " + result.negativeVolumeRows());
        System.out.println("Negative trades   : " + result.negativeTradeCountRows());

        assertTrue(result.totalRows() > 0);
    }
}