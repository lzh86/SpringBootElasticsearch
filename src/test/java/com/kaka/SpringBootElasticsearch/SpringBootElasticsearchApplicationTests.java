package com.kaka.SpringBootElasticsearch;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.kaka.SpringBootElasticsearch.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBootElasticsearchApplicationTests {

	@Autowired
	private ElasticSearchService elasticSearchService;


	@Test
	void createIndex() throws Exception {
		String index = elasticSearchService.createIndex();
		System.out.println(index);
	}

	@Test
	void queryIndex() throws Exception {
		String index = elasticSearchService.queryIndex();
		System.out.println(index);

	}

	@Test
	void queryAll() throws Exception {
		String index = elasticSearchService.queryAll();
		System.out.println(index);
	}

	@Test
	void createDocument() throws Exception {
		String index = elasticSearchService.createDocument();
		System.out.println(index);
	}


	@Test
	void queryDocument() throws Exception {
		String index = elasticSearchService.queryDocument();
		System.out.println(index);
	}

	@Test
	void queryByField() throws Exception {
		String index = elasticSearchService.queryByField();
		System.out.println(index);
	}

	@Test
	void updateDocument() throws Exception {
		String index = elasticSearchService.updateDocument();
		System.out.println(index);
	}

	@Test
	void deleteDocumentByCondition() throws Exception {
		String index = elasticSearchService.deleteDocumentByCondition();
		System.out.println(index);
	}


	@Test  //复合查询
	void testComplexMatch() throws Exception {
//		//1、准备请求
//		SearchRequest request = new SearchRequest("hotel");
//		//创建BoolQueryBuilder对象，通过对象指定"与","或"等等，然后再指定查询的方法
//		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		boolQueryBuilder.must(QueryBuilders.termQuery("杭州","北京"));
//		boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(200).lte(500));
//		//准备dsl
//		request.source().query(boolQueryBuilder);
//		//分页查询
//		request.source().from(5).size(10); //表示查询第五页的10条数据
//		//搜索结果排序
//		request.source().sort("price", SortOrder.ASC);
//		//发送请求


	}



}
