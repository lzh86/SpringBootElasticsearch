package com.kaka.SpringBootElasticsearch.service;


import java.util.concurrent.CountDownLatch;

public interface ElasticSearchService {

    Integer testDefinationTaskExecutor(CountDownLatch countDownLatch);
}
