package com.guru.researchplatform.marketdatacollector.integration;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.JavaHttpExecutor;
import com.guru.researchplatform.collector.provider.binance.client.BinanceClient;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceDefaults;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.Duration;

public class BinanceConnectionTest {

    @Test
    void TestPing() {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpExecutor executor =
                new JavaHttpExecutor(httpClient);

        BinanceProperties properties =
                BinanceDefaults.properties();

        BinanceClient client =
                new BinanceClient(executor, properties);

        client.ping();

        System.out.println("Binance connectivity successful.");
    }
}
