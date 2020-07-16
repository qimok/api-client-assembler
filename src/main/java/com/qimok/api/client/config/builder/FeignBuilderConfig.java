package com.qimok.api.client.config.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qimok.api.client.config.logger.XrFeignLogger;
import com.qimok.api.client.jackson.JsonObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.hystrix.HystrixFeign;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Feign 属性配置
 *
 * @author qimok
 * @since 2020-07-14
 */
@Configuration
public class FeignBuilderConfig {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "feign.hystrix.enabled", havingValue = "false")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = true)
    public Feign.Builder feignHystrixBuilder() {
        return HystrixFeign.builder();
    }

    @Primary
    @Bean
    public Decoder feignJsonDecoder() {
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    @Primary
    @Bean
    public Encoder feignJsonEncoder() {
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(objectFactory);
    }

    private ObjectMapper customObjectMapper() {
        return JsonObjectMapper.create();
    }

    @Primary
    @Bean
    Logger.Level getLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Primary
    @Bean
    feign.Logger getXrFeignLogger() {
        return new XrFeignLogger();
    }

    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

}
