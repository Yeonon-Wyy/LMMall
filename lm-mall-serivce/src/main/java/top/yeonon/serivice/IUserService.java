package top.yeonon.serivice;


import top.yeonon.common.ServerResponse;
import top.yeonon.entity.User;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:33
 **/
public interface IUserService {
    ServerResponse register(User user);

    ServerResponse<String> getQuestionByUsername(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse updatePassword(String username, String newPassword, String forgetToken);

    ServerResponse updateUserInfo(User user);

    ServerResponse<User> getUserInfo(Integer userId);

    /**
     * 后台管理的方法
     */
    ServerResponse deleteUser(Integer id);

    ServerResponse<List<User>> getUserList();

    ServerResponse checkAdminRole(User user);
}
