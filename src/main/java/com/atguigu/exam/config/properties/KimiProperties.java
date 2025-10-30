package com.atguigu.exam.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kimi.api")
@Data
public class KimiProperties {
    private String model;
    private String url;
    private String apiKey;
    private Double temperature;
    private Integer MaxTokens;
}
