package top.yeonon.controller;

import com.auth0.jwt.JWT;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.entity.Shipping;
import top.yeonon.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.service.IShippingService;

import javax.servlet.http.HttpServletRequest;

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
        String userId = getUserId(request);
        return shippingService.addShipping(Integer.valueOf(userId), shipping);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("{shippingId}")
    @Consumer
    public ServerResponse deleteShipping(HttpServletRequest request, @PathVariable("shippingId") Integer shippingId) {
        String userId = getUserId(request);
        return shippingService.deleteShipping(Integer.valueOf(userId), shippingId);
    }

    /**
     *  更新某个收货地址
     */
    @PutMapping("{shippingId}")
    @Consumer
    public ServerResponse updateShipping(HttpServletRequest request, @PathVariable("shippingId") Integer shippingId, Shipping shipping) {
        String userId = getUserId(request);
        shipping.setId(shippingId);
        return shippingService.updateShipping(Integer.valueOf(userId), shipping);
    }

    /**
     * 选择查看某个收货地址
     */
    @GetMapping("{shippingId}")
    @Consumer
    public ServerResponse<Shipping> selectShipping(HttpServletRequest request, @PathVariable("shippingId") Integer shippingId) {
        String userId = getUserId(request);
        return shippingService.selectShipping(Integer.valueOf(userId), shippingId);
    }

    /**
     * 获取收货地址列表
     */
    @GetMapping
    @Consumer
    public ServerResponse<PageInfo> getShippingList(HttpServletRequest request,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String userId = getUserId(request);
        return shippingService.getShippingList(Integer.valueOf(userId), pageNum, pageSize);
    }

    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
    }
}
