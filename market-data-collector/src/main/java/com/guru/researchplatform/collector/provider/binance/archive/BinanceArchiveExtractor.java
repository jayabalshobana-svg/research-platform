package com.guru.researchplatform.collector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.exception.BinanceException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BinanceArchiveExtractor {

    public Path extractCsv(Path zipFile) {

        Objects.requireNonNull(zipFile, "zipFile cannot be null");

        if (!Files.exists(zipFile)) {
            throw new BinanceException(
                    "Archive file does not exist: " + zipFile);
        }

        Path outputDirectory = zipFile.getParent()
                .resolve(removeExtension(zipFile.getFileName().toString()));

        try (InputStream inputStream = Files.newInputStream(zipFile);
             ZipInputStream zipInputStream =
                     new ZipInputStream(inputStream)) {

            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {

                // We only need the CSV file
                if (!entry.isDirectory()
                        && entry.getName().endsWith(".csv")) {

                    Path csvFile = outputDirectory
                            .resolve(Path.of(entry.getName()).getFileName());

                    Files.createDirectories(outputDirectory);

                    Files.copy(
                            zipInputStream,
                            csvFile,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    return csvFile;
                }
            }

            throw new BinanceException(
                    "No CSV file found inside archive: " + zipFile);

        } catch (IOException e) {
            throw new BinanceException(
                    "Failed to extract archive: " + zipFile,
                    e
            );
        }
    }

    private String removeExtension(String fileName) {

        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, extensionIndex);
    }
}
