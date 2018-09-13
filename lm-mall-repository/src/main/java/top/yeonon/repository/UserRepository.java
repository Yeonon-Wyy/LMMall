package top.yeonon.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yeonon.entity.User;

import java.util.List;

@Mapper
public interface UserRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserField(@Param("info") String info, @Param("type") String type);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePassword(@Param("username") String username, @Param("password") String password);

    int checkEmailByUserId(@Param("email") String email, @Param("id") Integer id);

    int checkPhoneByUserId(@Param("phone") String phone, @Param("id") Integer id);

    List<User> selectUserList();

    User selectUserByUserName(String username);
}