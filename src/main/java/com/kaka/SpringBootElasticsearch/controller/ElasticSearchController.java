package com.kaka.SpringBootElasticsearch.controller;

import com.kaka.SpringBootElasticsearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/index")
@Slf4j
public class ElasticSearchController {


    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping(value = "/createIndex" )
    @ResponseBody
    public void createIndex() throws Exception {
        String index = elasticSearchService.createIndex();
        System.out.println(index);
    }

    @RequestMapping(value = "/queryIndex" )
    @ResponseBody
    public void queryIndex() throws Exception {
        String index = elasticSearchService.queryIndex();
        System.out.println(index);
    }

    @RequestMapping(value = "/queryAll" )
    @ResponseBody
    public void queryAll() throws Exception {
        String index = elasticSearchService.queryAll();
        System.out.println(index);
    }

    @RequestMapping(value = "/createDocument" )
    @ResponseBody
    public void createDocument() throws Exception {
        String index = elasticSearchService.createDocument();
        System.out.println(index);
    }

    @RequestMapping(value = "/queryDocument" )
    @ResponseBody
    public void queryDocument() throws Exception {
        String index = elasticSearchService.queryDocument();
        System.out.println(index);
    }

    @RequestMapping(value = "/queryByField" )
    @ResponseBody
    public void queryByField() throws Exception {
        String index = elasticSearchService.queryByField();
        System.out.println(index);
    }

    @RequestMapping(value = "/updateDocument" )
    @ResponseBody
    public void updateDocument() throws Exception {
        String index = elasticSearchService.updateDocument();
        System.out.println(index);
    }


    @RequestMapping(value = "/deleteDocumentByCondition" )
    @ResponseBody
    public void deleteDocumentByCondition() throws Exception {
        String index = elasticSearchService.deleteDocumentByCondition();
        System.out.println(index);
    }

}
