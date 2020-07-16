package com.qimok.api.client.config.properties;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import static com.qimok.api.client.Constants.CLIENT_DELIMITER;

/**
 * 自定义配置线程池
 *
 * @author qimok
 * @since 2020-07-14
 */
@ConfigurationProperties("feign.thread")
@Data
public class HystrixThreadProperties {

    private Map<String, HystrixThreadConfiguration> config = new HashMap<>();

    public Map<String, HystrixThreadConfiguration> getConfig() {
        return this.config;
    }

    /**
     * 获取、解析、重新赋值配置
     */
    public void setConfig(Map<String, HystrixThreadConfiguration> config) {
        Map<String, HystrixThreadConfiguration> configurationMap = Maps.newHashMap();
        Iterator<Map.Entry<String, HystrixThreadConfiguration>> iterator = config.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HystrixThreadConfiguration> entry = iterator.next();
            String keyStr = entry.getKey().trim();
            HystrixThreadConfiguration value = entry.getValue();
            if (StringUtils.isBlank(value.getGroupKey())) {
                throw new IllegalArgumentException("Must specify a valid thread group key.");
            }
            if (keyStr.contains(CLIENT_DELIMITER)) {
                // 多个 Client 配置共用的情况
                String[] keys = keyStr.split(CLIENT_DELIMITER);
                for (String key : keys) {
                    setConfig(configurationMap, key, value);
                }
            } else {
                // 单个 Client 配置的情况
                setConfig(configurationMap, keyStr, value);
            }
        }
        this.config = configurationMap;
    }

    private void setConfig(Map<String, HystrixThreadConfiguration> configurationMap,
                           String key, HystrixThreadConfiguration value) {
        if (configurationMap.containsKey(key)) {
            throw new IllegalArgumentException(
                    String.format("feign.thread.config.%s... is duplicate!", key));
        } else {
            configurationMap.put(key, value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HystrixThreadProperties that = (HystrixThreadProperties) o;
        return Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config);
    }

    public static class HystrixThreadConfiguration {

        /**
         * 线程池名, 如果threadGroupKey一样, 则会使用同一个线程池。
         * 同一个线程池如果有多个配置, 则可能无法确保使用哪个配置, 所以请确保不要配置多个同名的线程池。
         */
        private String groupKey;

        /**
         * 线程池Core大小
         */
        private Integer coreSize = 5;

        /**
         * 线程池Maximum大小, 必须设置allowMaximumSizeToDivergeFromCoreSize为true才生效, 否则等于coreSize
         */
        private Integer maximumSize = 10;

        /**
         * 线程池KeepAlive时间, 线程空闲超过该时间会被回收, 直到线程为coreSize
         */
        private Integer keepAliveTimeMinutes = 2;

        /**
         * 是否允许线程池coreSize和maximumSize不同
         */
        private Boolean allowMaximumSizeToDivergeFromCoreSize = true;

        /**
         * 线程池队列大小
         */
        private Integer maxQueueSize = 20;

        /**
         * 线程池队列限制, 大于此值即使队列还有空间也会被Reject
         */
        private Integer queueSizeRejectionThreshold = 20;

        public String getGroupKey() {
            return groupKey;
        }

        public void setGroupKey(String groupKey) {
            this.groupKey = groupKey;
        }

        public Integer getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(Integer coreSize) {
            this.coreSize = coreSize;
        }

        public Integer getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(Integer maximumSize) {
            this.maximumSize = maximumSize;
        }

        public Integer getKeepAliveTimeMinutes() {
            return keepAliveTimeMinutes;
        }

        public void setKeepAliveTimeMinutes(Integer keepAliveTimeMinutes) {
            this.keepAliveTimeMinutes = keepAliveTimeMinutes;
        }

        public Boolean getAllowMaximumSizeToDivergeFromCoreSize() {
            return allowMaximumSizeToDivergeFromCoreSize;
        }

        public void setAllowMaximumSizeToDivergeFromCoreSize(Boolean allowMaximumSizeToDivergeFromCoreSize) {
            this.allowMaximumSizeToDivergeFromCoreSize = allowMaximumSizeToDivergeFromCoreSize;
        }

        public Integer getMaxQueueSize() {
            return maxQueueSize;
        }

        public void setMaxQueueSize(Integer maxQueueSize) {
            this.maxQueueSize = maxQueueSize;
        }

        public Integer getQueueSizeRejectionThreshold() {
            return queueSizeRejectionThreshold;
        }

        public void setQueueSizeRejectionThreshold(Integer queueSizeRejectionThreshold) {
            this.queueSizeRejectionThreshold = queueSizeRejectionThreshold;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HystrixThreadConfiguration that = (HystrixThreadConfiguration) o;
            return Objects.equals(groupKey, that.groupKey) &&
                    Objects.equals(coreSize, that.coreSize) &&
                    Objects.equals(maximumSize, that.maximumSize) &&
                    Objects.equals(keepAliveTimeMinutes, that.keepAliveTimeMinutes) &&
                    Objects.equals(allowMaximumSizeToDivergeFromCoreSize, that.allowMaximumSizeToDivergeFromCoreSize) &&
                    Objects.equals(maxQueueSize, that.maxQueueSize) &&
                    Objects.equals(queueSizeRejectionThreshold, that.queueSizeRejectionThreshold);
        }

        @Override
        public int hashCode() {
            return Objects.hash(groupKey, coreSize, maximumSize, keepAliveTimeMinutes,
                    allowMaximumSizeToDivergeFromCoreSize, maxQueueSize, queueSizeRejectionThreshold);
        }

    }

}
