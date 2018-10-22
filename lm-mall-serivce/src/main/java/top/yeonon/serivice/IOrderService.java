package top.yeonon.serivice;

import com.github.pagehelper.PageInfo;
import top.yeonon.common.ServerResponse;
import top.yeonon.serivice.vo.OrderVo;


import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 20:36
 **/
public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallcack(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<PageInfo> getOrders(Integer userId, Integer pageNum, Integer pageSize);


    ServerResponse deleteOrder(Integer userId, Long orderNo);

    ServerResponse<OrderVo> getDetails(Integer userId, Long orderNo);

    //backend
    ServerResponse<PageInfo> getManageList(Integer pageNum, Integer pageSize);


    ServerResponse<OrderVo> getManageDetails(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize);

    ServerResponse manageSendGoods(Long orderNo);

    //close order
    void closeOrder(int hour);
}
