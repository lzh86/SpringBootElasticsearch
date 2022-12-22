package com.kaka.SpringBootElasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchClientConfig {
    @Bean
    public RestClient restRestClient() {
        //127.0.0.1    10.52.11.106
        RestClient restClient = RestClient.builder(new HttpHost("127.0.0.1", 9200)).build();
        return restClient;
    }
}
