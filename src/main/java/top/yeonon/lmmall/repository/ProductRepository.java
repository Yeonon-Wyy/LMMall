package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.lmmall.entity.Product;

import java.util.List;

@Mapper
public interface ProductRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectProductsByNameAndProductIds(@Param("productName") String productName, @Param("productIds") List<Integer> productIds);
}