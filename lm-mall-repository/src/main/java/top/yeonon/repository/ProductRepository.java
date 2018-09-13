package top.yeonon.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.entity.Product;


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

    List<Product> selectProducts();

    List<Product> searchProductsByNameAndId(@Param("productName") String productName, @Param("productId") Integer productId);

    //因为这里有可能查不到任何stock(0也算是查到数据),如果返回值是int的话，mybatis就会报错，Integer就返回null
    Integer selectStockByProductId(Integer productId);
}