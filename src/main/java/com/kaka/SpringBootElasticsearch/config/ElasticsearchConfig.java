package com.kaka.SpringBootElasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean(name = "elasticsearchClient")
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(new ObjectMapper()));
        return new ElasticsearchClient(transport);
    }

    @Bean(name = "elasticsearchAsyncClient")
    public ElasticsearchAsyncClient elasticsearchAsyncClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(new ObjectMapper()));
        return new ElasticsearchAsyncClient(transport);
    }
}
