package top.yeonon.lmmall.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.java.Log;
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
@RequestMapping("sessions")
@Log
public class SessionController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private TokenGenerator<Integer, DecodedJWT> jwtTokenGenerator;

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
                accessToken = jwtTokenGenerator.generate(user.getId(), coreProperties.getSecurity().getTokenExpire());
                refreshToken = jwtTokenGenerator.generate(user.getId(), 10080);
            } catch (Exception e) {
                log.info("生成jwt失败");
                return ServerResponse.createByErrorMessage("登录失败");
            }
            redisTemplate.opsForValue().set(user.getId(), user);
            response.setHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME, accessToken);
            response.setHeader(ServerConst.LMMALL_REFRESH_TOKEN_NAME, refreshToken);
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
        Integer userId = getUserId(request);
        redisTemplate.delete(userId);
        return ServerResponse.createBySuccessMessage("登出成功!");
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping
    @Consumer
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        Integer userId = getUserId(request);
        User currentUser = (User) redisTemplate.opsForValue().get(userId);
        return ServerResponse.createBySuccess(currentUser);
    }


    /**
     * 从token中解码token，并获取用户ID
     */
    private Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.TOKEN_PAYLOAD_NAME).asInt();
    }

}
