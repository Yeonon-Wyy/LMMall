package top.yeonon.lmmall.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.properties.CoreProperties;
import top.yeonon.lmmall.service.ISessionService;
import top.yeonon.lmmall.token.TokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 20:47
 **/
@RestController
@RequestMapping("token")
@Log
public class TokenController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private TokenGenerator<String, DecodedJWT> jwtTokenGenerator;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private CoreProperties coreProperties;

    /**
     * 登录接口
     */
    @PostMapping
    public ServerResponse<User> login(HttpServletResponse response, String username, String password) {
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            String accessToken;
            String refreshToken;
            try {
                accessToken = jwtTokenGenerator.generate(user.getId().toString(),
                        coreProperties.getSecurity().getToken().getAccessTokenExpireIn());
                refreshToken = jwtTokenGenerator.generate(ServerConst.Token.REFRESH_TOKEN_PAYLOAD_NAME + user.getId().toString(),
                        coreProperties.getSecurity().getToken().getRefreshTokenExpireIn());
            } catch (Exception e) {
                log.info("生成jwt失败");
                return ServerResponse.createByErrorMessage("登录失败");
            }
            redisTemplate.opsForValue().set(user.getId().toString(), user);
            response.setHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME, accessToken);
            response.setHeader(ServerConst.Token.LMMALL_REFRESH_TOKEN_NAME, refreshToken);
        }
        return serverResponse;
    }

    /**
     * 登出接口
     */
    @SuppressWarnings("unchecked")
    @DeleteMapping
    @Consumer
    public ServerResponse logout(HttpServletRequest request) {
        String userId = getUserId(request);
        redisTemplate.delete(userId);
        return ServerResponse.createBySuccessMessage("登出成功!");
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping
    @Consumer
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String userId = getUserId(request);
        User currentUser = (User) redisTemplate.opsForValue().get(userId);
        return ServerResponse.createBySuccess(currentUser);
    }

    @GetMapping("refresh")
    public ServerResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refreshToken = request.getHeader(ServerConst.Token.LMMALL_REFRESH_TOKEN_NAME);
        if (StringUtils.isEmpty(refreshToken)) {
            return ServerResponse.createByErrorMessage("请在请求头中携带refreshToken");
        }
        DecodedJWT jwt = null;
        try {
            jwt = jwtTokenGenerator.verifyToken(refreshToken);
        } catch (Exception e) {
            throw new JWTVerificationException("token过期或者错误");
        }
        String refreshTokenPayLoad = jwt.getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
        if (!StringUtils.contains(refreshTokenPayLoad,ServerConst.Token.REFRESH_TOKEN_PAYLOAD_NAME)) {
            throw new JWTVerificationException("你传的不是refreshToken");
        }
        String userId = StringUtils.substringAfter(refreshTokenPayLoad, ServerConst.Token.REFRESH_TOKEN_PAYLOAD_NAME);
        String newAccessToken = jwtTokenGenerator.generate(userId, coreProperties.getSecurity().getToken().getAccessTokenExpireIn());
        response.setHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME, newAccessToken);
        return ServerResponse.createBySuccessMessage("刷新token成功!");
    }


    /**
     * 从token中解码token，并获取用户ID
     */
    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
    }



}
