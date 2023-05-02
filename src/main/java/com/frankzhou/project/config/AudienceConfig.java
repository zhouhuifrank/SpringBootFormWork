package com.frankzhou.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description JwtToken配置类
 * @date 2023-05-02
 */
@Data
@ConfigurationProperties(prefix = "audience")
@Configuration("audience")
public class AudienceConfig {

    private String clientId;

    private String base64Secret;

    private String name;
}
