package com.qiexr.api.client.interceptors;

import com.qiexr.api.client.utils.OkHttpUtils;
import com.xingren.service.ServiceAuth;
import com.xingren.v.auth.utils.AuthUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.tuple.Pair;
import java.io.IOException;
import java.util.Map;
import static com.xingren.v.auth.Signature.HMAC_SHA256;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toMap;

/**
 * 插入服务鉴权头
 * Created by Jearton on 16/6/15.
 */
public class AuthorizationInterceptor implements Interceptor {

    private static String AUTH_PATTERN = "XR1-HMAC-SHA256 Credential={0}/{1}/{Service},Signature={2}";

    private final String serviceName;

    private final String clientId;

    private final String clientSecret;

    private final String pattern;

    /**
     * @param serviceName
     * @param clientId
     * @param clientSecret
     */
    public AuthorizationInterceptor(String serviceName, String clientId, String clientSecret) {
        this.serviceName = serviceName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.pattern = AUTH_PATTERN.replace("{Service}", serviceName);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 2.添加请求头
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", getAuthorization(chain.request()))
                .build();

        // 3.继续处理请求
        return chain.proceed(request);
    }

    /**
     * 获取鉴权请求头
     *
     * @param request 请求对象
     */
    private String getAuthorization(Request request) {
        if (request == null || request.url() == null) {
            return "";
        }

        long timestamp = System.currentTimeMillis();
        HttpUrl url = request.url();
        String uri = url.uri().getPath();
        String method = request.method().toUpperCase();
        Map<String, String[]> queryMap = url.queryParameterNames().stream()
                .map(name -> Pair.of(name, url.queryParameterValues(name).stream().toArray(String[]::new)))
                .collect(toMap(Pair::getKey, Pair::getValue));
        String body = OkHttpUtils.getBody(request).orElse("");

        ServiceAuth auth = new ServiceAuth(timestamp, serviceName, method, uri, queryMap, body);
        String signature = AuthUtils.auth(clientSecret, auth, HMAC_SHA256);

        return format(pattern, clientId, Long.toString(timestamp), signature);
    }

}
