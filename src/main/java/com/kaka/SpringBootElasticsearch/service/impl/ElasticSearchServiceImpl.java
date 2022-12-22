package com.kaka.SpringBootElasticsearch.service.impl;

import com.kaka.SpringBootElasticsearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    //@Async(AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    @Async("definationTaskExecutor")
    public Integer testDefinationTaskExecutor(CountDownLatch countDownLatch) {
        int i = atomicInteger.incrementAndGet();
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "异步调用结束输出结果:" + i);
        countDownLatch.countDown();
        return i;
    }
}
