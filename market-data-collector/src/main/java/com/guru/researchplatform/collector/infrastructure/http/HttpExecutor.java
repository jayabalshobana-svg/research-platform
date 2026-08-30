package com.guru.researchplatform.collector.infrastructure.http;

import java.util.function.Function;

public interface HttpExecutor {

    <T> HttpResult<T> execute(
            HttpRequestSpec requestSpec,
            Function<String, T> mapper
    );

    HttpResult<byte[]> download(
            HttpRequestSpec requestSpec
    );

}
