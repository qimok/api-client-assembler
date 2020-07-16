package com.qimok.api.client.config.properties;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import static com.qimok.api.client.Constants.CLIENT_DELIMITER;

/**
 * 自定义配置鉴权
 *
 * @author qimok
 * @since 2020-07-14
 */
@ConfigurationProperties(prefix = "feign.auth")
public class AuthorizationProperties {

	private Map<String, AuthorizationConfiguration> config = new HashMap<>();

	public Map<String, AuthorizationConfiguration> getConfig() {
		return this.config;
	}

	/**
	 * 获取、解析、重新赋值配置
	 */
	public void setConfig(Map<String, AuthorizationConfiguration> config) {
		Map<String, AuthorizationConfiguration> configurationMap = Maps.newHashMap();
		Iterator<Map.Entry<String, AuthorizationConfiguration>> iterator = config.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, AuthorizationConfiguration> entry = iterator.next();
			String keyStr = entry.getKey().trim();
			AuthorizationConfiguration value = entry.getValue();
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

	private void setConfig(Map<String, AuthorizationConfiguration> configurationMap,
						   String key, AuthorizationConfiguration value) {
		if (configurationMap.containsKey(key)) {
			throw new IllegalArgumentException(
					String.format("feign.auth.config.%s... is duplicate!", key));
		} else {
			configurationMap.put(key, value);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AuthorizationProperties that = (AuthorizationProperties) o;
		return Objects.equals(config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(config);
	}

	public static class AuthorizationConfiguration {

		/**
		 * authMod, 鉴权方式
		 * <p>
		 *     token：token 鉴权
		 *     ...
		 */
		private String authMod;

		public String getAuthMod() {
			return authMod;
		}

		public void setAuthMod(String authMod) {
			this.authMod = authMod;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			AuthorizationConfiguration that = (AuthorizationConfiguration) o;
			return Objects.equals(authMod, that.authMod);
		}

		@Override
		public int hashCode() {
			return Objects.hash(authMod);
		}

		@Override
		public String toString() {
			return "AuthorizationConfiguration{" +
					"authMod='" + authMod + '\'' +
					'}';
		}

	}

}
