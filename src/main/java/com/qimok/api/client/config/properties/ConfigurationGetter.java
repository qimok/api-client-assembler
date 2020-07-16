package com.qimok.api.client.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

/**
 * 自定义配置获取
 *
 * @author qimok
 * @since 2020-07-16
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({HystrixCommandProperties.class, HystrixThreadProperties.class,
        RequestOptionsProperties.class, AuthorizationProperties.class, FeignRetryerProperties.class})
public class ConfigurationGetter {

    @Autowired
    private HystrixCommandProperties hystrixCommandProperties;

    @Autowired
    private HystrixThreadProperties hystrixThreadProperties;

    @Autowired
    private AuthorizationProperties authorizationProperties;

    @Autowired
    private RequestOptionsProperties requestOptionsProperties;

    @Autowired
    private FeignRetryerProperties feignRetryerProperties;

    /**
     * @param clientName feign client's name
     * @return 熔断配置
     */
    public HystrixCommandProperties.HystrixCommandConfiguration getCommandConfig(String clientName) {
        HystrixCommandProperties.HystrixCommandConfiguration commandConfig;
        Map<String, HystrixCommandProperties.HystrixCommandConfiguration> configMap
                = hystrixCommandProperties.getConfig();
        if (configMap.containsKey(clientName)) {
            commandConfig = configMap.get(clientName);
        } else {
            throw new IllegalArgumentException(
                    String.format("Client【%s】Must specify valid HystrixCommandConfiguration.", clientName));
        }
        return commandConfig;
    }

    /**
     * @param clientName feign client's name
     * @return 线程池配置
     */
    public HystrixThreadProperties.HystrixThreadConfiguration getThreadConfig(String clientName) {
        HystrixThreadProperties.HystrixThreadConfiguration threadConfig;
        Map<String, HystrixThreadProperties.HystrixThreadConfiguration> configMap = hystrixThreadProperties.getConfig();
        if (configMap.containsKey(clientName)) {
            threadConfig = hystrixThreadProperties.getConfig().get(clientName);
        } else {
            throw new IllegalArgumentException(
                    String.format("Client【%s】Must specify valid HystrixThreadConfiguration.", clientName));
        }
        return threadConfig;
    }

    /**
     * @param clientName feign client's name
     * @return 鉴权配置
     */
    public AuthorizationProperties.AuthorizationConfiguration getAuthorizationConfig(String clientName) {
        AuthorizationProperties.AuthorizationConfiguration authorizationConfig = null;
        Map<String, AuthorizationProperties.AuthorizationConfiguration> configMap = authorizationProperties.getConfig();
        if (configMap.containsKey(clientName)) {
            authorizationConfig = configMap.get(clientName);
        }
        return authorizationConfig;
    }

    /**
     * @param clientName feign client's name
     * @return 超时时间配置
     */
    public RequestOptionsProperties.RequestOptionsConfiguration getRequestOptionsConfig(String clientName) {
        RequestOptionsProperties.RequestOptionsConfiguration requestOptionsConfig;
        Map<String, RequestOptionsProperties.RequestOptionsConfiguration> configMap
                = requestOptionsProperties.getConfig();
        if (configMap.containsKey(clientName)) {
            requestOptionsConfig = configMap.get(clientName);
        } else {
            requestOptionsConfig = new RequestOptionsProperties.RequestOptionsConfiguration();
            log.warn(String.format("Currently【%s】using default（> %s <）!",
                    clientName, requestOptionsConfig.toString()));
        }
        return requestOptionsConfig;
    }

    /**
     * @param clientName feign client's name
     * @return 重试配置（与 Request.Options 同时使用）
     */
    public FeignRetryerProperties.FeignRetryerConfiguration getFeignRetryerConfig(String clientName) {
        FeignRetryerProperties.FeignRetryerConfiguration feignRetryerConfig = null;
        Map<String, FeignRetryerProperties.FeignRetryerConfiguration> configMap = feignRetryerProperties.getConfig();
        if (configMap.containsKey(clientName)) {
            feignRetryerConfig = configMap.get(clientName);
        }
        return feignRetryerConfig;
    }

}
