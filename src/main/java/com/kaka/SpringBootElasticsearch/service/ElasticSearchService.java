package com.kaka.SpringBootElasticsearch.service;


import java.util.concurrent.CountDownLatch;

public interface ElasticSearchService {
    /**
     * 创建索引
     *
     * @return
     */
    String createIndex() throws Exception;

    /**
     * 查询索引结构
     *
     * @return
     * @throws Exception
     */
    String queryIndex() throws Exception;

    /**
     * 查询索引数据
     *
     * @return
     * @throws Exception
     */
    String queryAll() throws Exception;


    /**
     * 新增文档
     * @return
     * @throws Exception
     */
    String createDocument()throws Exception;

    /**
     * 查询文档
     * @return
     * @throws Exception
     */
    String queryDocument()throws Exception;

    /**
     * 根据Id获取
     * @return
     * @throws Exception
     */
    String queryByField()throws Exception;

    /**
     * 更新数据
     * @return
     * @throws Exception
     */
    String updateDocument()throws Exception;

    /**
     * 删除数据
     * @return
     * @throws Exception
     */
    String deleteDocument()throws Exception;

    /**
     * 按条件删除
     * @return
     * @throws Exception
     */
    String deleteDocumentByCondition()throws Exception;

    Integer testDefinationTaskExecutor(CountDownLatch countDownLatch);
}
