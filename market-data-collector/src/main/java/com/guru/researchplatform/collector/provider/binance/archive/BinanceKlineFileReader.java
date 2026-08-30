package com.guru.researchplatform.collector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BinanceKlineFileReader {

    private final BinanceKlineCsvReader csvReader;

    public BinanceKlineFileReader(BinanceKlineCsvReader csvReader) {
        this.csvReader = Objects.requireNonNull(csvReader);
    }

    public List<BinanceKlineRecord> readAll(Path csvFile) {

        Objects.requireNonNull(csvFile, "csvFile cannot be null");

        if (!Files.exists(csvFile)) {
            throw new IllegalArgumentException(
                    "CSV file does not exist: " + csvFile
            );
        }

        List<BinanceKlineRecord> records = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvFile)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                records.add(csvReader.readLine(line));
            }

            return records;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to read CSV file: " + csvFile,
                    e
            );
        }
    }

    public BinanceKlineRecord readFirstRecord(Path csvFile) {

        Objects.requireNonNull(csvFile, "csvFile cannot be null");

        if (!Files.exists(csvFile)) {
            throw new IllegalArgumentException(
                    "CSV file does not exist: " + csvFile
            );
        }

        try (BufferedReader reader = Files.newBufferedReader(csvFile)) {

            String line = reader.readLine();

            if (line == null || line.isBlank()) {
                throw new IllegalArgumentException(
                        "CSV file is empty: " + csvFile
                );
            }

            return csvReader.readLine(line);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to read CSV file: " + csvFile,
                    e
            );
        }
    }
}