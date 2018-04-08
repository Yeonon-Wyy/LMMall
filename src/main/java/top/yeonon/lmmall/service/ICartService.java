package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.vo.CartVo;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 18:28
 **/
public interface ICartService {

    ServerResponse<CartVo> addProductToCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> updateProductToCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProductFromCart(Integer userId, String productIds);

    ServerResponse<CartVo> getLists(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<CartVo> getTotalCount(Integer userId);
}
