package com.frankzhou.project.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.simpleframework.xml.core.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Elasticsearch配置类
 * @date 2023-08-20
 */
//@Configuration
//@EnableReactiveElasticsearchRepositories(basePackages = "")
//public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
//
//    @Value("${elasticsearch.uris}")
//    private String uris;
//
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        int startIndex = uris.lastIndexOf("/");
//        String[] uriSplit = uris.substring(startIndex + 1).split(":");
//        String host = uriSplit[0];
//        Integer port = Integer.valueOf(uriSplit[1]);
//        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
//        return new RestHighLevelClient(builder);
//    }
//}
