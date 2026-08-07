package com.guru.researchplatform.collector.provider.binance.configuration;

import java.net.URI;
import java.time.Duration;

public final class BinanceDefaults {

    private BinanceDefaults() {
    }

    public static BinanceProperties properties() {
        return new BinanceProperties(
                URI.create("https://api.binance.com"),
                Duration.ofSeconds(10),
                Duration.ofSeconds(30),
                "ResearchPlatform/1.0"
        );
    }
}