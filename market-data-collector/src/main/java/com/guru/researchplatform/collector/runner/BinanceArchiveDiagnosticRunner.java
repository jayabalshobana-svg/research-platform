package com.guru.researchplatform.collector.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinanceArchiveDiagnosticRunner {

    public static void main(String[] args) throws IOException {

        Path csvFile = Path.of(
                "data",
                "archives",
                "spot",
                "BTCUSDT",
                "1m",
                "BTCUSDT-1m-2017-12",
                "BTCUSDT-1m-2017-12.csv"
        );

        System.out.println("Reading CSV: "
                + csvFile.toAbsolutePath());

        try (BufferedReader reader =
                     Files.newBufferedReader(csvFile)) {

            String line;

            while ((line = reader.readLine()) != null) {

                // Print rows containing the suspicious timestamp.
                // 1512367220799 = 2017-12-04T06:00:20.799Z
                if (line.startsWith("1512367220799,")) {
                    System.out.println();
                    System.out.println("Suspicious raw CSV row:");
                    System.out.println(line);
                    break;
                }
            }
        }
    }
}