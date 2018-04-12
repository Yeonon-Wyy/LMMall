package top.yeonon.lmmall.controller.backend;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Manager;
import top.yeonon.lmmall.properties.CoreProperties;
import top.yeonon.lmmall.service.ISessionService;
import top.yeonon.lmmall.service.IUserService;
import top.yeonon.lmmall.token.TokenGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @Author yeonon
 * @date 2018/4/4 0004 22:53
 **/
@RestController
@RequestMapping("/manage/token")
@Log
public class TokenManagerController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private TokenGenerator<Integer, DecodedJWT> jwtTokenGenerator;

    @Autowired
    private CoreProperties coreProperties;

    @PostMapping
    public ServerResponse<User> login(HttpServletResponse response, String username, String password) {
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            ServerResponse checkResponse = userService.checkAdminRole(user);
            if (checkResponse.isSuccess()) {
                String accessToken;
                String refreshToken;
                try {
                    accessToken = jwtTokenGenerator.generate(user.getId(), coreProperties.getSecurity().getTokenExpire());
                    refreshToken = jwtTokenGenerator.generate(user.getId(), 10080);
                } catch (Exception e) {
                    return ServerResponse.createByErrorMessage("登录失败");
                }
                redisTemplate.opsForValue().set(user.getId(), user);
                response.setHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME, accessToken);
                response.setHeader(ServerConst.LMMALL_REFRESH_TOKEN_NAME, refreshToken);
            }
            else {
                return ServerResponse.createByErrorMessage("不是管理员");
            }
        }
        return serverResponse;
    }

    @DeleteMapping
    @Manager
    public ServerResponse logout(HttpServletRequest request) {
        Integer userId = getUserId(request);
        redisTemplate.delete(userId);
        return ServerResponse.createBySuccessMessage("登出成功!");
    }

    private Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.TOKEN_PAYLOAD_NAME).asInt();
    }

}
