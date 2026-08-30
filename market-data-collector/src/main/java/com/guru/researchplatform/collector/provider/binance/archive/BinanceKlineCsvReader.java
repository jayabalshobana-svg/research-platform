package com.guru.researchplatform.collector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;

import java.math.BigDecimal;

public class BinanceKlineCsvReader {

    public BinanceKlineRecord readLine(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("CSV line cannot be null or blank");
        }

        String[] columns = line.split(",", -1);

        if (columns.length != 12) {
            throw new IllegalArgumentException(
                    "Expected 12 columns but found " + columns.length
            );
        }

        return new BinanceKlineRecord(
                Long.parseLong(columns[0]),
                new BigDecimal(columns[1]),
                new BigDecimal(columns[2]),
                new BigDecimal(columns[3]),
                new BigDecimal(columns[4]),
                new BigDecimal(columns[5]),
                Long.parseLong(columns[6]),
                new BigDecimal(columns[7]),
                Long.parseLong(columns[8]),
                new BigDecimal(columns[9]),
                new BigDecimal(columns[10])
        );
    }
}
