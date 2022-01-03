package com.example.es_test;

import com.example.es_test.entity.Product;
import com.example.es_test.entity.ProductDao;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
public class SpringDataESIndexTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ProductDao productDao;

    //创建索引并增加映射配置
    @Test
    public void createIndex() {
//创建索引，系统初始化会自动创建索引
        System.out.println("创建索引");
    }

    @Test
    public void deleteIndex() {
//创建索引，系统初始化会自动创建索引
        DeleteIndexRequest indexRequest = new DeleteIndexRequest("shopping");
        elasticsearchRestTemplate.delete(indexRequest);
//        DeleteQuery deleteQuery=new DeleteQuery();
//        deleteQuery.setIndex("indexName");
//        boolean flg = productDao.delete(Product);
//        System.out.println("删除索引 = " + flg);
    }

    /**
     * 新增
     */
    @Test
    public void save() {
        Product product = new Product();
        product.setId(2L);
        product.setTitle("华为手机");
        product.setCategory("手机");
        product.setPrice(2999.0);
        product.setImages("http://www.atguigu/hw.jpg");
        productDao.save(product);
    }

    //修改
    @Test
    public void update() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("小米 2 手机");
        product.setCategory("手机");
        product.setPrice(9999.0);
        product.setImages("http://www.atguigu/xm.jpg");
        productDao.save(product);
    }

    //根据 id 查询
    @Test
    public void findById() {
        Product product = productDao.findById(1L).get();
        System.out.println(product);
    }

    @Test
    public void findAll() {
        Iterable<Product> products = productDao.findAll();
        for (Product product : products) {
            System.out.println(product);
        }
    }

    //删除
    @Test
    public void delete() {
        Product product = new Product();
        product.setId(1L);
        productDao.delete(product);
    }

    //批量新增
    @Test
    public void saveAll() {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setId(Long.valueOf(i));
            product.setTitle("[" + i + "]小米手机");
            product.setCategory("手机");
            product.setPrice(1999.0 + i);
            product.setImages("http://www.atguigu/xm.jpg");
            productList.add(product);
        }
        productDao.saveAll(productList);
    }

    //分页查询
    @Test
    public void findByPageable() {
//设置排序(排序方式，正序还是倒序，排序的 id)
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int currentPage = 0;//当前页，第一页从 0 开始， 1 表示第二页
        int pageSize = 5;//每页显示多少条
//设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
//分页查询
        Page<Product> productPage = productDao.findAll(pageRequest);
        for (Product Product : productPage.getContent()) {
            System.out.println(Product);
        }
    }

    /**
     * term 查询
     * search(termQueryBuilder) 调用搜索方法，参数查询构建器对象
     */
    @Test
    public void termQuery() {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "小米");
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        NativeSearchQuery query = builder.withQuery(QueryBuilders.termQuery("category", "手机"))
                .build();
        SearchHits<Product> products = elasticsearchRestTemplate.search(query, Product.class);
        Iterator<SearchHit<Product>> iterator = products.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().getContent());
        }
    }

    /**
     * term 查询加分页
     */
    @Test
    public void termQueryByPage() {
        int currentPage = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
        Pageable pageable= PageRequest.of(0,2);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "小米");
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        NativeSearchQuery query=builder.withQuery(QueryBuilders.termQuery("category", "手机"))
                .withPageable(pageable)
                .build();
        SearchHits<Product> products  = elasticsearchRestTemplate.search(query, Product.class);
        Iterator<SearchHit<Product>> iterator = products.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next().getContent());
        }

//设置查询分页
//        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "小米");
//        Iterable<Product> products =
//                productDao.search(termQueryBuilder, pageRequest);
//        for (Product product : products) {
//            System.out.println(product);
//        }
    }
}
