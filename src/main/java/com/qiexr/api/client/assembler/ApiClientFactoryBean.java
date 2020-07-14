package com.qiexr.api.client.assembler;

import com.qiexr.api.client.config.FeignJsonConfiguration;
import com.qiexr.api.common.annotation.ApiContract;
import com.xingren.v.logging.annotations.Slf4j;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.hystrix.HystrixFeign;
import feign.okhttp.OkHttpClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.util.StringUtils;

/**
 * @author Xinshuai
 * @since 2020-07-03 13:29
 */
@Slf4j
@AllArgsConstructor
@ConditionalOnClass(Feign.class)
public class ApiClientFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    @Autowired
    private Decoder feignJsonDecoder;

    @Autowired
    private Encoder feignJsonEncoder;

    @Autowired
    private ApiClientConfigAssemble configAssemble;

    @Autowired
    private Logger.Level feignLoggerLevel;

    @Override
    @ConditionalOnMissingBean(FeignJsonConfiguration.class)
    public T getObject() {
        ApiContract annotation = interfaceType.getAnnotation(ApiContract.class);
        String serviceClient = annotation.value();
        if (StringUtils.isEmpty(serviceClient)) {
            throw new IllegalStateException(interfaceType + "的 value 未配置！");
        }
        String host = annotation.host();
        if (StringUtils.isEmpty(host)) {
            throw new IllegalStateException(interfaceType + "的 Host 未配置！");
        }
        return HystrixFeign.builder()
                .contract(new SpringMvcContract())
                .decoder(feignJsonDecoder)
                .encoder(feignJsonEncoder)
                .client(new OkHttpClient(configAssemble.okHttpClient(serviceClient)))
                .options(configAssemble.requestOptions(serviceClient))
                .setterFactory(configAssemble.setterFactory(serviceClient))
                .logLevel(feignLoggerLevel)
                .target(interfaceType, host);
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