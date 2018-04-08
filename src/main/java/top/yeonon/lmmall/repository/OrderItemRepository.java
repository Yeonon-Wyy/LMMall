package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.lmmall.entity.OrderItem;

import java.util.List;

@Mapper
public interface OrderItemRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectOrderItemsByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);
}