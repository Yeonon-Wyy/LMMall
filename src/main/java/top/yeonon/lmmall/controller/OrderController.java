package top.yeonon.lmmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.IOrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 19:56
 **/
@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private IOrderService orderService;

    @PostMapping("{orderNo}/pay")
    public ServerResponse pay(HttpSession session, @PathVariable("orderNo") Long orderNo, HttpServletRequest request) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        String path = request.getServletContext().getRealPath("upload");
        return orderService.pay(user.getId(), orderNo, path);
    }
}
