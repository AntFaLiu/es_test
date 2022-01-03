package com.example.es_test.service;

public interface ElasticSearchService {
    void createIndex(String index);

    void deleteIndex(String index);

    void addDocument(String index, String type, String id);

    void getDocument(String index, String type, String id);
}
