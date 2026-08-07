package com.guru.researchplatform.collector.infrastructure.http;

import org.springframework.http.HttpMethod;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

// 1. Spec to hold request configurations
public record HttpRequestSpec(

        URI uri,

        HttpMethod method,

        Map<String, String> headers,

        Duration timeout

) {
}
