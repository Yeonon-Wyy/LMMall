package top.yeonon.lmmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Cart;
import top.yeonon.lmmall.entity.Product;
import top.yeonon.lmmall.properties.CoreProperties;
import top.yeonon.lmmall.repository.CartRepository;
import top.yeonon.lmmall.repository.ProductRepository;
import top.yeonon.lmmall.service.ICartService;
import top.yeonon.lmmall.utils.BigDecimalUtil;
import top.yeonon.lmmall.vo.CartProductVo;
import top.yeonon.lmmall.vo.CartVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 18:28
 **/
@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CoreProperties coreProperties;

    @Override
    @CachePut(value = "CartCache", key = "#root.caches[0].name + ':' + #userId")
    public ServerResponse<CartVo> addProductToCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }

        Cart cart = cartRepository.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(count);
            cartItem.setChecked(ServerConst.Cart.CHECKED);
            cartRepository.insert(cartItem);
        } else {
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartRepository.updateByPrimaryKeySelective(cart);
        }
        return getCartVoLimit(userId);
    }

    @Override
    @CachePut(value = "CartCache", key = "#root.caches[0].name + ':' + #userId")
    public ServerResponse<CartVo> updateProductToCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }
        Cart cart = cartRepository.selectCartByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartRepository.updateByPrimaryKeySelective(cart);
        return getCartVoLimit(userId);
    }

    @Override
    @CacheEvict(value = "CartCache", key = "#root.caches[0].name + ':' + #userId")
    public ServerResponse<CartVo> deleteProductFromCart(Integer userId, String productIds) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }

        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        cartRepository.deleteProductFromCart(userId, productIdList);
        return getCartVoLimit(userId);
    }

    @Override
    @Cacheable(value = "CartCache")
    public ServerResponse<CartVo> getLists(Integer userId) {
        return getCartVoLimit(userId);
    }

    @Override
    @CachePut(value = "CartCache", key = "#root.caches[0].name + ':' + #userId")
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }

        cartRepository.checkOrUnCheckedCart(userId, productId, checked);
        return getCartVoLimit(userId);
    }

    @Override
    public ServerResponse getTotalCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        int totalCount = cartRepository.selectTotalCount(userId);
        return ServerResponse.createBySuccess(totalCount);
    }

    //购物车的各种操作，增加，删除，更新都依赖这个方法
    /**
     * 需要特殊说明的是，这里的Cart对象并不是用户整个购物车的概念，而是在购物车中的所有商品的对象，CartVo才是整个购物车全貌
     * 举个例子：假设现在用户yeonon向购物车添加了三个商品，分别是IPhone X，Apple Watch，IPad Air 2。
     * 在这里Cart对象就有三个，分别对应上述三个商品，在数据库中保存了productId,userId, quantity等字段。
     * 所以，在本项目中Cart就可以理解为是表示商品和用户的关系的类，同样，在数据库中，cart表也仅仅包含了表示关系的简单字段
     * 最后，CartVo对象就包含这三个商品的List，并且包含了商品的详细信息，这个对象是通过构造得到的，专门返回给前端展示的，真实在数据库中的数据并没有那么丰富。
     */
    private ServerResponse<CartVo> getCartVoLimit(Integer userId) {
        //CartVo 是最终要返回的对象
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartRepository.selectCartsByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            //遍历该用户的所有购物车相关的项（即同一个用户的购物车）
            for (Cart cart : cartList) {
                //对CartProductVo 进行字段设置
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());
                cartProductVo.setId(cart.getId());

                //获取该购物车中商品的对象
                Product product = productRepository.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    //设置一些字段
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    //如果库存充足，则设置标记LIMIT_NUMBER_SUCCESS
                    if (product.getStock() >= cart.getQuantity()) {
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(ServerConst.Cart.LIMIT_NUMBER_SUCCESS);
                    } else {
                        //否则，设置标记LIMIT_NUMBER_FAIL，并且要更新购物车中商品的数量为最大库存值
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(ServerConst.Cart.LIMIT_NUMBER_FAIL);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartRepository.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    //这里buyLimitCount在上面的逻辑中一定会被设置为用户购买的数量，所以直接设置即可
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算该购物车项中的总价，直接单价 X 数量即可，方便整个购物车总价的累加，
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    //设置该购物车项中该商品的选中状态
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                //如果该购物车项中的商品是被选中的，则将整个购物车的总价累加
                if (cart.getChecked() == ServerConst.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                //每遍历一个购物车项都要把构造出来的cartProductVo对象添加到cartProductVoList中
                cartProductVoList.add(cartProductVo);
            }
        }
        //将上面遍历得到的cartProductVoList添加到CartVo中
        cartVo.setCartProductVoList(cartProductVoList);
        //设置总价，这里的总价仅包含被选中（准确的说是被用户选择）的商品的价格总和
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(coreProperties.getFtp().getHostPrefix());
        //单独设置一个是否被全选的字段，方便前端判断
        cartVo.setAllChecked(getAllChecked(userId));
        return ServerResponse.createBySuccess(cartVo);
    }

    //判断整个购物车中的所有商品是否都被选中
    private boolean getAllChecked(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartRepository.selectCartProductCheckedByUserId(userId) <= 0;
    }
}
