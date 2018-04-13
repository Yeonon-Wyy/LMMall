package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import top.yeonon.lmmall.entity.Cart;

import java.util.List;

@Mapper
public interface CartRepository {

    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartsByUserId(Integer userId);

    int selectCartProductCheckedByUserId(Integer userId);

    int deleteProductFromCart(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    int checkOrUnCheckedCart(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    int selectTotalCount(Integer userId);

    List<Cart> selectCheckedCartsByUserId(Integer userId);


}