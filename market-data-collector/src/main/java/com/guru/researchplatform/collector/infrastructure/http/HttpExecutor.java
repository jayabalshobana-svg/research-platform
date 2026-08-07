package com.guru.researchplatform.collector.infrastructure.http;

import java.util.function.Function;

public interface HttpExecutor {

    /*HttpResponse<String> get(URI uri);

    HttpResponse<String> get(URI uri, Map<String, String> headers);*/

    <T> HttpResult<T> execute(
            HttpRequestSpec requestSpec,
            Function<String, T> mapper
    );

}
