package com.qiexr.api.client.assembler.runtest;

import com.qiexr.api.client.assembler.ApiClientConfigAssemble;
import com.qiexr.api.client.assembler.ApiContractBeanRegister;
import com.qiexr.api.client.config.FeignJsonConfiguration;
import com.qiexr.api.common.config.FeignApiClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author Xinshuai
 * @description
 * @since 2020-07-03 13:49
 */

@SpringBootApplication
@Import({ApiContractBeanRegister.class, ApiClientConfigAssemble.class,
        FeignJsonConfiguration.class, FeignApiClientConfig.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
