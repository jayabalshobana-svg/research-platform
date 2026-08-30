package com.guru.researchplatform.marketdatacollector.integration;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.JavaHttpExecutor;
import com.guru.researchplatform.collector.provider.binance.archive.BinanceArchiveDownloader;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceDefaults;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BinanceArchiveDownloaderTest {

    @Test
    void shouldBuildCorrectDownloadLocation() throws IOException {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor executor = new JavaHttpExecutor(httpClient);

        BinanceProperties properties = BinanceDefaults.properties();

        BinanceArchiveDownloader downloader =
                new BinanceArchiveDownloader(executor, properties);

        Asset btc = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.SPOT,
                AssetStatus.ACTIVE
        );

        Path path =
                downloader.downloadMonthlyArchive(
                        btc,
                        Timeframe.ONE_MINUTE,
                        YearMonth.of(2024, 1));

        assertTrue(Files.exists(path));

        assertTrue(Files.size(path) > 0);

        System.out.println(Files.size(path));

        System.out.println();
        System.out.println("===============================");
        System.out.println("Download Path");
        System.out.println("===============================");
        System.out.println(path);
    }
}