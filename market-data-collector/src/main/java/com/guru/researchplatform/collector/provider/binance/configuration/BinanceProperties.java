package com.guru.researchplatform.collector.provider.binance.configuration;

import java.net.URI;
import java.time.Duration;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public record BinanceProperties(
        URI baseUri,
        Duration connectTimeout,
        Duration requestTimeout,
        String userAgent
) {

    public BinanceProperties {
        Objects.requireNonNull(baseUri);
        Objects.requireNonNull(connectTimeout);
        Objects.requireNonNull(requestTimeout);
        Objects.requireNonNull(userAgent);

        if (connectTimeout.isNegative()) {
            throw new IllegalArgumentException("connectTimeout cannot be negative");
        }

        if (requestTimeout.isNegative()) {
            throw new IllegalArgumentException("requestTimeout cannot be negative");
        }

        if (userAgent.isBlank()) {
            throw new IllegalArgumentException("userAgent cannot be blank");
        }
    }
}
