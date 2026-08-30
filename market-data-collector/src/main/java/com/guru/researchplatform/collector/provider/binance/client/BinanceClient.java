package com.guru.researchplatform.collector.provider.binance.client;

import com.guru.researchplatform.collector.infrastructure.http.HttpExecutor;
import com.guru.researchplatform.collector.infrastructure.http.HttpMethod;
import com.guru.researchplatform.collector.infrastructure.http.HttpRequestSpec;
import com.guru.researchplatform.collector.infrastructure.http.HttpResult;
import com.guru.researchplatform.collector.provider.binance.configuration.BinanceProperties;
import com.guru.researchplatform.collector.provider.binance.exception.BinanceException;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class BinanceClient {

    private final HttpExecutor httpExecutor;
    private final BinanceProperties properties;

    public BinanceClient(HttpExecutor httpExecutor,
                         BinanceProperties properties) {

        this.httpExecutor = Objects.requireNonNull(httpExecutor);
        this.properties = Objects.requireNonNull(properties);
    }

    public HttpResult<String> serverTime() {

        HttpRequestSpec request = new HttpRequestSpec(
                properties.apiBaseUri().resolve(BinanceEndpoints.SERVER_TIME),
                HttpMethod.GET,
                Map.of(),
                Duration.ofSeconds(10)
        );

        HttpResult<String> result =
                httpExecutor.execute(request, Function.identity());

        if (!result.isSuccessful()) {
            throw new BinanceException(
                    "Failed to retrieve server time. HTTP Status: "
                            + result.statusCode());
        }

        return result;
    }

    public void ping() {

        HttpRequestSpec request = new HttpRequestSpec(
                properties.apiBaseUri().resolve(BinanceEndpoints.PING),
                HttpMethod.GET,
                Map.of(),
                Duration.ofSeconds(10)
        );

        HttpResult<String> result =
                httpExecutor.execute(request, Function.identity());

        if (!result.isSuccessful()) {
            throw new BinanceException(
                    "Ping failed. HTTP Status: " + result.statusCode()
            );
        }
    }

}