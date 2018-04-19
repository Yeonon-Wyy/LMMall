package top.yeonon.lmmall.oauth.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.OAuthUser;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.repository.OAuthUserRepository;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.IUserService;

import java.util.List;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 12:25
 **/
@Service
public class OAuthServices {

    @Autowired
    private List<AbstractOAuthService> oAuthServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Autowired
    private IUserService userService;

    public AbstractOAuthService getOAuthService(String type) {
        for (AbstractOAuthService oAuthService : oAuthServices) {
            if (oAuthService.getoAuthType().equals(type)) {
                return oAuthService;
            }
        }
        return null;
    }

    public Map<String, String> getAuthorizationUrls() {
        Map<String, String> result = Maps.newHashMap();
        for (AbstractOAuthService oAuthService : oAuthServices) {
            result.put(oAuthService.getoAuthType(), oAuthService.getAuthorizeUrl());
        }
        return result;
    }


    /**
     * 第三方登录的跳转接口
     */

    public ServerResponse oauthRegister(String oAuthType, Integer oAuthId, User user) {
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
}
