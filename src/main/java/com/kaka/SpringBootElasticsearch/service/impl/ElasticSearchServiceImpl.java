package com.kaka.SpringBootElasticsearch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kaka.SpringBootElasticsearch.service.ElasticSearchService;
import com.kaka.SpringBootElasticsearch.vo.Gift;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    RestClient restClient;


    @Override
    public String createIndex() throws Exception {
        String method = "PUT";
        String endpoint = "/gift";
        /**
         *  可以通过这种方式指定分词类型
         *                      "giftName": {
         * 						"type": "text",
         * 						"store": true,
         * 						"analyzer": "ik_max_word",
         * 						"search_analyzer": "ik_smart"
         *                                   }
         */
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"mappings\": {\n" +
                "    \"good\": {\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"type\": \"keyword\"," +
                "          \"store\": true" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}", ContentType.APPLICATION_JSON);

        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        request.setEntity(entity);
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    @Override
    public String queryIndex() throws Exception {
        String method = "GET";
        String endpoint = "/gift";
        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    /**
     *   term:精确查询
     *   match:查询所有
     *   match_query:全文检索
     */
    @Override
    public String queryAll() throws Exception {
        String method = "POST";
        String endpoint = "/gift/_search";
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  }\n" +
                "}", ContentType.APPLICATION_JSON);

        /**
         *  复杂查询 URL http://127.0.0.1:9200/gift/_search
         *  term:精确查询
         *  match:查询所有
         *  match_query:全文检索
         *  需要在创建索引时指定 name 类型为 keyword 才能使 term 查询生效
         *  {"query":{"bool":{"must":[{"match":{"describe":"牛肉干"}},{"term":{"name":"测试数据"}}],"must_not":[],"should":[]}},"from":0,"size":10,"sort":[],"aggs":{}}
         */

        // "正式礼包"
        HttpEntity entity1 = new NStringEntity("{\n" +
                "  \"query\": {\n" +
                "    \"match\": {" +
                "\"describe\": \"测试礼包数据\"" +
                "},\n" +
                "    \"term\": {" +
                "\"name\":{\"value\":\"正式礼包\" }" +
                "}\n" +
                "  },\n" +
                "\"sort\":[\n" +
                "       { \"price\":\"ASC\"}\n" +
                "        ]," +
                "\"from\": 0,\n" +
                "  \"size\": 1"+
                "}", ContentType.APPLICATION_JSON);

        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        request.setEntity(entity1);

        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));

        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 新增文档
     *
     * @return
     * @throws Exception
     */
    @Override
    public String createDocument() throws Exception {
        String method = "PUT";
        String endpoint = "/gift/good/3"; // 索引：礼包【DB】  类型：食品【table】 文档：【表里的数据】
        Gift build = Gift.builder()
                .name("正式数据")
                .describe("牛肉干")
                .price(101)
                .date(new Date())
                .build();
        String jsonStr = JSONObject.toJSONString(build);
        // JSON格式字符串
        HttpEntity entity = new NStringEntity(jsonStr, ContentType.APPLICATION_JSON);
        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        request.setEntity(entity);

        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 查询文档
     *
     * @return
     * @throws Exception
     */
    @Override
    public String queryDocument() throws Exception {
        String method = "GET";
        String endpoint = "/gift/good/1";
        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 根据Id获取
     *
     * @return
     * @throws Exception
     */
    @Override
    public String queryByField() throws Exception {
        String method = "POST";
        String endpoint = "/gift/good/_search";

        HttpEntity entity = new NStringEntity("{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"name\": \"测试\"\n" +
                "    }\n" +
                "  }\n" +
                "}", ContentType.APPLICATION_JSON);
        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        request.setEntity(entity);
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 更新数据
     *
     * @return
     * @throws Exception
     */
    @Override
    public String updateDocument() throws Exception {
        // doc_as_upsert ：使用doc_as_upsert可以在文档不存在的时候，把doc中的内容插入到文档中
        String method = "POST";
        String endpoint = "/gift/good/1/_update";
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"doc\": {\n" +
                "    \"name\":\"测试礼包名称修改\"\n" +
                "	}\n" +
                "}", ContentType.APPLICATION_JSON);

        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        request.setEntity(entity);

        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 删除数据
     *
     * @return
     * @throws Exception
     */
    @Override
    public String deleteDocument() throws Exception {
        String method = "DELETE";
        String endpoint = "/gift/good/1";
        HttpEntity entity = new NStringEntity("", ContentType.APPLICATION_JSON);
        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        request.setEntity(entity);
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 按条件删除
     *
     * @return
     * @throws Exception
     */
    @Override
    public String deleteDocumentByCondition() throws Exception {
        String method = "DELETE";
        String endpoint = "/gift";
        //String endpoint = "/gift/good/_delete_by_query";
//        HttpEntity entity = new NStringEntity("{\n" +
//                "  \"query\": {\n" +
//                "    \"term\": {\n" +
//                "\"name\":{\"value\":\"正式礼包\" }" +
//                "    }\n" +
//                "  }\n" +
//                "}", ContentType.APPLICATION_JSON);
        Request request = new Request(
                method,  // HTTP 方法( GET 、POST 、HEAD 等)
                endpoint);
        //request.setEntity(entity);
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
        return EntityUtils.toString(response.getEntity());
    }


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
