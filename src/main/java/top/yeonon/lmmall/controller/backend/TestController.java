package top.yeonon.lmmall.controller.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;

import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/4 0004 22:41
 **/
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public ServerResponse getSession(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        System.out.println(user.getUsername());
        return ServerResponse.createBySuccess(user);
    }
}
