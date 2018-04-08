package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.lmmall.entity.Order;

@Mapper
public interface OrderRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectOrderByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);
}