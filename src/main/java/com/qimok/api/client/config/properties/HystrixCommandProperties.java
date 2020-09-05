package com.qimok.api.client.config.properties;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import static com.qimok.api.client.Constants.CLIENT_DELIMITER;

/**
 * 自定义配置熔断
 *
 * @author qimok
 * @since 2020-07-14
 */
@ConfigurationProperties(prefix = "feign.hystrix")
public class HystrixCommandProperties {

	private Map<String, HystrixCommandConfiguration> config = new HashMap<>();

	public Map<String, HystrixCommandConfiguration> getConfig() {
		return this.config;
	}

	/**
	 * 获取、解析、重新赋值配置
	 */
	public void setConfig(Map<String, HystrixCommandConfiguration> config) {
		Map<String, HystrixCommandConfiguration> configurationMap = Maps.newHashMap();
		Iterator<Map.Entry<String, HystrixCommandConfiguration>> iterator = config.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, HystrixCommandConfiguration> entry = iterator.next();
			String keyStr = entry.getKey().trim();
			HystrixCommandConfiguration value = entry.getValue();
			if (StringUtils.isBlank(value.getGroupKey()) || StringUtils.isBlank(value.getCommandKey())) {
				throw new IllegalArgumentException("Must specify valid group key and command key.");
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

	private void setConfig(Map<String, HystrixCommandConfiguration> configurationMap,
						   String key, HystrixCommandConfiguration value) {
		if (configurationMap.containsKey(key)) {
			throw new IllegalArgumentException(
					String.format("feign.hystrix.config.%s... is duplicate!", key));
		} else {
			configurationMap.put(key, value);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HystrixCommandProperties that = (HystrixCommandProperties) o;
		return Objects.equals(config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(config);
	}

	public static class HystrixCommandConfiguration {

		/**
		 * GroupKey, 用于设置Command的分组
		 */
		private String groupKey;

		/**
		 * CommandKey, 控制熔断和统计的最小单位
		 */
		private String commandKey;

		/**
		 * 是否检测超时
		 */
		private Boolean executionTimeoutEnabled = true;

		/**
		 * 请求超时时间
		 */
		private Integer executionTimeoutInMilliseconds = 1000;

		/**
		 * 是否开启熔断
		 */
		private Boolean circuitBreakerEnabled = true;

		/**
		 * 时间窗口内至少多少个请求, 才会开始判断熔断
		 */
		private Integer circuitBreakerRequestVolumeThreshold = 5;

		/**
		 * 熔断后间隔多长时间, 会开始再次尝试决定是否继续熔断
		 */
		private Integer circuitBreakerSleepWindowInMilliseconds = 5000;

		/**
		 * 错误百分比大于多少, 进行熔断
		 */
		private Integer circuitBreakerErrorThresholdPercentage = 50;

		/**
		 * 时间窗口大小
		 */
		private Integer metricsTimeInMilliseconds = 20000;

		/**
		 * 每个时间窗口的Bucket数, 必须能整除statsTimeInMilliseconds
		 */
		private Integer metricsNumBuckets = 10;

		public String getGroupKey() {
			return groupKey;
		}

		public void setGroupKey(String groupKey) {
			this.groupKey = groupKey;
		}

		public String getCommandKey() {
			return commandKey;
		}

		public void setCommandKey(String commandKey) {
			this.commandKey = commandKey;
		}

		public Boolean getExecutionTimeoutEnabled() {
			return executionTimeoutEnabled;
		}

		public void setExecutionTimeoutEnabled(Boolean executionTimeoutEnabled) {
			this.executionTimeoutEnabled = executionTimeoutEnabled;
		}

		public Integer getExecutionTimeoutInMilliseconds() {
			return executionTimeoutInMilliseconds;
		}

		public void setExecutionTimeoutInMilliseconds(Integer executionTimeoutInMilliseconds) {
			this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
		}

		public Boolean getCircuitBreakerEnabled() {
			return circuitBreakerEnabled;
		}

		public void setCircuitBreakerEnabled(Boolean circuitBreakerEnabled) {
			this.circuitBreakerEnabled = circuitBreakerEnabled;
		}

		public Integer getCircuitBreakerRequestVolumeThreshold() {
			return circuitBreakerRequestVolumeThreshold;
		}

		public void setCircuitBreakerRequestVolumeThreshold(Integer circuitBreakerRequestVolumeThreshold) {
			this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
		}

		public Integer getCircuitBreakerSleepWindowInMilliseconds() {
			return circuitBreakerSleepWindowInMilliseconds;
		}

		public void setCircuitBreakerSleepWindowInMilliseconds(Integer circuitBreakerSleepWindowInMilliseconds) {
			this.circuitBreakerSleepWindowInMilliseconds = circuitBreakerSleepWindowInMilliseconds;
		}

		public Integer getCircuitBreakerErrorThresholdPercentage() {
			return circuitBreakerErrorThresholdPercentage;
		}

		public void setCircuitBreakerErrorThresholdPercentage(Integer circuitBreakerErrorThresholdPercentage) {
			this.circuitBreakerErrorThresholdPercentage = circuitBreakerErrorThresholdPercentage;
		}

		public Integer getMetricsTimeInMilliseconds() {
			return metricsTimeInMilliseconds;
		}

		public void setMetricsTimeInMilliseconds(Integer metricsTimeInMilliseconds) {
			this.metricsTimeInMilliseconds = metricsTimeInMilliseconds;
		}

		public Integer getMetricsNumBuckets() {
			return metricsNumBuckets;
		}

		public void setMetricsNumBuckets(Integer metricsNumBuckets) {
			this.metricsNumBuckets = metricsNumBuckets;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			HystrixCommandConfiguration that = (HystrixCommandConfiguration) o;
			return Objects.equals(groupKey, that.groupKey) &&
					Objects.equals(commandKey, that.commandKey) &&
					Objects.equals(executionTimeoutEnabled, that.executionTimeoutEnabled) &&
					Objects.equals(executionTimeoutInMilliseconds, that.executionTimeoutInMilliseconds) &&
					Objects.equals(circuitBreakerEnabled, that.circuitBreakerEnabled) &&
					Objects.equals(circuitBreakerRequestVolumeThreshold, that.circuitBreakerRequestVolumeThreshold) &&
					Objects.equals(circuitBreakerSleepWindowInMilliseconds, that.circuitBreakerSleepWindowInMilliseconds) &&
					Objects.equals(circuitBreakerErrorThresholdPercentage, that.circuitBreakerErrorThresholdPercentage) &&
					Objects.equals(metricsTimeInMilliseconds, that.metricsTimeInMilliseconds) &&
					Objects.equals(metricsNumBuckets, that.metricsNumBuckets);
		}

		@Override
		public int hashCode() {
			return Objects.hash(groupKey, commandKey, executionTimeoutEnabled, executionTimeoutInMilliseconds,
					circuitBreakerEnabled, circuitBreakerRequestVolumeThreshold, circuitBreakerSleepWindowInMilliseconds,
					circuitBreakerErrorThresholdPercentage, metricsTimeInMilliseconds, metricsNumBuckets);
		}

	}

}
