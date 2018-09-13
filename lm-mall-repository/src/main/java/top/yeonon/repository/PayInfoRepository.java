package top.yeonon.repository;

import org.apache.ibatis.annotations.Mapper;
import top.yeonon.entity.PayInfo;

@Mapper
public interface PayInfoRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);
}