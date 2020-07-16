package com.qimok.api.client.assembler;

import com.qimok.api.client.annotation.ApiContract;
import com.qimok.api.client.config.fallback.HystrixClientFallbackFactory;
import lombok.extern.slf4j.Slf4j;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.hystrix.HystrixFeign;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 自定义装配 FeignClient
 *
 * @author qimok
 * @since 2020-07-14
 */
@Slf4j
@AllArgsConstructor
@AutoConfigureAfter(FeignAutoConfiguration.class)
public class ApiClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware {

    private Class<T> interfaceType;

    @Autowired
    private ConfigurationAssemble configAssembler;

    @Autowired
    private Feign.Builder feignBuilder;

    @Autowired
    private Decoder feignJsonDecoder;

    @Autowired
    private Encoder feignJsonEncoder;

    @Autowired
    private Logger.Level loggerLevel;

    @Autowired
    private feign.Logger feignLogger;

    @Autowired
    private Client feignClient;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public T getObject() {
        ApiContract annotation = interfaceType.getAnnotation(ApiContract.class);
        String url = configAssembler.getUrl(interfaceType, annotation, environment);
        String clientName = configAssembler.getClientName(interfaceType, annotation);
        Feign.Builder builder = feignBuilder
                .contract(new SpringMvcContract())
                .decoder(feignJsonDecoder)
                .encoder(feignJsonEncoder)
                .client(feignClient)
                .requestInterceptors(configAssembler.getRequestInterceptor(clientName))
                .options(configAssembler.getRequestOptions(clientName))
                .retryer(configAssembler.getRetryer(clientName))
                .logLevel(loggerLevel)
                .logger(feignLogger);
        if (builder instanceof HystrixFeign.Builder) {
            // 启动熔断、降级
            return (T)((HystrixFeign.Builder) builder)
                    .setterFactory(configAssembler.getSetterFactory(clientName))
                    .target(interfaceType, url, new HystrixClientFallbackFactory(interfaceType));
        }
        return builder.target(interfaceType, url);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}