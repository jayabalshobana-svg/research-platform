package com.guru.researchplatform.marketdatacollector.integration;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.HttpResult;
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

        HttpResult<String> result =
                client.serverTime();

        System.out.println("================================");
        System.out.println("Binance Server Time Response");
        System.out.println("================================");
        System.out.println(result.body());
        System.out.println("HTTP Status : " + result.statusCode());
        System.out.println("Duration    : " + result.duration());
    }
}
