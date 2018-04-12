package top.yeonon.lmmall.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.service.ICartService;
import top.yeonon.lmmall.token.TokenGenerator;
import top.yeonon.lmmall.vo.CartVo;

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
        Integer userId = getUserId(request);
        return cartService.addProductToCart(userId, productId, count);
    }

    /**
     *  更新商品的数量
     */
    @PutMapping("{productId}")
    @Consumer
    public ServerResponse<CartVo> updateProductToCart(HttpServletRequest request, @PathVariable("productId") Integer productId, Integer count) {
        Integer userId = getUserId(request);
        return cartService.updateProductToCart(userId, productId, count);
    }

    /**
     *  删除商品
     */
    @DeleteMapping("{productIds}")
    @Consumer
    public ServerResponse<CartVo> deleteProductFromCart(HttpServletRequest request, @PathVariable("productIds") String productIds) {
        Integer userId = getUserId(request);
        return cartService.deleteProductFromCart(userId, productIds);
    }

    /**
     *  获取整个购物车列表
     */
    @GetMapping
    @Consumer
    public ServerResponse<CartVo> getLists(HttpServletRequest request) {
        Integer userId = getUserId(request);
        return cartService.getLists(userId);
    }

    /**
     *  全部勾选
     */
    @PostMapping("selectAll")
    @Consumer
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        Integer userId = getUserId(request);
        return cartService.selectOrUnSelect(userId, null, ServerConst.Cart.CHECKED);
    }

    /**
     * 全部不勾选
     */
    @PostMapping("unSelectAll")
    @Consumer
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest request) {
        Integer userId = getUserId(request);
        return cartService.selectOrUnSelect(userId,null, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  单选
     */
    @PostMapping("select/{productId}")
    @Consumer
    public ServerResponse<CartVo> SelectProduct(HttpServletRequest request, @PathVariable("productId") Integer productId) {
        Integer userId = getUserId(request);
        return cartService.selectOrUnSelect(userId,productId, ServerConst.Cart.CHECKED);
    }

    /**
     * 单不选
     */
    @PostMapping("unSelect/{productId}")
    @Consumer
    public ServerResponse<CartVo> unSelectProduct(HttpServletRequest request, @PathVariable("productId") Integer productId) {
        Integer userId = getUserId(request);
        return cartService.selectOrUnSelect(userId,productId, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  获取购物车中商品的数量
     */
    @GetMapping("totalCount")
    @Consumer
    public ServerResponse<CartVo> getTotalCount(HttpServletRequest request) {
        Integer userId = getUserId(request);
        return cartService.getTotalCount(userId);
    }


    private Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.TOKEN_PAYLOAD_NAME).asInt();
    }

}
