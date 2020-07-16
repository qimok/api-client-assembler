package com.qimok.api.client.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.Asserts;

/**
 * 字符串操作工具类
 *
 * @author qimok
 * @since 2020/07/16
 */
public class StringUtil {

    /**
     * 组装符合约定的 ClientName
     * eg: serviceContract to ServiceContractClient
     *
     * @param name serviceName
     * @return serviceName
     */
    public static String toClientName(String name) {
        Asserts.notBlank(name, "name is blank!");
        StringBuilder sb = new StringBuilder(name);
        sb.append("Client");
        return StringUtils.capitalize(sb.toString());
    }

}
