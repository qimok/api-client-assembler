package com.qiexr.api.client.interceptors;

import com.qiexr.api.client.utils.OkHttpUtils;
import com.xingren.v.logging.annotations.Slf4j;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import okhttp3.*;
import java.io.IOException;
import java.util.Objects;
import static okhttp3.Protocol.HTTP_1_1;

/**
 * 打印请求日志
 * Created by Jearton on 16/7/14.
 */
@Slf4j(module = "vclient")
@EqualsAndHashCode
public class HttpRequestLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 1.请求日志
        Request request = chain.request();
        _outputRequestLog(chain.connection(), request);

        // 2.发送请求, 接收响应
        return chain.proceed(request);
    }

    /**
     * 输出请求日志
     */
    private void _outputRequestLog(Connection connection, @NonNull Request request) throws IOException {
        HttpRequestLog.HttpRequestLogBuilder logBuilder = HttpRequestLog.builder();
        // protocol
        Protocol protocol = connection != null ? connection.protocol() : HTTP_1_1;
        logBuilder.protocol(Objects.toString(protocol));

        // method
        logBuilder.method(request.method());

        // url
        logBuilder.url(Objects.toString(request.url()));

        // headers
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            logBuilder.header(headers.name(i) + ": " + headers.value(i));
        }

        // body
        logBuilder.body(OkHttpUtils.getBody(request).orElse(null));
        log.info(logBuilder.build().toString());
    }
}
