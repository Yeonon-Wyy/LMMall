package top.yeonon.service;

import com.github.pagehelper.PageInfo;
import top.yeonon.common.ServerResponse;
import top.yeonon.entity.Shipping;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 15:13
 **/
public interface IShippingService {

    ServerResponse addShipping(Integer userId, Shipping shipping);

    ServerResponse deleteShipping(Integer userId, Integer shippingId);

    ServerResponse updateShipping(Integer userId, Shipping shipping);

    ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> getShippingList(Integer userId, Integer pageNum, Integer pageSize);
}
