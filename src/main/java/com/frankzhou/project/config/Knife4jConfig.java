package com.frankzhou.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Swagger+Knife4j接口文档配置
 * @date 2023-04-08
 */
@Configuration
@EnableSwagger2
@Profile({"dev","test"})
public class Knife4jConfig {

    /**
     * 接口文档配置
     * 文档地址:http://ip:port/doc.html
     */
    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("project-backend")
                        .description("project-backend")
                        .version("1.0")
                        .build())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.frankzhou.project.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}