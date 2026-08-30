package com.guru.researchplatform.collector.provider.binance.archive;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.HttpRequestSpec;
import com.guru.researchplatform.collector.infrastructure.http.HttpResult;
import com.guru.researchplatform.collector.provider.binance.client.BinanceEndpoints;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.collector.provider.binance.exception.BinanceException;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.Timeframe;
import com.guru.researchplatform.collector.infrastructure.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;


public class BinanceArchiveDownloader {
    private final HttpExecutor httpExecutor;
    private final BinanceProperties properties;
    public BinanceArchiveDownloader(HttpExecutor httpExecutor,
                                    BinanceProperties properties) {

        this.httpExecutor = Objects.requireNonNull(httpExecutor);
        this.properties = Objects.requireNonNull(properties);
    }

    public Path downloadMonthlyArchive(
            Asset asset,
            Timeframe timeframe,
            YearMonth month) {

        HttpRequestSpec request = buildDownloadRequest(asset, timeframe, month);

        HttpResult<byte[]> result =
                httpExecutor.download(request);

        if (!result.isSuccessful()) {
            throw new BinanceException(
                    "Archive download failed. HTTP Status: "
                            + result.statusCode());
        }
        Path localPath = buildLocalPath(asset, timeframe, month);
        System.out.println("Local Path   : " + localPath);
        try {

            Files.createDirectories(localPath.getParent());

            Files.write(localPath, result.body());

        } catch (IOException e) {

            throw new BinanceException(
                    "Failed to save archive: " + localPath,
                    e);

        }
        System.out.println("Local Path   : " + localPath);
        return localPath;
    }


    private String buildFileName(
            Asset asset,
            Timeframe timeframe,
            YearMonth month) {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(timeframe);
        Objects.requireNonNull(month);

        return String.format(
                "%s-%s-%s.zip",
                asset.symbol(),
                timeframe.apiValue(),
                month
        );
    }
    private URI buildDownloadUri(
            Asset asset,
            Timeframe timeframe,
            YearMonth month) {

        String fileName = buildFileName(asset, timeframe, month);

        String relativePath = String.format(
                "data/spot/monthly/klines/%s/%s/%s",
                asset.symbol(),
                timeframe.apiValue(),
                fileName
        );

        return properties.archiveBaseUri().resolve(relativePath);
    }

    private Path buildLocalPath(
            Asset asset,
            Timeframe timeframe,
            YearMonth month) {

        return Path.of(
                "data",
                "archives",
                "spot",
                asset.symbol(),
                timeframe.apiValue(),
                buildFileName(asset, timeframe, month)
        );
    }

    private HttpRequestSpec buildDownloadRequest(
            Asset asset,
            Timeframe timeframe,
            YearMonth month) {
        return  new HttpRequestSpec(
                buildDownloadUri(asset, timeframe, month),
                HttpMethod.GET,
                Map.of(),
                Duration.ofSeconds(50));
    }

}

