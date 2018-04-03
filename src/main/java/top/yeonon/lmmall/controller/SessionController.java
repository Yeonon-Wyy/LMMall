package top.yeonon.lmmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.ISessionService;
import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 20:47
 **/
@RestController
@RequestMapping("sessions")
public class SessionController {

    @Autowired
    private ISessionService sessionService;

    /**
     * 登录接口
     */
    @PostMapping
    public ServerResponse<User> login(HttpSession session, String username, String password) {
        //先查看Session中是否已经有用户信息，有就表示用户已经登录，禁止重复登录
        User currentUser = getUserInfoInSession(session);
        if (currentUser != null) {
            return ServerResponse.createByErrorMessage("用户" + username + "已登录，请勿重复登录！");
        }
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            session.setAttribute(ServerConst.SESSION_KEY_FOR_CURRENT, serverResponse.getData());
        }
        return serverResponse;
    }

    /**
     * 登出接口
     */
    @SuppressWarnings("unchecked")
    @DeleteMapping
    public ServerResponse logout(HttpSession session) {
        User currentUser = getUserInfoInSession(session);
        if (currentUser != null) {
            session.removeAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
            return ServerResponse.createBySuccessMessage("登出成功!");
        }
        return ServerResponse.createByErrorMessage("登出失败，可能用户并没有登录，故导致登出失败！");
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User currentUser = getUserInfoInSession(session);
        if (currentUser != null) {
            return ServerResponse.createBySuccess(currentUser);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，无法获取用户信息");
    }

    /**
     * 获取Session中的用户信息
     */
    private User getUserInfoInSession(HttpSession session) {
        return (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
    }

}
