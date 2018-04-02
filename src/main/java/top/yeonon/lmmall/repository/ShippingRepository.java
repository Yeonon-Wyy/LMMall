package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import top.yeonon.lmmall.entity.Shipping;

@Mapper
public interface ShippingRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
}