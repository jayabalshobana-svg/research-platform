package com.guru.researchplatform.collector.infrastructure.http;

import java.io.IOException;
import java.net.http.*;

import java.time.Duration;

import java.time.Instant;
import java.util.function.Function;

public class JavaHttpExecutor implements HttpExecutor {

    private final HttpClient httpClient;

    // Dependency Injection
    public JavaHttpExecutor(HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient cannot be null");
        }
        this.httpClient = httpClient;
    }

    /**
     * Core execution engine.
     * Executes the HTTP request, captures metadata, and transforms the raw string response body.
     */
    public <T> HttpResult<T> execute(HttpRequestSpec requestSpec, Function<String, T> mapper) {
        if (requestSpec == null || requestSpec.uri() == null) {
            throw new HttpRequestException("Invalid request specification: URI cannot be null");
        }

        // 1. Create HttpRequest from Spec
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(requestSpec.uri())
                .GET();

        if (requestSpec.headers() != null) {
            requestSpec.headers().forEach(requestBuilder::header);
        }

        HttpRequest request = requestBuilder.build();

        // 2. Track duration
        Instant startTime = Instant.now();
        try {
            // 3. Always pull raw body as String first to feed into the functional mapper
            java.net.http.HttpResponse<String> rawResponse =
                    httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);

            // 4. Map the String body using the provided functional mapper
            T transformedBody = null;
            if (rawResponse.body() != null && mapper != null) {
                transformedBody = mapper.apply(rawResponse.body());
            }

            // 5. Wrap everything neatly inside the infrastructure response object
            return new HttpResult<>(
                    requestSpec.uri(),
                    rawResponse.statusCode(),
                    transformedBody,
                    rawResponse.headers().map(),
                    duration
            );

        } catch (IOException e) {
            throw new HttpRequestException("Network infrastructure failure for URI: " + requestSpec.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpRequestException("HTTP Request execution interrupted for URI: " + requestSpec.uri(), e);
        }
    }

    @Override
    public HttpResult<byte[]> download(HttpRequestSpec requestSpec) {
        if (requestSpec == null || requestSpec.uri() == null) {
            throw new HttpRequestException("Invalid request specification: URI cannot be null");
        }

        // 1. Create HttpRequest from Spec
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(requestSpec.uri())
                .GET();

        if (requestSpec.headers() != null) {
            requestSpec.headers().forEach(requestBuilder::header);
        }

        HttpRequest request = requestBuilder.build();

        // 2. Track duration
        Instant startTime = Instant.now();
        try {
            // 3. Always pull raw body as byte Array
            java.net.http.HttpResponse<byte[]> rawResponse =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);

            // 4. Wrap everything neatly inside the infrastructure response object
            return new HttpResult<>(
                    requestSpec.uri(),
                    rawResponse.statusCode(),
                    rawResponse.body(),
                    rawResponse.headers().map(),
                    duration
            );

        } catch (IOException e) {
            throw new HttpRequestException("Network infrastructure failure for URI: " + requestSpec.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpRequestException("HTTP Request execution interrupted for URI: " + requestSpec.uri(), e);
        }

    }
}