package top.yeonon.lmmall.repository;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.lmmall.entity.Order;

import java.util.List;

@Mapper
public interface OrderRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectOrderByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    Order selectOrderByOrderNo(Long orderNo);

    List<Order> selectOrdersByUserId(Integer userId);

    List<Order> selectAllOrders();

    List<Order> selectOrdersStatusByCreateTime(@Param("status") int status, @Param("orderDate") String orderDate);

    int closeOrderByOrderId(Integer orderId);
}