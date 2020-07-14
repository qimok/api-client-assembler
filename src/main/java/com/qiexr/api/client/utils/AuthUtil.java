package com.qiexr.api.client.utils;

import com.xingren.clinic.ClinicApiAuth;
import com.xingren.v.auth.Signature;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static com.xingren.v.auth.utils.AuthUtils.auth;

/**
 * @author Xinshuai
 * @description API Token 生成工具类
 * @since 2020-03-26 16:34
 */
public class AuthUtil {

    /**
     * 生成API Token.
     *
     * @return
     */
    public static String generateApiToken(String secret) {
        String now = DateTimeFormatter.ofPattern("yyMMddHH").format(OffsetDateTime.now());
        return generateApiToken(secret, now);
    }

    public static String generateApiToken(String secret, String timestamp) {
        ClinicApiAuth apiAuth = new ClinicApiAuth(timestamp);
        return auth(secret, apiAuth, Signature.HMAC_SHA1);
    }

}
