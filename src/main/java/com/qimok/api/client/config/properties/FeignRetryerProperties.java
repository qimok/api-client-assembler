package com.qimok.api.client.config.properties;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import static com.qimok.api.client.Constants.CLIENT_DELIMITER;

/**
 * 自定义配置重试
 *
 * @author qimok
 * @since 2020-07-14
 */
@ConfigurationProperties(prefix = "feign.retryer")
public class FeignRetryerProperties {

	private Map<String, FeignRetryerConfiguration> config = new HashMap<>();

	public Map<String, FeignRetryerConfiguration> getConfig() {
		return this.config;
	}

	/**
	 * 获取、解析、重新赋值配置
	 */
	public void setConfig(Map<String, FeignRetryerConfiguration> config) {
		Map<String, FeignRetryerConfiguration> configurationMap = Maps.newHashMap();
		Iterator<Map.Entry<String, FeignRetryerConfiguration>> iterator = config.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, FeignRetryerConfiguration> entry = iterator.next();
			String keyStr = entry.getKey().trim();
			FeignRetryerConfiguration value = entry.getValue();
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

	private void setConfig(Map<String, FeignRetryerConfiguration> configurationMap,
						   String key, FeignRetryerConfiguration value) {
		if (configurationMap.containsKey(key)) {
			throw new IllegalArgumentException(
					String.format("feign.retryer.config.%s... is duplicate!", key));
		} else {
			configurationMap.put(key, value);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FeignRetryerProperties that = (FeignRetryerProperties) o;
		return Objects.equals(config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(config);
	}

	public static class FeignRetryerConfiguration {

		/**
		 * 时间间隔
		 */
		private Long period;

		/**
		 * 最大时间间隔
		 */
		private Long maxPeriod;

		/**
		 * 最大尝试次数
		 */
		private int maxAttempts;

		public Long getPeriod() {
			return period;
		}

		public void setPeriod(Long period) {
			this.period = period;
		}

		public Long getMaxPeriod() {
			return maxPeriod;
		}

		public void setMaxPeriod(Long maxPeriod) {
			this.maxPeriod = maxPeriod;
		}

		public int getMaxAttempts() {
			return maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			FeignRetryerConfiguration that = (FeignRetryerConfiguration) o;
			return maxAttempts == that.maxAttempts &&
					Objects.equals(period, that.period) &&
					Objects.equals(maxPeriod, that.maxPeriod);
		}

		@Override
		public int hashCode() {
			return Objects.hash(period, maxPeriod, maxAttempts);
		}

	}

}
