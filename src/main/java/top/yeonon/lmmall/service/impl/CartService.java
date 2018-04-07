package top.yeonon.lmmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ServerResponse addProductToCart(Integer userId, Integer productId, Integer count) {
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
    public ServerResponse updateProductToCart(Integer userId, Integer productId, Integer count) {
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
    public ServerResponse deleteProductFromCart(Integer userId, String productIds) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }

        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        cartRepository.deleteProductFromCart(userId, productIdList);
        return getCartVoLimit(userId);
    }

    @Override
    public ServerResponse getLists(Integer userId) {
        return getCartVoLimit(userId);
    }

    @Override
    public ServerResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
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
    private ServerResponse getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartRepository.selectCartsByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());
                cartProductVo.setId(cart.getId());

                Product product = productRepository.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(ServerConst.Cart.LIMIT_NUMBER_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(ServerConst.Cart.LIMIT_NUMBER_FAIL);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartRepository.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if (cart.getChecked() == ServerConst.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(coreProperties.getFtp().getHostPrefix());
        cartVo.setAllChecked(getAllChecked(userId));
        return ServerResponse.createBySuccess(cartVo);
    }

    private boolean getAllChecked(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartRepository.selectCartProductCheckedByUserId(userId) <= 0;
    }
}
