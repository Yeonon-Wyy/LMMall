package top.yeonon.lmmall.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.IUserService;

import javax.servlet.http.HttpSession;

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
    public ServerResponse updateUserInfo(HttpSession session, @PathVariable("username") String username, User user) {
        User currentUser = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (!StringUtils.equals(username, currentUser.getUsername())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误，有可能是黑客攻击！");
        }
        user.setId(currentUser.getId());
        ServerResponse serverResponse = userService.updateUserInfo(user);
        if (serverResponse.isSuccess()) {
            session.setAttribute(ServerConst.SESSION_KEY_FOR_CURRENT, serverResponse.getData());
        }
        return serverResponse;
    }
}
