package top.yeonon.lmmall.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Shipping;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.service.IShippingService;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 15:11
 *
 * 这里的所有方法都是针对已登录用户的，未登录用户没有权限操作
 **/
@RestController
@RequestMapping("shipping")
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    /**
     * 添加收货地址
     */
    @PostMapping
    @Consumer
    public ServerResponse addShipping(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return shippingService.addShipping(user.getId(), shipping);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("{shippingId}")
    @Consumer
    public ServerResponse deleteShipping(HttpSession session, @PathVariable("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return shippingService.deleteShipping(user.getId(), shippingId);
    }

    /**
     *  更新某个收货地址
     */
    @PutMapping("{shippingId}")
    @Consumer
    public ServerResponse updateShipping(HttpSession session, @PathVariable("shippingId") Integer shippingId, Shipping shipping) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        shipping.setId(shippingId);
        return shippingService.updateShipping(user.getId(), shipping);
    }

    /**
     * 选择查看某个收货地址
     */
    @GetMapping("{shippingId}")
    @Consumer
    public ServerResponse<Shipping> selectShipping(HttpSession session, @PathVariable("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return shippingService.selectShipping(user.getId(), shippingId);
    }

    /**
     * 获取收货地址列表
     */
    @GetMapping
    @Consumer
    public ServerResponse<PageInfo> getShippingList(HttpSession session,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return shippingService.getShippingList(user.getId(), pageNum, pageSize);
    }
}
