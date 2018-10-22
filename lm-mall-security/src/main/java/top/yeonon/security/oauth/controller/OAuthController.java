package top.yeonon.security.oauth.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.common.properties.CoreProperties;
import top.yeonon.security.oauth.services.CustomerOAuthService;
import top.yeonon.security.oauth.services.OAuthServices;
import top.yeonon.security.token.TokenGenerator;
import top.yeonon.entity.OAuthUser;
import top.yeonon.entity.User;
import top.yeonon.repository.OAuthUserRepository;
import top.yeonon.repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 12:28
 **/
@RestController
@RequestMapping("oauth")
public class OAuthController {

    @Autowired
    private OAuthServices oAuthServices;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoreProperties coreProperties;

    @Autowired
    private TokenGenerator<String, DecodedJWT> jwtTokenGenerator;

    @GetMapping("/authorizationUrls")
    public ServerResponse<Map<String, String>> getAuthorizationUrls() {
        return ServerResponse.createBySuccess(oAuthServices.getAuthorizationUrls());
    }

    @GetMapping("/{type}/callback")
    public ServerResponse oauthCallback(@PathVariable("type") String type, HttpServletResponse response,
                                        @RequestParam(value = "code", required = true) String code) {
        CustomerOAuthService oAuthService = oAuthServices.getOAuthService(type);
        Token accessToken = oAuthService.getAccessToken(null, new Verifier(code));
        OAuthUser userInfo = oAuthService.getOAuthUser(accessToken);
        OAuthUser oAuthUser = oAuthUserRepository.selectByOAuthTypeAndOAuthId(userInfo.getOauthType(), userInfo.getOauthId());
        if (oAuthUser == null) {
            Map<String, String> result = Maps.newHashMap();
            result.put("oauthType", userInfo.getOauthType());
            result.put("oauthId", userInfo.getOauthId());
            return ServerResponse.createBySuccess("请先去注册", result);
        }
        User user = userRepository.selectByPrimaryKey(oAuthUser.getUserId());
        if (user == null) {
            return ServerResponse.createByErrorMessage("不存在该用户");
        }
        String sysAccessToken = null;
        String refreshToken = null;
        try {
            sysAccessToken = jwtTokenGenerator.generate(user.getId().toString(),
                    coreProperties.getSecurity().getToken().getAccessTokenExpireIn());
            refreshToken = jwtTokenGenerator.generate(ServerConst.Token.REFRESH_TOKEN_PAYLOAD_NAME + user.getId().toString(),
                    coreProperties.getSecurity().getToken().getRefreshTokenExpireIn());
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("登录失败");
        }
        response.setHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME, sysAccessToken);
        response.setHeader(ServerConst.Token.LMMALL_REFRESH_TOKEN_NAME, refreshToken);
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @PostMapping("register")
    public ServerResponse register(User user,
                                   @RequestParam(value = "oAuthType", required = false) String oAuthType,
                                   @RequestParam(value = "oAuthId", required = true) String oAuthId) {

        return oAuthServices.oauthRegister(oAuthType, oAuthId, user);
    }

    @PostMapping("/bind")
    public ServerResponse bind(String username, String password,
                               @RequestParam(value = "oAuthType", required = false) String oAuthType,
                               @RequestParam(value = "oAuthId", required = true) String oAuthId) {
        return oAuthServices.bind(username, password, oAuthType, oAuthId);
    }
}
