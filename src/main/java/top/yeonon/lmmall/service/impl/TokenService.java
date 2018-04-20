package top.yeonon.lmmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.ITokenService;
import top.yeonon.lmmall.utils.MD5Utils;

/**
 * @Author yeonon
 * @date 2018/4/3 0003 19:48
 **/
@Service
public class TokenService implements ITokenService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int rowCount = userRepository.checkUserField(username,
                ServerConst.UserFieldValidate.USERNAME.getDesc());

        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("该用户名不存在");
        }

        String newPassword = MD5Utils.MD5EncodeUtf8(password);
        User user = userRepository.selectLogin(username, newPassword);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户名或者密码输入错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }
}
