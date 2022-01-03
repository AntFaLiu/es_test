package com.example.es_test.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.es_test.entity.People;
import com.example.es_test.service.ElasticSearchService;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.io.IOException;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    RestHighLevelClient client;

    @Override
    public void createIndex(String index) {
        try {
            XContentBuilder mappings = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("id")
                    .field("type", "long")
                    .endObject()
                    .startObject("adviceTypeId")
                    .field("type", "long")
                    .endObject()
                    .startObject("title")
                    .field("type", "text")
                    .field("store", true)
                    .endObject()
                    .endObject()
                    .endObject();

            CreateIndexRequest request = new CreateIndexRequest(index)
                    .settings(Settings.builder()
                            .put("number_of_shards", 5)
                            .put("number_of_shards", 1)
                            .build())
                    .mapping("_doc", mappings);
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteIndex(String index) {
        AcknowledgedResponse response = null;
        try {
            response = client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            System.out.println("result:" + response.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDocument(String index, String type, String id) {
        try {
            IndexRequest indexRequest = null;
            //创建索引请求对象
            if (StringUtils.isEmpty(id)) {
                indexRequest = new IndexRequest(index, type);
            } else {
                indexRequest = new IndexRequest(index, type, id);
            }

            //创建员工信息
            People hello = new People();
            hello.setTitle("张三");
            hello.setId(29);
            //将对象转换为 byte 数组
            byte[] json = JSON.toJSONBytes(hello);
            //设置文档内容
            indexRequest.source(json, XContentType.JSON);
            //执行增加文档
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println("response:" + response.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getDocument(String index, String type, String id) {
        try {
            //获取请求对象
            GetRequest getRequest = new GetRequest(index, type, id);
            //获取文档信恩
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            //将 JSON 转换成对泉
            if (getResponse.isExists()) {
                People people = JSON.parseObject(getResponse.getSourceAsBytes(), People.class);
                System.out.println("员工信息 : " + people);
            }
        } catch (IOException e) {
            System.out.println("" + e);
        }
    }
}
