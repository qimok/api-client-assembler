package com.qimok.api.client.assembler;

import com.google.common.collect.Lists;
import com.netflix.hystrix.*;
import com.qimok.api.client.annotation.ApiContract;
import com.qimok.api.client.config.properties.*;
import com.qimok.api.client.interceptors.FeignXrContextInterceptor;
import com.qimok.api.client.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import feign.*;
import feign.hystrix.SetterFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.env.Environment;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 配置组装
 *
 * @author qimok
 * @since 2020-07-14
 */
@Slf4j
@ConditionalOnClass(Feign.class)
public class ConfigurationAssemble {

    @Autowired
    private ConfigurationGetter configurationGetter;

    @Value("${reaper.clientId}")
    private String clientId;

    @Autowired
    private Retryer retryer;

    /**
     * 获取请求的 url
     *
     * @param interfaceType 当前处理的
     * @param annotation ApiContract 注解
     * @param environment 运行时环境
     * @return 请求域名
     */
    public String getUrl(Class interfaceType, ApiContract annotation, Environment environment) {
        String host = annotation.host();
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException(interfaceType + "的 Host 未配置！");
        }
        String url = resolve(annotation.host(), environment);
        return getUrl(url);
    }

    /**
     * Host To 域名
     */
    private String resolve(String value, Environment environment) {
        if (org.springframework.util.StringUtils.hasText(value)) {
            return environment.resolvePlaceholders(value);
        }
        return value;
    }

    private static String getUrl(String url) {
        if (org.springframework.util.StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }

    /**
     * 获取 ClientName
     *
     * @param interfaceType 处理的 Class
     * @param annotation ApiContract 注解
     * @return ClientName
     */
    public String getClientName(Class interfaceType, ApiContract annotation) {
        if (StringUtils.isBlank(annotation.value())) {
            throw new IllegalArgumentException(interfaceType + "的 value 未配置！");
        }
        return StringUtil.toClientName(annotation.value());
    }

    /**
     * 获取 SetterFactory
     * <p>
     *     熔断、线程池配置
     *
     * @param clientName feign client's name
     * @return 熔断、线程池配置工厂
     */
    public SetterFactory getSetterFactory(String clientName) {
        com.qimok.api.client.config.properties.HystrixCommandProperties.HystrixCommandConfiguration commandConfig = configurationGetter
                .getCommandConfig(clientName);
        HystrixThreadProperties.HystrixThreadConfiguration threadConfig = configurationGetter
                .getThreadConfig(clientName);
        return (target, method) ->
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandConfig.getGroupKey()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandConfig.getCommandKey()))
                .andCommandPropertiesDefaults(com.netflix.hystrix.HystrixCommandProperties.Setter()
                        .withCircuitBreakerEnabled(commandConfig.getCircuitBreakerEnabled())
                        .withExecutionTimeoutEnabled(commandConfig.getExecutionTimeoutEnabled())
                        .withExecutionTimeoutInMilliseconds(commandConfig.getExecutionTimeoutInMilliseconds())
                        .withMetricsRollingStatisticalWindowInMilliseconds(commandConfig.getMetricsTimeInMilliseconds())
                        .withMetricsRollingPercentileWindowInMilliseconds(commandConfig.getMetricsTimeInMilliseconds())
                        .withMetricsRollingStatisticalWindowBuckets(commandConfig.getMetricsNumBuckets())
                        .withMetricsRollingPercentileBucketSize(commandConfig.getMetricsNumBuckets())
                        .withCircuitBreakerRequestVolumeThreshold(commandConfig
                                .getCircuitBreakerRequestVolumeThreshold())
                        .withCircuitBreakerSleepWindowInMilliseconds(commandConfig
                                .getCircuitBreakerSleepWindowInMilliseconds())
                        .withCircuitBreakerErrorThresholdPercentage(commandConfig
                                .getCircuitBreakerErrorThresholdPercentage()))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadConfig.getGroupKey()))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(threadConfig.getCoreSize()).withMaximumSize(threadConfig.getMaximumSize())
                        .withKeepAliveTimeMinutes(threadConfig.getKeepAliveTimeMinutes())
                        .withAllowMaximumSizeToDivergeFromCoreSize(threadConfig
                                .getAllowMaximumSizeToDivergeFromCoreSize())
                        .withMaxQueueSize(threadConfig.getMaxQueueSize())
                        .withQueueSizeRejectionThreshold(threadConfig.getQueueSizeRejectionThreshold())
                );
    }

    /**
     * 获取 Request.Options(超时时间设置)
     *
     * @param clientName feign client's name
     * @return 超时配置
     */
    public Request.Options getRequestOptions(String clientName) {
        RequestOptionsProperties.RequestOptionsConfiguration requestOptionsConfig =
                configurationGetter.getRequestOptionsConfig(clientName);
        return new Request.Options(requestOptionsConfig.getConnectTimeoutMillis(),
                requestOptionsConfig.getReadTimeoutMillis(), requestOptionsConfig.getFollowRedirects());
    }

    /**
     * 获取重试配置
     *
     * @param clientName feign client's name
     * @return 重试策略
     */
    public Retryer getRetryer(String clientName) {
        FeignRetryerProperties.FeignRetryerConfiguration retryerConfig
                = configurationGetter.getFeignRetryerConfig(clientName);
        return retryerConfig == null ? retryer : new Retryer.Default(retryerConfig.getPeriod(),
                retryerConfig.getMaxPeriod(), retryerConfig.getMaxAttempts());
    }

    /**
     * 获取请求拦截器
     *
     * @param clientName feign client's name
     * @return 请求拦截器
     */
    public Iterable<RequestInterceptor> getRequestInterceptor(String clientName) {
        List<RequestInterceptor> requestInterceptors = Lists.newArrayList();
        // 设置鉴权
        AuthorizationProperties.AuthorizationConfiguration authorizationConfig
                = configurationGetter.getAuthorizationConfig(clientName);
        if (authorizationConfig == null) {
            log.warn("No authentication is configured!");
        } else {
            // 鉴权配置
        }
        // 设置 XRC
        requestInterceptors.add(new FeignXrContextInterceptor());
        return requestInterceptors;
    }

}
