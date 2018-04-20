package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;

/**
 * @Author yeonon
 * @date 2018/4/3 0003 19:48
 **/
public interface ITokenService {

    ServerResponse<User> login(String username, String password);
}
