package top.yeonon.lmmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.service.ICartService;
import top.yeonon.lmmall.vo.CartVo;

import javax.servlet.http.HttpServletRequest;
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


    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     *  添加商品到购物车里
     */
    @PostMapping("{productId}")
    @Consumer
    public ServerResponse<CartVo> addProductToCart(HttpServletRequest request, @PathVariable("productId") Integer productId, Integer count) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));

        return cartService.addProductToCart(user.getId(), productId, count);
    }

    /**
     *  更新商品的数量
     */
    @PutMapping("{productId}")
    @Consumer
    public ServerResponse<CartVo> updateProductToCart(HttpServletRequest request, @PathVariable("productId") Integer productId, Integer count) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.updateProductToCart(user.getId(), productId, count);
    }

    /**
     *  删除商品
     */
    @DeleteMapping("{productIds}")
    @Consumer
    public ServerResponse<CartVo> deleteProductFromCart(HttpServletRequest request, @PathVariable("productIds") String productIds) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.deleteProductFromCart(user.getId(), productIds);
    }

    /**
     *  获取整个购物车列表
     */
    @GetMapping
    @Consumer
    public ServerResponse<CartVo> getLists(HttpServletRequest request) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.getLists(user.getId());
    }

    /**
     *  全部勾选
     */
    @PostMapping("selectAll")
    @Consumer
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.selectOrUnSelect(user.getId(), null, ServerConst.Cart.CHECKED);
    }

    /**
     * 全部不勾选
     */
    @PostMapping("unSelectAll")
    @Consumer
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest request) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.selectOrUnSelect(user.getId(),null, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  单选
     */
    @PostMapping("select/{productId}")
    @Consumer
    public ServerResponse<CartVo> SelectProduct(HttpServletRequest request, @PathVariable("productId") Integer productId) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.selectOrUnSelect(user.getId(),productId, ServerConst.Cart.CHECKED);
    }

    /**
     * 单不选
     */
    @PostMapping("unSelect/{productId}")
    @Consumer
    public ServerResponse<CartVo> unSelectProduct(HttpServletRequest request, @PathVariable("productId") Integer productId) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.selectOrUnSelect(user.getId(),productId, ServerConst.Cart.UNCHECKED);
    }

    /**
     *  获取购物车中商品的数量
     */
    @GetMapping("totalCount")
    @Consumer
    public ServerResponse<CartVo> getTotalCount(HttpServletRequest request) {
        User user = (User) redisTemplate.opsForValue().get(request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME));
        return cartService.getTotalCount(user.getId());
    }


}
