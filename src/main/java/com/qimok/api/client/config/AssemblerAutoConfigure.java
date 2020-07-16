package com.qimok.api.client.config;

import com.qimok.api.client.assembler.ApiContractBeanRegister;
import com.qimok.api.client.assembler.ConfigurationAssemble;
import com.qimok.api.client.config.builder.FeignBuilderConfig;
import com.qimok.api.client.config.properties.ConfigurationGetter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({FeignBuilderConfig.class, ConfigurationGetter.class})
public class AssemblerAutoConfigure {

    @Bean
    public ApiContractBeanRegister apiContractBeanRegister() {
        return new ApiContractBeanRegister();
    }

    @Bean
    public ConfigurationAssemble configurationAssemble() {
        return new ConfigurationAssemble();
    }

}
