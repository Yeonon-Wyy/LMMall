package top.yeonon.lmmall.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.lmmall.entity.OAuthUser;

@Mapper
public interface OAuthUserRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(OAuthUser record);

    int insertSelective(OAuthUser record);

    OAuthUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OAuthUser record);

    int updateByPrimaryKey(OAuthUser record);

    OAuthUser selectByOAuthTypeAndOAuthId(@Param("oAuthType") String oAuthType, @Param("oAuthId") Integer oAuthId);
}