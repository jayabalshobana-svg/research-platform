package com.guru.researchplatform.collector.provider.binance.client;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.HttpRequestSpec;
import com.guru.researchplatform.collector.infrastructure.http.HttpResult;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.collector.provider.binance.exception.BinanceException;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.http.HttpMethod;

public final class BinanceClient {

    private final HttpExecutor httpExecutor;
    private final BinanceProperties properties;

    public BinanceClient(HttpExecutor httpExecutor,
                         BinanceProperties properties) {

        this.httpExecutor = Objects.requireNonNull(httpExecutor);
        this.properties = Objects.requireNonNull(properties);
    }

    public void ping() {

        HttpRequestSpec request = new HttpRequestSpec(
                properties.baseUri().resolve(BinanceEndpoints.PING),
                HttpMethod.GET,
                Map.of(),
                Duration.ofSeconds(10)
        );

        HttpResult<String> result =
                httpExecutor.execute(request, Function.identity());

        if (result.statusCode() != 200) {
            throw new BinanceException(
                    "Ping failed. HTTP Status: " + result.statusCode());
        }
    }

}