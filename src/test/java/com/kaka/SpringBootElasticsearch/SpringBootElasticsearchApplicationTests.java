package com.kaka.SpringBootElasticsearch;

import com.kaka.SpringBootElasticsearch.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
class SpringBootElasticsearchApplicationTests {

	@Autowired
	private ElasticSearchService elasticSearchService;

	@Test
	void contextLoads() {
		CountDownLatch countDownLatch = new CountDownLatch(30);
		for(int i = 0; i < 30 ; i++){
			Integer integer = elasticSearchService.testDefinationTaskExecutor(countDownLatch);
		}

		try {
			countDownLatch.await();
			System.out.println("多线程执行完成,主线成输出");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void forkJoinTest(){
		List<Integer> numberList = Arrays.asList(1,2,3,4,5,6,7,8,9);
		numberList.parallelStream().forEach(System.out::println);
	}

	@Test
	void parallelTest() {
		List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		integers.stream().reduce((a,b) -> {
			System.out.println(String.format("%s: %d+%d=%d",Thread.currentThread(),a,b,a+b));
			return a+b;
		});
	}

}
