package top.yeonon.lmmall.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.service.ISessionService;
import top.yeonon.lmmall.token.TokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

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
    private TokenGenerator<String> jwtTokenGenerator;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 登录接口
     */
    @PostMapping
    public ServerResponse<User> login(HttpServletResponse response, String username, String password) {
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            String token;
            try {
                token = jwtTokenGenerator.generate(username);
            } catch (Exception e) {
                log.info("生成jwt失败");
                return ServerResponse.createByErrorMessage("登录失败");
            }
            redisTemplate.opsForValue().set(token, serverResponse.getData(), 30, TimeUnit.MINUTES);
            response.setHeader("lmmall_login_token", token);
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
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        redisTemplate.delete(token);
        return ServerResponse.createBySuccessMessage("登出成功!");
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping
    @Consumer
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        User currentUser = (User) redisTemplate.opsForValue().get(token);
        return ServerResponse.createBySuccess(currentUser);
    }

}
