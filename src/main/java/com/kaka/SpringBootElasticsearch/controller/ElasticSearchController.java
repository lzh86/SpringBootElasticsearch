package com.kaka.SpringBootElasticsearch.controller;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SuggestMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.elasticsearch.sql.QueryResponse;
import co.elastic.clients.json.JsonData;
import com.kaka.SpringBootElasticsearch.vo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/index")
@Slf4j
public class ElasticSearchController {

    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Resource
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    private final static String INDEX_NAME = "student-index";

    /**
     * 创建索引
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @ResponseBody
    public void indexCreate() throws IOException {
        CreateIndexResponse response = elasticsearchClient.indices().create(builder -> builder
                .settings(indexSettingsBuilder -> indexSettingsBuilder.numberOfReplicas("1").numberOfShards("2"))
                .mappings(typeMappingBuilder -> typeMappingBuilder
                        .properties("age", propertyBuilder -> propertyBuilder.integer(integerNumberPropertyBuilder -> integerNumberPropertyBuilder))
                        .properties("version", propertyBuilder -> propertyBuilder.integer(integerNumberPropertyBuilder -> integerNumberPropertyBuilder))
                        .properties("name", propertyBuilder -> propertyBuilder.keyword(keywordPropertyBuilder -> keywordPropertyBuilder))
                        .properties("evaluate", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("address", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                )
                .index(INDEX_NAME));
        log.info("create->response:{}", response.acknowledged());
    }

    /**
     * 修改索引的_mapping信息
     * 字段可以新增，已有的字段只能修改字段的search_analyzer属性
     */
    @RequestMapping(value = "/modifyIndex", method = RequestMethod.GET)
    @ResponseBody
    public void modifyIndex() throws IOException {
        PutMappingResponse response = elasticsearchClient.indices().putMapping(typeMappingBuilder -> typeMappingBuilder
                .index(INDEX_NAME)
                .properties("age", propertyBuilder -> propertyBuilder.integer(integerNumberPropertyBuilder -> integerNumberPropertyBuilder))
                .properties("studentNo", propertyBuilder -> propertyBuilder.keyword(keywordPropertyBuilder -> keywordPropertyBuilder))
                .properties("evaluate", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
        );
        log.info("modifyIndex->response={}", response.acknowledged());
    }

    /**
     * 查询索引详情
     */
    @RequestMapping(value = "/getIndex", method = RequestMethod.GET)
    @ResponseBody
    public void getIndex() throws IOException {
        //使用 * 也可以
        GetIndexResponse response = elasticsearchClient.indices().get(builder -> builder.index("_all"));
        log.info("getIndex->response={}", response.result().toString());
    }


    /**
     * 查询索引详情
     */
    @RequestMapping(value = "/getIndexDetaile", method = RequestMethod.GET)
    @ResponseBody
    public void getIndexDetail() throws IOException {
        GetIndexResponse response = elasticsearchClient.indices().get(builder -> builder.index(INDEX_NAME));
        log.info("getIndexDetail->response:{}", response.result().get(INDEX_NAME));
    }

    /**
     * 删除索引
     */
    @RequestMapping(value = "/deleteIndex", method = RequestMethod.GET)
    @ResponseBody
    public void deleteIndex() throws IOException {
        DeleteIndexResponse response = elasticsearchClient.indices().delete(builder -> builder.index(INDEX_NAME));
        log.info("deleteIndex->response:{}", response.acknowledged());
    }


    /**
     * 创建文档
     */
    @RequestMapping(value = "/createDoc", method = RequestMethod.GET)
    @ResponseBody
    public void createDoc() throws IOException {
        Map<String, Object> doc = new HashMap<>();
        doc.put("studentNo", 2050001);
        doc.put("name", "霸王龙");
        doc.put("age", 20);
        doc.put("address", "侏罗纪公园3号左侧");
        doc.put("version", 1);
        doc.put("evaluate", "史前巨兽,霸王龙属于暴龙超科的暴龙属,为该属下的唯一一种");
        CreateResponse response = elasticsearchClient.create(builder -> builder.index(INDEX_NAME).id("1").document(doc));
        log.info("createDoc->response:{}", response.toString());


        Student build = Student.builder().studentNo("2050002").name("三角龙").age(22).address("美利坚合众国双子大厦23层").version(2)
                .evaluate("鸟臀目角龙下目角龙科的草食性恐龙的一属，化石发现于北美洲的晚白垩纪晚马斯特里赫特阶地层，约6800万年前到6500万年前").build();
        response = elasticsearchClient.create(builder -> builder.index(INDEX_NAME).id("2").document(build));
        log.info("createDoc->response:{}", response.toString());
    }

    /**
     * 删除文档
     */
    @RequestMapping(value = "/deleteDoc", method = RequestMethod.GET)
    @ResponseBody
    public void deleteDoc() throws IOException {
        DeleteResponse response = elasticsearchClient.delete(builder -> builder.index(INDEX_NAME).id("1"));
        log.info("deleteDoc->response:{}", response.toString());
    }

    /**
     * 修改文档
     */
    @RequestMapping(value = "/updateDoc", method = RequestMethod.GET)
    @ResponseBody
    public void updateDoc() throws IOException {
        Map<String, Object> doc = new HashMap<>();
        doc.put("age", 11);
        doc.put("name", "霸王龙1");

        //只更新设置的字段
        UpdateResponse response = elasticsearchClient.update(builder -> builder.index(INDEX_NAME).id("1").doc(doc), Map.class);
        log.info("updateDoc->response:{}", response.toString());

        Student student = new Student();
        student.setAge(22);
        student.setName("三角龙1");
        response = elasticsearchClient.update(builder -> builder.index(INDEX_NAME).id("2").doc(student).docAsUpsert(true), Student.class);
        log.info("updateDoc->response:{}", response.toString());
    }


    /**
     * 新增或修改文档，修改时所有的字段都会覆盖(相当于先删除在新增)
     */
    @RequestMapping(value = "/createOrUpdateDoc", method = RequestMethod.GET)
    @ResponseBody
    public void createOrUpdateDoc() throws IOException {
        Map<String, Object> doc = new HashMap<>();
        doc.put("age", 11);
        doc.put("name", "霸王龙1");

        //只更新设置的字段
        IndexResponse response = elasticsearchClient.index(builder -> builder.index(INDEX_NAME).id("1").document(doc));
        log.info("createOrUpdateDoc->response:{}", response.toString());

        Student student = new Student();
        student.setAge(22);
        student.setName("三角龙1");
        response = elasticsearchClient.index(builder -> builder.index(INDEX_NAME).id("2").document(student));
        log.info("createOrUpdateDoc->response:{}", response.toString());
    }

    /**
     * 批量操作
     */
    @RequestMapping(value = "/bulk", method = RequestMethod.GET)
    @ResponseBody
    public void bulk() throws IOException {
        List<BulkOperation> list = new ArrayList<>();

        //批量新增
//        for (int i = 0; i < 5; i++) {
//            Map<String, Object> doc = new HashMap<>();
//            doc.put("age", 30);
//            doc.put("name", "李四" + i);
//            String id = 10 + i + "";
//            list.add(new BulkOperation.Builder().create(builder -> builder.index(INDEX_NAME).id(id).document(doc)).build());
//        }
//        for (int i = 0; i < 5; i++) {
//            Student student = Student.builder().name("张三").build();
//            String id = 20 + i + "";
//            list.add(new BulkOperation.Builder().create(builder -> builder.index(INDEX_NAME).id(id).document(student)).build());
//        }

        //批量删除
        list.add(new BulkOperation.Builder().delete(builder -> builder.index(INDEX_NAME).id("12")).build());
        list.add(new BulkOperation.Builder().delete(builder -> builder.index(INDEX_NAME).id("13")).build());

        BulkResponse response = elasticsearchClient.bulk(builder -> builder.index(INDEX_NAME).operations(list));
        log.info("bulk->response:{}", response.toString());
    }

    /**
     * 查询所有文档
     */
    @RequestMapping(value = "/getAllDoc", method = RequestMethod.GET)
    @ResponseBody
    public void getAllDoc() throws IOException {
        SearchResponse<Map> response = elasticsearchClient.search(builder -> builder.index(INDEX_NAME), Map.class);
        log.info("getAllDoc->response:{}", response.toString());
    }

    /**
     * 查询单个文档
     */
    @RequestMapping(value = "/getDoc", method = RequestMethod.GET)
    @ResponseBody
    public void getDoc() throws IOException {
        GetResponse<Map> response = elasticsearchClient.get(builder -> builder.index(INDEX_NAME).id("1"), Map.class);
        if (response.found()) {
            log.info("getDoc->response:{}", response.source().toString());
        }

        GetResponse<Student> response2 = elasticsearchClient.get(builder -> builder.index(INDEX_NAME).id("2"), Student.class);
        if (response2.found()) {
            log.info("getDoc->response2:{}", response.source().toString());
        }
    }

    /**
     * term/terms查询,对输入内容不做分词处理
     *
     * @return
     */
    @RequestMapping(value = "/searchTerm", method = RequestMethod.GET)
    @ResponseBody
    public String searchTerm() throws IOException {
        SearchResponse<Map> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .term(termQueryBuilder -> termQueryBuilder
                                        .field("name").value("张三")))
                        .sort(sortOptionsBuilder -> sortOptionsBuilder
                                .field(fieldSortBuilder -> fieldSortBuilder
                                        .field("name").order(SortOrder.Asc)))
                        .source(sourceConfigBuilder -> sourceConfigBuilder
                                .filter(sourceFilterBuilder -> sourceFilterBuilder
                                        .includes("age", "name")))
                        .from(0)
                        .size(10)
                , Map.class);
        log.info("searchTerm->response,{}", response.toString());

        List<FieldValue> words = new ArrayList<>();
        words.add(new FieldValue.Builder().stringValue("三角龙1").build());
        words.add(new FieldValue.Builder().stringValue("霸王龙1").build());
        SearchResponse<Student> response2 = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .terms(termsQueryBuilder -> termsQueryBuilder
                                        .field("name").terms(termsQueryFieldBuilder -> termsQueryFieldBuilder.value(words))))
                        .source(sourceConfigBuilder -> sourceConfigBuilder
                                .filter(sourceFilterBuilder -> sourceFilterBuilder
                                        .excludes("evaluate")))
                        .from(0)
                        .size(10)
                , Student.class);
        log.info("searchTerm->response,{}", response2.toString());

        return response.toString() + response2.toString();
    }


    /**
     * range查询,范围查询
     */
    @RequestMapping(value = "/searchRange", method = RequestMethod.GET)
    @ResponseBody
    public String searchRange() throws IOException {
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .range(rangeQueryBuilder -> rangeQueryBuilder
                                        .field("age").gte(JsonData.of("10")).lt(JsonData.of("22"))))
                , Student.class);
        log.info("searchRange->response{}", response.toString());
        return response.toString();
    }


    /**
     * match查询，对输入内容先分词再查询
     */
    @RequestMapping(value = "/searchMatch", method = RequestMethod.GET)
    @ResponseBody
    public String searchMatch() throws IOException {
        SearchResponse<Map> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("evaluate").query("史前巨兽")))
                , Map.class);
        log.info("searchRange->response{}", response.toString());
        return response.toString();
    }

    /**
     * multi_match查询
     */
    @RequestMapping(value = "/searchMultiMatch", method = RequestMethod.GET)
    @ResponseBody
    public String searchMultiMatch() throws IOException {
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .multiMatch(multiMatchQueryBuilder -> multiMatchQueryBuilder
                                        .fields("evaluate", "address").query("史前")))
                , Student.class);
        log.info("searchMultiMatch->response{}", response.toString());
        return response.toString();
    }

    /**
     * match_phrase 查询,匹配整个查询字符串
     */
    @RequestMapping(value = "/searchMatchPhrase", method = RequestMethod.GET)
    @ResponseBody
    public String searchMatchPhrase() throws IOException {
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .matchPhrase(matchPhraseQueryBuilder -> matchPhraseQueryBuilder.field("evaluate").query("史前巨兽")))
                , Student.class);
        log.info("matchPhrase->response{}", response.toString());
        return response.toString();
    }


    /**
     * match_all 查询,查询所有
     */
    @RequestMapping(value = "/searchMatchAll", method = RequestMethod.GET)
    @ResponseBody
    public String searchMatchAll() throws IOException {
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .matchPhrase(matchPhraseQueryBuilder -> matchPhraseQueryBuilder.field("evaluate").query("史前")))
                , Student.class);
        log.info("searchMatchAll->response{}", response.toString());
        return response.toString();
    }


    /**
     * query_string 查询
     */
    @RequestMapping(value = "/searchQueryString", method = RequestMethod.GET)
    @ResponseBody
    public void searchQueryString() throws IOException {
        //类似 match
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .defaultField("evaluate").query("史前")))
                , Student.class);
        log.info(response.toString());

        //类似 mulit_match
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .fields("evaluate", "address").query("史前")))
                , Student.class);
        log.info(response.toString());

        //类似 match_phrase
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .defaultField("evaluate").query("\"史前\"")))
                , Student.class);
        log.info(response.toString());

        //带运算符查询，运算符两边的词不再分词
        //查询同时包含 ”史前“ 和 ”巨兽“ 的文档
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .fields("evaluate").query("史前 AND 巨兽")))
                , Student.class);
        log.info(response.toString());

        //等同上一个查询
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .fields("evaluate").query("史前 巨兽").defaultOperator(Operator.And)))
                , Student.class);
        log.info(response.toString());


        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .fields("evaluate", "name").query("(史前 AND 巨兽) OR 李四4")))
                , Student.class);
        log.info(response.toString());
    }

    /**
     * simple_query_string 查询,和query_string类似
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/searchSimpleQueryString", method = RequestMethod.GET)
    @ResponseBody
    public String searchSimpleQueryString() throws IOException {
        /**
         * 不支持AND OR NOT，会当做字符串处理
         * 使用 +替代AND,|替代OR,-替代NOT
         */
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .simpleQueryString(simpleQueryStringQueryBuilder -> simpleQueryStringQueryBuilder
                                        .fields("evaluate").query("史前 + 巨兽")))
                , Student.class);
        log.info("searchSimpleQueryString->response:{}", response.toString());
        return response.toString();
    }

    /**
     * 模糊查询
     */
    @RequestMapping(value = "/searchFuzzy", method = RequestMethod.GET)
    @ResponseBody
    public void searchFuzzy() throws IOException {
        //全文查询时使用模糊参数，先分词再计算模糊选项。
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("evaluate").query("史前巨兽").fuzziness("1")))
                , Student.class);
        log.info("searchFuzzy->response:{}", response.toString());

        //使用 fuzzy query，对输入不分词，直接计算模糊选项。
        SearchResponse<Student> response2 = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .fuzzy(fuzzyQueryBuilder -> fuzzyQueryBuilder
                                        .field("evaluate").fuzziness("1").value("史前巨兽")))
                , Student.class);
        log.info("searchFuzzy->response2:{}", response2.toString());
    }

    /**
     * bool查询,组合查询
     */
    @RequestMapping(value = "/searchBool", method = RequestMethod.GET)
    @ResponseBody
    public void searchBool() throws IOException {
        //查询 evaluate 包含 “史前巨兽” 且 age 在 [20-40] 之间的文档
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .bool(boolQueryBuilder -> boolQueryBuilder
                                        .must(queryBuilder2 -> queryBuilder2
                                                .match(matchQueryBuilder -> matchQueryBuilder
                                                        .field("evaluate").query("史前巨兽"))
                                        )
                                        .must(queryBuilder2 -> queryBuilder2
                                                .range(rangeQueryBuilder -> rangeQueryBuilder
                                                        .field("age").gte(JsonData.of("20")).lt(JsonData.of("40")))
                                        )
                                )
                        )
                , Student.class);
        log.info("searchFuzzy->response:{}", response.toString());

        //过滤出 evaluate 包含 “史前巨兽” 且 age 在 [20-40] 之间的文档，不计算得分
        SearchResponse<Student> response2 = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .bool(boolQueryBuilder -> boolQueryBuilder
                                        .filter(queryBuilder2 -> queryBuilder2
                                                .match(matchQueryBuilder -> matchQueryBuilder
                                                        .field("evaluate").query("史前巨兽"))
                                        )
                                        .filter(queryBuilder2 -> queryBuilder2
                                                .range(rangeQueryBuilder -> rangeQueryBuilder
                                                        .field("age").gte(JsonData.of("20")).lt(JsonData.of("40")))
                                        )
                                )
                        )
                , Student.class);
        log.info("searchFuzzy->response2:{}", response2.toString());
    }

    /**
     * aggs查询,聚合查询
     */
    @RequestMapping(value = "/searchAggs", method = RequestMethod.GET)
    @ResponseBody
    public void searchAggs() throws IOException {
        //求和
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .aggregations("age_sum", aggregationBuilder -> aggregationBuilder
                                .sum(sumAggregationBuilder -> sumAggregationBuilder
                                        .field("age")))
                , Student.class);
        log.info("searchAggs->response:{}", response.toString());

        //类似 select count distinct(age) from Student-index
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .aggregations("age_count", aggregationBuilder -> aggregationBuilder
                                .cardinality(cardinalityAggregationBuilder -> cardinalityAggregationBuilder.field("age")))
                , Student.class);
        log.info("searchAggs->response:{}", response.toString());

        //数量、最大、最小、平均、求和
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .aggregations("age_stats", aggregationBuilder -> aggregationBuilder
                                .stats(statsAggregationBuilder -> statsAggregationBuilder
                                        .field("age")))
                , Student.class);
        log.info("searchAggs->response:{}", response.toString());

        //select name,count(*) from Student-index group by name
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .aggregations("name_terms", aggregationBuilder -> aggregationBuilder
                                .terms(termsAggregationBuilder -> termsAggregationBuilder
                                        .field("name")))
                , Student.class);
        log.info("searchAggs->response:{}", response.toString());

        //select name,age,count(*) from Student-index group by name,age
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .aggregations("name_terms", aggregationBuilder -> aggregationBuilder
                                .terms(termsAggregationBuilder -> termsAggregationBuilder
                                        .field("name")
                                )
                                .aggregations("age_terms", aggregationBuilder2 -> aggregationBuilder2
                                        .terms(termsAggregationBuilder -> termsAggregationBuilder
                                                .field("age")
                                        ))
                        )
                , Student.class);
        log.info("searchAggs->response:{}", response.toString());

        //类似 select avg(age) from Student-index where name='霸王龙'
        response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .bool(boolQueryBuilder -> boolQueryBuilder
                                        .filter(queryBuilder2 -> queryBuilder2
                                                .term(termQueryBuilder -> termQueryBuilder
                                                        .field("name").value("霸王龙")))))
                        .aggregations("ave_age", aggregationBuilder -> aggregationBuilder
                                .avg(averageAggregationBuilder -> averageAggregationBuilder.field("age")))
                , Student.class);
        log.info("searchAggs->response:{}", response.toString());
    }

    /**
     * suggest查询,推荐搜索
     */
    @RequestMapping(value = "/searchSuggest", method = RequestMethod.GET)
    @ResponseBody
    public void searchSuggest() throws IOException {
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .suggest(suggesterBuilder -> suggesterBuilder
                                .suggesters("evaluate_suggest", fieldSuggesterBuilder -> fieldSuggesterBuilder
                                        .text("史前巨兽")
                                        .term(termSuggesterBuilder -> termSuggesterBuilder
                                                .field("evaluate")
                                                .suggestMode(SuggestMode.Always)
                                                .minWordLength(2)
                                        )
                                )
                        )
                , Student.class);
        log.info("searchSuggest->response:{}", response.toString());
    }

    /**
     * 高亮显示
     */
    @RequestMapping(value = "/searchHighlight", method = RequestMethod.GET)
    @ResponseBody
    public String searchHighlight() throws IOException {
        SearchResponse<Student> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("evaluate").query("史前巨兽")))
                        .highlight(highlightBuilder -> highlightBuilder
                                .preTags("<span color='red'>")
                                .postTags("</span>")
                                .fields("evaluate", highlightFieldBuilder -> highlightFieldBuilder))
                , Student.class);
        log.info("searchHighlight->response:{}", response.toString());
        return response.toString();
    }

    /**
     * sql查询
     */
    @RequestMapping(value = "/searchSql", method = RequestMethod.GET)
    @ResponseBody
    public void searchSql() throws IOException, ExecutionException, InterruptedException {
        QueryResponse response = elasticsearchClient.sql().query(builder -> builder
                .format("json").query("SELECT * FROM \"" + INDEX_NAME + "\" limit 1"));

        CompletableFuture<QueryResponse> asynResponse = elasticsearchAsyncClient.sql().query(builder -> builder
                .format("json").query("SELECT * FROM \"" + INDEX_NAME + "\" limit 1"));

        QueryResponse queryResponse = asynResponse.get();

        log.info("searchSql->response:{}", response.toString());
        log.info("searchSql->queryResponse:{}", queryResponse.toString());
    }

}
