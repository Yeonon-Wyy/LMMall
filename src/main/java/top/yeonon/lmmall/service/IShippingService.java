package top.yeonon.lmmall.service;

import com.github.pagehelper.PageInfo;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.Shipping;

import java.util.List;

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
