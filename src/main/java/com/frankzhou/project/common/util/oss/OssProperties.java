package com.frankzhou.project.common.util.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Oss配置类
 * @date 2023-07-21
 */
@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    Boolean enabled;

    OssType type;

    String endpoint;

    String accessKey;

    String secretKey;

    String defaultBucketName;
}
