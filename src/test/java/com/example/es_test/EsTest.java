package com.example.es_test;

import com.example.es_test.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EsTest {

    @Autowired
    ElasticSearchService service;

    @Test
    public void esCreatIndex(){
        service.createIndex("hello");
    }


    @Test
    public void esaddDocument(){
        service.addDocument("hello","_doc",null);
    }

}
