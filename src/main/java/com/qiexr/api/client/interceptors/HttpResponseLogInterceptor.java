package com.qiexr.api.client.interceptors;

import com.qiexr.api.client.utils.OkHttpUtils;
import com.xingren.v.logging.annotations.Slf4j;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * 打印响应日志
 * Created by Jearton on 16/7/14.
 */
@Slf4j(module = "vclient")
@EqualsAndHashCode
public class HttpResponseLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 1.发送请求
        long startNs = nanoTime();
        Request request = chain.request();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            _outputResponseLog(request, e);
            throw e;
        }
        long tookMs = NANOSECONDS.toMillis(nanoTime() - startNs);

        // 2.响应日志
        _outputResponseLog(response, tookMs);
        return response;
    }

    /**
     * 输出正常响应日志
     */
    private void _outputResponseLog(@NonNull Response response, long tookMs) throws IOException {
        HttpResponseLog.HttpResponseLogBuilder logBuilder = HttpResponseLog.builder();
        // code
        logBuilder.code(response.code());

        // msg
        logBuilder.msg(response.message());

        // url
        logBuilder.url(Objects.toString(response.request().url()));

        // tookMs
        logBuilder.tookMs(tookMs);

        // header
        Headers headers;
        Response networkResponse = response.networkResponse();
        if (networkResponse != null) {
            headers = networkResponse.headers();
        } else {
            headers = response.headers();
        }
        for (int i = 0, count = headers.size(); i < count; i++) {
            logBuilder.header(headers.name(i) + ": " + headers.value(i));
        }

        // body
        logBuilder.body(OkHttpUtils.getBody(response).orElse(null));
        log.info(logBuilder.build().toString());
    }

    /**
     * 输出异常响应日志
     */
    private void _outputResponseLog(@NonNull Request request, Exception e) {
        HttpResponseLog.HttpResponseLogBuilder logBuilder = HttpResponseLog.builder();
        // url
        logBuilder.url(Objects.toString(request.url()));

        // errorMsg
        logBuilder.errorMsg(e.getMessage());
        log.error(logBuilder.build().toString(), e);
    }

}
