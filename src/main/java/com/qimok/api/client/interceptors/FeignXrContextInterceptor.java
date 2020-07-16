package com.qimok.api.client.interceptors;

import lombok.extern.slf4j.Slf4j;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 此拦截器用于资源鉴权，填资源鉴权的头
 */
@Slf4j
public class FeignXrContextInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 设置 XRC
    }

}
