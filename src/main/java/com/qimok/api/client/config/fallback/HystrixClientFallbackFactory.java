package com.qimok.api.client.config.fallback;

import lombok.extern.slf4j.Slf4j;
import feign.hystrix.FallbackFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 默认的降级策略
 *
 * @author qimok
 * @since 2020-07-16
 */
@Slf4j
public class HystrixClientFallbackFactory<T> implements FallbackFactory<T> {

    private Class<T> interfaceType;

    private Throwable cause;

    public HystrixClientFallbackFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T create(Throwable throwable) {
        this.cause = throwable;
        Class<T> clazz = interfaceType;
        InvocationHandler invocationHandler = new HystrixFallbackClientMethodProxy();
        Object fallback = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, invocationHandler);
        return (T) fallback;
    }

    public class HystrixFallbackClientMethodProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            final Class<?> clazz = method.getDeclaringClass();
            if (Object.class.equals(clazz)) {
                try {
                    return method.invoke(this, args);
                } catch (Throwable t) {
                    log.error("invoke method failed:", t);
                }
            } else {
                if (!(cause instanceof RuntimeException && cause.getMessage() == null)) {
                    String serviceName = interfaceType.getSimpleName();
                    log.error(String.format("\n\nFallback class:【%s】，method:【%s】，message >>",
                            serviceName, method.getName()), cause);
                }
            }
            return "error";
        }

    }

}
