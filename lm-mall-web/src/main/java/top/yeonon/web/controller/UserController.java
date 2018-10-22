package top.yeonon.web.controller;

import com.auth0.jwt.JWT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ResponseCode;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.serivice.IUserService;
import top.yeonon.web.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:36
 **/
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 注册用户接口
     */
    @PostMapping
    public ServerResponse register(User user) {
        return userService.register(user);
    }

    /**
     * 忘记密码获取用户密保问题接口
     */
    @GetMapping("question/{username}")
    public ServerResponse<String> getQuestion(@PathVariable("username") String username) {
        return userService.getQuestionByUsername(username);
    }

    /**
     * 输入密保问题答案
     */
    @PostMapping("answer/{username}")
    public ServerResponse<String> getAnswer(@PathVariable("username") String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    /**
     * 修改密码（忘记密码的情况下，携带Token）
     */
    @PostMapping("password/{username}")
    public ServerResponse updatePassword(@PathVariable("username") String username, String newPassword, String forgetToken) {
        return userService.updatePassword(username, newPassword, forgetToken);
    }

    @PutMapping("{username}")
    @Consumer
    public ServerResponse updateUserInfo(HttpServletRequest request, @PathVariable("username") String username, User user) {
        String userId = getUserId(request);
        User currentUser = userService.getUserInfo(Integer.valueOf(userId)).getData();
        if (!StringUtils.equals(username, currentUser.getUsername())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误，有可能是黑客攻击！");
        }
        user.setId(currentUser.getId());
        return userService.updateUserInfo(user);
    }

    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
    }
}
