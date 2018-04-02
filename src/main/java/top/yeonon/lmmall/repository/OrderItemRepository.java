package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import top.yeonon.lmmall.entity.OrderItem;

@Mapper
public interface OrderItemRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}