package com.qimok.api.client;

/**
 * @author qimok
 * @since  2020-07-16
 */
public interface Constants {

    /**
     * 需要扫描的包前缀
     */
    String XXX_PACKAGE_PREFIX = "com.xxx";
    String YYY_PACKAGE_PREFIX = "com.yyy";

    /**
     * Feign日志用的字段
     */
    String LOG_TYPE_KEY = "Type of log";
    String LOG_TYPE_REQUEST = "Request";
    String LOG_TYPE_DEFAULT = "Default";
    String LOG_TYPE_RESPONSE = "Response";
    String CLIENT_NAME = "Client";
    String HEADS = "Head";
    String METHOD = "Method";
    String URL = "Url";
    String BODY = "Body";
    String STATUS = "Status";
    String ELAPSED_TIME= "Elapsed";
    String ARGS = "Args";
    String FORMAT = "Format";

    /**
     * 多个 Client 共用一套配置时的分隔符
     */
    String CLIENT_DELIMITER = "-";

}
