package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 18:28
 **/
public interface ICartService {

    ServerResponse addProductToCart(Integer userId, Integer productId, Integer count);

    ServerResponse updateProductToCart(Integer userId, Integer productId, Integer count);

    ServerResponse deleteProductFromCart(Integer userId, String productIds);

    ServerResponse getLists(Integer userId);

    ServerResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse getTotalCount(Integer userId);
}
