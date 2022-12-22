package com.kaka.SpringBootElasticsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;


/**
 * 自定义 ThreadPoolTaskExecutor
 */
@Slf4j
public class KakaThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(Runnable task) {
        logThreadPoolStatus();
        super.execute(task);
    }

    @Override
    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        logThreadPoolStatus();
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        logThreadPoolStatus();
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        logThreadPoolStatus();
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        logThreadPoolStatus();
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        logThreadPoolStatus();
        return super.submitListenable(task);
    }

    /**
     * 在线程池运行的时候输出线程池的基本信息
     */
    private void logThreadPoolStatus() {
        log.info("核心线程数:{}, 最大线程数:{}, 当前线程数: {}, 活跃的线程数: {},队列额定容量: {},队列实际容量: {}",
                getCorePoolSize(), getMaxPoolSize(), getPoolSize(), getActiveCount(), getQueueCapacity(), getQueueSize());
    }

}
