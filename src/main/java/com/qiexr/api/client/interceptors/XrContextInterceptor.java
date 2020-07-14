package com.qiexr.api.client.interceptors;

import com.xingren.common.utils.EncodeUtils;
import com.xingren.v.auth.XrContextHolder;
import com.xingren.v.auth.context.XrContext;
import com.xingren.v.logging.annotations.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

/**
 * 插入SDK版本
 */
@Slf4j(module = "vclient")
public class XrContextInterceptor implements Interceptor {


    public XrContextInterceptor() { }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Optional<XrContext> contextOpt = XrContextHolder.get();
        if (contextOpt.isPresent()) {
            try {
                log.debug("RetrofitInterceptor: Service context set to Request: {" + contextOpt.get().toLog() + "}");
                String json = contextOpt.get().toJson();
                String encodedJson = EncodeUtils.encodeURIComponent(json);
                Request request = chain.request().newBuilder()
                        .addHeader(XrContext.XR_CONTEXT_HEADER, encodedJson)
                        .build();
                return chain.proceed(request);
            } catch (Exception e) {
                log.error("RetrofitInterceptor: Service context exception.", e);
                return chain.proceed(chain.request());
            }
        } else {
            //todo 暂时不抛异常，后期要求必须存在XrContext
            Request request = chain.request();
            log.warn("RetrofitInterceptor: Service context not exists. Please fix this! [url=" + request.url() + "]");
            return chain.proceed(request);
//            throw new NoContextCallExcception();
        }

    }
}