package top.yeonon.lmmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.ISessionService;

import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/4 0004 22:53
 **/
@RestController
@RequestMapping("/manage/sessions")
public class SessionManagerController {

    @Autowired
    private ISessionService sessionService;

    @PostMapping
    public ServerResponse<User> login(HttpSession session, String username, String password) {
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if (user.getRole() == ServerConst.Role.ADMIN.getCode()) {
                session.setAttribute(ServerConst.SESSION_KEY_FOR_CURRENT, user);
                return ServerResponse.createBySuccess("登录成功", user);
            }
            return ServerResponse.createByErrorMessage("不是管理员");
        }
        return serverResponse;
    }


}
