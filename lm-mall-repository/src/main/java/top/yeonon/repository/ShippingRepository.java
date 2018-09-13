package top.yeonon.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.entity.Shipping;

import java.util.List;

@Mapper
public interface ShippingRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteShippingByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    Shipping selectShippingByIdAndUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectShippingListByUserId(Integer userId);
}