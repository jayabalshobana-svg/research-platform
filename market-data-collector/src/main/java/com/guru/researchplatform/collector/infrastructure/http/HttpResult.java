package com.guru.researchplatform.collector.infrastructure.http;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public record HttpResult<T>(
        URI requestUri,
        int statusCode,
        T body,
        Map<String, List<String>> headers,
        Duration duration
) {
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

}