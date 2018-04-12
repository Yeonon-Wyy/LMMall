package top.yeonon.lmmall.controller;

import com.auth0.jwt.JWT;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Shipping;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.service.IShippingService;

import javax.servlet.http.HttpServletRequest;
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
    public ServerResponse addShipping(HttpServletRequest request, Shipping shipping) {
        Integer userId = getUserId(request);
        return shippingService.addShipping(userId, shipping);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("{shippingId}")
    @Consumer
    public ServerResponse deleteShipping(HttpServletRequest request, @PathVariable("shippingId") Integer shippingId) {
        Integer userId = getUserId(request);
        return shippingService.deleteShipping(userId, shippingId);
    }

    /**
     *  更新某个收货地址
     */
    @PutMapping("{shippingId}")
    @Consumer
    public ServerResponse updateShipping(HttpServletRequest request, @PathVariable("shippingId") Integer shippingId, Shipping shipping) {
        Integer userId = getUserId(request);
        shipping.setId(shippingId);
        return shippingService.updateShipping(userId, shipping);
    }

    /**
     * 选择查看某个收货地址
     */
    @GetMapping("{shippingId}")
    @Consumer
    public ServerResponse<Shipping> selectShipping(HttpServletRequest request, @PathVariable("shippingId") Integer shippingId) {
        Integer userId = getUserId(request);
        return shippingService.selectShipping(userId, shippingId);
    }

    /**
     * 获取收货地址列表
     */
    @GetMapping
    @Consumer
    public ServerResponse<PageInfo> getShippingList(HttpServletRequest request,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Integer userId = getUserId(request);
        return shippingService.getShippingList(userId, pageNum, pageSize);
    }

    private Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.TOKEN_PAYLOAD_NAME).asInt();
    }
}
