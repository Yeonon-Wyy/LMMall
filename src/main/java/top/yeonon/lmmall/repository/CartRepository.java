package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import top.yeonon.lmmall.entity.Cart;

@Mapper
public interface CartRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
}