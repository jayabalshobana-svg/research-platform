package com.guru.researchplatform.collector.infrastructure.http;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public record HttpResult<T>(
        int statusCode,
        T body,
        Map<String, List<String>> headers,
        Duration duration
) {}