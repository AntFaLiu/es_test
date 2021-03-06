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
            //????????????????????????
            if (StringUtils.isEmpty(id)) {
                indexRequest = new IndexRequest(index, type);
            } else {
                indexRequest = new IndexRequest(index, type, id);
            }

            //??????????????????
            People hello = new People();
            hello.setTitle("??????");
            hello.setId(29);
            //?????????????????? byte ??????
            byte[] json = JSON.toJSONBytes(hello);
            //??????????????????
            indexRequest.source(json, XContentType.JSON);
            //??????????????????
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println("response:" + response.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getDocument(String index, String type, String id) {
        try {
            //??????????????????
            GetRequest getRequest = new GetRequest(index, type, id);
            //??????????????????
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            //??? JSON ???????????????
            if (getResponse.isExists()) {
                People people = JSON.parseObject(getResponse.getSourceAsBytes(), People.class);
                System.out.println("???????????? : " + people);
            }
        } catch (IOException e) {
            System.out.println("" + e);
        }
    }
}
