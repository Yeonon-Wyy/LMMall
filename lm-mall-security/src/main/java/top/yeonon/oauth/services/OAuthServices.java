package top.yeonon.oauth.services;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.entity.OAuthUser;
import top.yeonon.entity.User;
import top.yeonon.repository.OAuthUserRepository;
import top.yeonon.repository.UserRepository;
import top.yeonon.service.IUserService;
import top.yeonon.util.MD5Utils;


import java.util.List;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 12:25
 **/
@Service
public class OAuthServices {

    @Autowired
    private List<CustomerOAuthService> oAuthServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Autowired
    private IUserService userService;

    public CustomerOAuthService getOAuthService(String type) {
        for (CustomerOAuthService oAuthService : oAuthServices) {
            if (oAuthService.getoAuthType().equals(type)) {
                return oAuthService;
            }
        }
        return null;
    }

    public Map<String, String> getAuthorizationUrls() {
        Map<String, String> result = Maps.newHashMap();
        for (CustomerOAuthService oAuthService : oAuthServices) {
            result.put(oAuthService.getoAuthType(), oAuthService.getAuthorizationUrl());
        }
        return result;
    }


    /**
     * 第三方登录的跳转接口
     */

    public ServerResponse oauthRegister(String oAuthType, String oAuthId, User user) {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setOauthType(oAuthType);
        oAuthUser.setOauthId(oAuthId);
        ServerResponse serverResponse = userService.register(user);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        User newUser = userRepository.selectUserByUserName(user.getUsername());
        oAuthUser.setUserId(newUser.getId());
        int rowCount = oAuthUserRepository.insert(oAuthUser);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("数据库异常！");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 绑定接口
     */
    public ServerResponse bind(String username, String password, String oAuthType, String oAuthId) {
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

        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setOauthId(oAuthId);
        oAuthUser.setOauthType(oAuthType);
        oAuthUser.setUserId(user.getId());
        rowCount = oAuthUserRepository.insert(oAuthUser);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("绑定失败");
        }
        return ServerResponse.createBySuccessMessage("绑定成功");
    }
}
