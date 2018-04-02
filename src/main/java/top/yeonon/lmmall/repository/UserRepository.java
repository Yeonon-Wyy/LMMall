package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import top.yeonon.lmmall.entity.User;

@Mapper
public interface UserRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}