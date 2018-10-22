package top.yeonon.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import org.springframework.stereotype.Repository;
import top.yeonon.entity.Product;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/6/17 0017 12:29
 **/
@Repository
public interface ProductElasticRespository extends ElasticsearchRepository<Product, Integer> {

    List<Product> findByCategoryIdIn(List<Integer> categoryId);
}
