package top.yeonon.lmmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.ICartService;

import javax.servlet.http.HttpSession;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 18:24
 **/
@RestController
@RequestMapping("carts")
public class CartController {

    @Autowired
    private ICartService cartService;

    /**
     *  添加商品到购物车里
     */
    @PostMapping("{productId}")
    public ServerResponse addProductToCart(HttpSession session, @PathVariable("productId") Integer productId, Integer count) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }

        return cartService.addProductToCart(user.getId(), productId, count);
    }

    /**
     *  更新商品的数量
     */
    @PutMapping("{productId}")
    public ServerResponse updateProductToCart(HttpSession session, @PathVariable("productId") Integer productId, Integer count) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.updateProductToCart(user.getId(), productId, count);
    }

    /**
     *  删除商品
     */
    @DeleteMapping("{productIds}")
    public ServerResponse deleteProductFromCart(HttpSession session, @PathVariable("productIds") String productIds) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.deleteProductFromCart(user.getId(), productIds);
    }

    /**
     *  获取整个购物车列表
     */
    @GetMapping
    public ServerResponse getLists(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.getLists(user.getId());
    }

    /**
     *  全部勾选
     */
    @PostMapping("selectAll")
    public ServerResponse selectAll(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.selectOrUnSelect(user.getId(), null, ServerConst.Cart.CHECKED);
    }

    /**
     * 全部不勾选
     */
    @PostMapping("unSelectAll")
    public ServerResponse unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.selectOrUnSelect(user.getId(),null, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  单选
     */
    @PostMapping("select/{productId}")
    public ServerResponse SelectProduct(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.selectOrUnSelect(user.getId(),productId, ServerConst.Cart.CHECKED);
    }

    /**
     * 单不选
     */
    @PostMapping("unSelect/{productId}")
    public ServerResponse unSelectProduct(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.selectOrUnSelect(user.getId(),productId, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  获取购物车中商品的数量
     */
    @GetMapping("totalCount")
    public ServerResponse getTotalCount(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        return cartService.getTotalCount(user.getId());
    }


}
