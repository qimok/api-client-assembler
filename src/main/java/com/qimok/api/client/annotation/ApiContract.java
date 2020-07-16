package com.qimok.api.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qimok
 * @since 2020/07/05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiContract {

    /**
     * @return feign client's name.
     */
    String value() default "";

    /**
     * @return 请求的服务 host 占位符，例如：${xxx.consult-mid.host}
     */
    String host();

}
