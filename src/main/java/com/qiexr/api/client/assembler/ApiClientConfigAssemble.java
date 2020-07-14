package com.qiexr.api.client.assembler;

import com.netflix.hystrix.*;
import com.qiexr.api.client.config.HystrixCommandProperties;
import com.qiexr.api.client.config.HystrixThreadProperties;
import com.qiexr.api.client.config.RequestOptionsProperties;
import com.qiexr.api.client.interceptors.HttpRequestLogInterceptor;
import com.qiexr.api.client.interceptors.HttpResponseLogInterceptor;
import com.qiexr.api.client.interceptors.XrContextInterceptor;
import com.qiexr.api.client.utils.AuthUtil;
import com.xingren.reaper.interceptors.ImplicitAccessTokenInterceptor;
import com.xingren.v.logging.annotations.Slf4j;
import feign.Feign;
import feign.Request;
import feign.hystrix.SetterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xinshuai
 * @description
 * @since 2020-07-14 16:09
 */
@Slf4j
@Configuration
@ConditionalOnClass(Feign.class)
@EnableConfigurationProperties({HystrixCommandProperties.class, HystrixThreadProperties.class, RequestOptionsProperties.class})
public class ApiClientConfigAssemble {

    @Autowired
    private HystrixCommandProperties hystrixCommandProperties;

    @Autowired
    private HystrixThreadProperties hystrixThreadProperties;

    @Autowired
    private RequestOptionsProperties requestOptionsProperties;

    @Value("${xingren.reaper.host}")
    private String reaperHost;

    @Value("${xingren.reaper.clientId}")
    private String clientId;

    @Value("${xingren.secret}")
    private String xingrenSecret;

    @Bean
    public ImplicitAccessTokenInterceptor accessTokenInterceptor() {
        return new ImplicitAccessTokenInterceptor(reaperHost, clientId);
    }

    /**
     * 获取 SetterFactory
     * @param serviceClient
     * @return
     */
    public SetterFactory setterFactory(String serviceClient) {
        return (target, method) -> {
            HystrixCommandProperties.HystrixCommandConfiguration commandConfig = commandConfig(serviceClient);
            HystrixThreadProperties.HystrixThreadConfiguration threadConfig = threadConfig(serviceClient);
            return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandConfig.getGroupKey()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandConfig.getCommandKey()))
                    .andCommandPropertiesDefaults(com.netflix.hystrix.HystrixCommandProperties.Setter()
                            .withCircuitBreakerEnabled(commandConfig.getCircuitBreakerEnabled())
                            .withExecutionTimeoutEnabled(commandConfig.getExecutionTimeoutEnabled())
                            .withExecutionTimeoutInMilliseconds(commandConfig.getExecutionTimeoutInMilliseconds())
                            .withMetricsRollingStatisticalWindowInMilliseconds(commandConfig.getMetricsTimeInMilliseconds())
                            .withMetricsRollingPercentileWindowInMilliseconds(commandConfig.getMetricsTimeInMilliseconds())
                            .withMetricsRollingStatisticalWindowBuckets(commandConfig.getMetricsNumBuckets())
                            .withMetricsRollingPercentileBucketSize(commandConfig.getMetricsNumBuckets())
                            .withCircuitBreakerRequestVolumeThreshold(commandConfig.getCircuitBreakerRequestVolumeThreshold())
                            .withCircuitBreakerSleepWindowInMilliseconds(commandConfig.getCircuitBreakerSleepWindowInMilliseconds())
                            .withCircuitBreakerErrorThresholdPercentage(commandConfig.getCircuitBreakerErrorThresholdPercentage()))
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadConfig.getGroupKey()))
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                            .withCoreSize(threadConfig.getCoreSize()).withMaximumSize(threadConfig.getMaximumSize())
                            .withKeepAliveTimeMinutes(threadConfig.getKeepAliveTimeMinutes())
                            .withAllowMaximumSizeToDivergeFromCoreSize(threadConfig.getAllowMaximumSizeToDivergeFromCoreSize())
                            .withMaxQueueSize(threadConfig.getMaxQueueSize())
                            .withQueueSizeRejectionThreshold(threadConfig.getQueueSizeRejectionThreshold())
                    );
        };
    }

    /**
     * 获取 HystrixCommandProperties.HystrixCommandConfiguration 的配置
     * @param serviceClient
     * @return
     */
    private HystrixCommandProperties.HystrixCommandConfiguration commandConfig(String serviceClient) {
        HystrixCommandProperties.HystrixCommandConfiguration commandConfig;
        if (hystrixCommandProperties.getConfig().containsKey(serviceClient)) {
            commandConfig = hystrixCommandProperties.getConfig().get(serviceClient);
        } else {
            commandConfig = hystrixCommandProperties.getConfig().get("default");
            log.warn("Currently using default HystrixCommandConfiguration!");
        }
        return commandConfig;
    }

    /**
     * 获取 HystrixThreadProperties.HystrixThreadConfiguration 的配置
     * @param serviceClient
     * @return
     */
    private HystrixThreadProperties.HystrixThreadConfiguration threadConfig(String serviceClient) {
        HystrixThreadProperties.HystrixThreadConfiguration threadConfig;
        if (hystrixThreadProperties.getConfig().containsKey(serviceClient)) {
            threadConfig = hystrixThreadProperties.getConfig().get(serviceClient);
        } else {
            threadConfig = hystrixThreadProperties.getConfig().get("default");
            log.warn("Currently using default HystrixThreadConfiguration!");
        }
        return threadConfig;
    }

    /**
     * 获取 okhttp3.OkHttpClient
     * @param serviceClient
     * @return
     */
    public okhttp3.OkHttpClient okHttpClient(String serviceClient) {
        return new okhttp3.OkHttpClient.Builder()
                .addInterceptor(accessTokenInterceptor())
                .addNetworkInterceptor(new HttpRequestLogInterceptor())
                .addNetworkInterceptor(new HttpResponseLogInterceptor())
//                .addInterceptor(new AuthorizationInterceptor(serviceClient, clientId, xingrenSecret)) // TODO 去掉
                .addInterceptor((chain) -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder().header("token",
                            AuthUtil.generateApiToken(xingrenSecret)).build();
                    return chain.proceed(request);
                })
                .addInterceptor(new XrContextInterceptor())
                .build();
    }

    /**
     * 获取 Request.Options(超时时间)
     * @param serviceClient
     * @return
     */
    public Request.Options requestOptions(String serviceClient) {
        RequestOptionsProperties.RequestOptionsConfiguration optionsConfiguration;
        if (requestOptionsProperties.getConfig().containsKey(serviceClient)) {
            optionsConfiguration = requestOptionsProperties.getConfig().get(serviceClient);
        } else {
            optionsConfiguration = requestOptionsProperties.getConfig().get("default");
            log.warn("Currently using default RequestOptionsConfiguration!");
        }
        return new Request.Options(optionsConfiguration.getConnectTimeoutMillis(), optionsConfiguration.getReadTimeoutMillis());
    }

}
