package com.qiexr.api.client.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Xinshuai
 * @description Hystrix控制隔离线程池的参数
 * @since 2020-07-03 13:29
 */
@ConfigurationProperties("vfeign.request.option")
public class RequestOptionsProperties {

	private Map<String, RequestOptionsProperties.RequestOptionsConfiguration> config = new HashMap<>();

	public Map<String, RequestOptionsProperties.RequestOptionsConfiguration> getConfig() {
		return this.config;
	}

	public void setConfig(Map<String, RequestOptionsProperties.RequestOptionsConfiguration> config) {
		this.config = config;
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
		 * GroupKey, 用于设置 RequestOptions 的分组
		 */
		@NonNull
		private String groupKey;

		/**
		 * 连接超时时间
		 */
		private Integer connectTimeoutMillis = 500;

		/**
		 * 响应超时时间
		 */
		private Integer readTimeoutMillis = 1000;

		public String getGroupKey() {
			return groupKey;
		}

		public void setGroupKey(String groupKey) {
			this.groupKey = groupKey;
		}

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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			RequestOptionsConfiguration that = (RequestOptionsConfiguration) o;
			return Objects.equals(groupKey, that.groupKey) &&
					Objects.equals(connectTimeoutMillis, that.connectTimeoutMillis) &&
					Objects.equals(readTimeoutMillis, that.readTimeoutMillis);
		}

		@Override
		public int hashCode() {
			return Objects.hash(groupKey, connectTimeoutMillis, readTimeoutMillis);
		}
	}

}
