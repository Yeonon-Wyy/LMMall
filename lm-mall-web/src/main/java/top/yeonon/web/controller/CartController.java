package top.yeonon.web.controller;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.serivice.ICartService;
import top.yeonon.serivice.vo.CartVo;
import top.yeonon.web.interceptor.authenticationAnnotation.Consumer;


import javax.servlet.http.HttpServletRequest;

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
    @Consumer
    public ServerResponse<CartVo> addProductToCart(HttpServletRequest request, @PathVariable("productId") Integer productId, Integer count) {
        String userId = getUserId(request);
        return cartService.addProductToCart(Integer.valueOf(userId), productId, count);
    }

    /**
     *  更新商品的数量
     */
    @PutMapping("{productId}")
    @Consumer
    public ServerResponse<CartVo> updateProductToCart(HttpServletRequest request, @PathVariable("productId") Integer productId, Integer count) {
        String userId = getUserId(request);
        return cartService.updateProductToCart(Integer.valueOf(userId), productId, count);
    }

    /**
     *  删除商品
     */
    @DeleteMapping("{productIds}")
    @Consumer
    public ServerResponse<CartVo> deleteProductFromCart(HttpServletRequest request, @PathVariable("productIds") String productIds) {
        String userId = getUserId(request);
        return cartService.deleteProductFromCart(Integer.valueOf(userId), productIds);
    }

    /**
     *  获取整个购物车列表
     */
    @GetMapping
    @Consumer
    public ServerResponse<CartVo> getLists(HttpServletRequest request) {
        String userId = getUserId(request);
        return cartService.getLists(Integer.valueOf(userId));
    }

    /**
     *  全部勾选
     */
    @PostMapping("selectAll")
    @Consumer
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        String userId = getUserId(request);
        return cartService.selectOrUnSelect(Integer.valueOf(userId), null, ServerConst.Cart.CHECKED);
    }

    /**
     * 全部不勾选
     */
    @PostMapping("unSelectAll")
    @Consumer
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest request) {
        String userId = getUserId(request);
        return cartService.selectOrUnSelect(Integer.valueOf(userId),null, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  单选
     */
    @PostMapping("select/{productId}")
    @Consumer
    public ServerResponse<CartVo> SelectProduct(HttpServletRequest request, @PathVariable("productId") Integer productId) {
        String userId = getUserId(request);
        return cartService.selectOrUnSelect(Integer.valueOf(userId),productId, ServerConst.Cart.CHECKED);
    }

    /**
     * 单不选
     */
    @PostMapping("unSelect/{productId}")
    @Consumer
    public ServerResponse<CartVo> unSelectProduct(HttpServletRequest request, @PathVariable("productId") Integer productId) {
        String userId = getUserId(request);
        return cartService.selectOrUnSelect(Integer.valueOf(userId),productId, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  获取购物车中商品的数量
     */
    @GetMapping("totalCount")
    @Consumer
    public ServerResponse<CartVo> getTotalCount(HttpServletRequest request) {
        String userId = getUserId(request);
        return cartService.getTotalCount(Integer.valueOf(userId));
    }


    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
    }

}
