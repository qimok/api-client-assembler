package com.qimok.api.client.config.properties;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import static com.qimok.api.client.Constants.CLIENT_DELIMITER;

/**
 * 自定义配置超时时间
 *
 * @author qimok
 * @since 2020-07-14
 */
@ConfigurationProperties("feign.request.option")
public class RequestOptionsProperties {

	private Map<String, RequestOptionsConfiguration> config = new HashMap<>();

	public Map<String, RequestOptionsConfiguration> getConfig() {
		return this.config;
	}

	/**
	 * 获取、解析、重新赋值配置
	 */
	public void setConfig(Map<String, RequestOptionsConfiguration> config) {
		Map<String, RequestOptionsConfiguration> configurationMap = Maps.newHashMap();
		Iterator<Map.Entry<String, RequestOptionsConfiguration>> iterator = config.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, RequestOptionsConfiguration> entry = iterator.next();
			String keyStr = entry.getKey().trim();
			RequestOptionsConfiguration value = entry.getValue();
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

	private void setConfig(Map<String, RequestOptionsConfiguration> configurationMap,
						   String key, RequestOptionsConfiguration value) {
		if (configurationMap.containsKey(key)) {
			throw new IllegalArgumentException(
					String.format("feign.request.option.config.%s... is duplicate!", key));
		} else {
			configurationMap.put(key, value);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RequestOptionsProperties that = (RequestOptionsProperties) o;
		return Objects.equals(config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(config);
	}

	public static class RequestOptionsConfiguration {

		/**
		 * 连接超时时间
		 */
		private Integer connectTimeoutMillis = 200;

		/**
		 * 响应超时时间
		 */
		private Integer readTimeoutMillis = 3000;

		/**
		 * 重定向
		 */
		private Boolean followRedirects = false;

		public Integer getConnectTimeoutMillis() {
			return connectTimeoutMillis;
		}

		public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
			this.connectTimeoutMillis = connectTimeoutMillis;
		}

		public Integer getReadTimeoutMillis() {
			return readTimeoutMillis;
		}

		public void setReadTimeoutMillis(Integer readTimeoutMillis) {
			this.readTimeoutMillis = readTimeoutMillis;
		}

		public Boolean getFollowRedirects() {
			return followRedirects;
		}

		public void setFollowRedirects(Boolean followRedirects) {
			this.followRedirects = followRedirects;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			RequestOptionsConfiguration that = (RequestOptionsConfiguration) o;
			return Objects.equals(connectTimeoutMillis, that.connectTimeoutMillis) &&
					Objects.equals(readTimeoutMillis, that.readTimeoutMillis) &&
					Objects.equals(followRedirects, that.followRedirects);
		}

		@Override
		public int hashCode() {
			return Objects.hash(connectTimeoutMillis, readTimeoutMillis, followRedirects);
		}

		@Override
		public String toString() {
			return "RequestOptionsConfiguration{" +
					"connectTimeoutMillis=" + connectTimeoutMillis +
					", readTimeoutMillis=" + readTimeoutMillis +
					", followRedirects=" + followRedirects +
					'}';
		}

	}

}
