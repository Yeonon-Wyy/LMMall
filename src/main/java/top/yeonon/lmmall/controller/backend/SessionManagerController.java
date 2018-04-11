package top.yeonon.lmmall.controller.backend;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Manager;
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
@RequestMapping("/manage/sessions")
@Log
public class SessionManagerController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private TokenGenerator<String> jwtTokenGenerator;

    @PostMapping
    public ServerResponse<User> login(HttpServletResponse response, String username, String password) {
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            ServerResponse checkResponse = userService.checkAdminRole(user);
            if (checkResponse.isSuccess()) {
                String token = null;
                try {
                    token = jwtTokenGenerator.generate(username);
                } catch (Exception e) {
                    log.info("生成jwt失败");
                    return ServerResponse.createByErrorMessage("登录失败");
                }
                redisTemplate.opsForValue().set(token, serverResponse.getData(), 30, TimeUnit.MINUTES);
                response.setHeader("lmmall_login_token", token);
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
        redisTemplate.delete(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return ServerResponse.createBySuccessMessage("管理员登出成功");
    }

}
