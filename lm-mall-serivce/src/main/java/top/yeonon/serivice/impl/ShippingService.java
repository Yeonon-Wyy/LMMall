package top.yeonon.serivice.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeonon.common.ResponseCode;
import top.yeonon.common.ServerResponse;
import top.yeonon.entity.Shipping;
import top.yeonon.repository.ShippingRepository;
import top.yeonon.serivice.IShippingService;


import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 15:13
 **/
@Service
public class ShippingService implements IShippingService {

    @Autowired
    private ShippingRepository shippingRepository;


    @Override
    public ServerResponse addShipping(Integer userId, Shipping shipping) {
        if (userId == null || shipping == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }
        //防止攻击者添加userId字段，导致修改其他用户的相关信息，即防止横向越权
        shipping.setUserId(userId);
        int rowCount = shippingRepository.insert(shipping);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("新增收货地址失败");
        }
        return ServerResponse.createBySuccessMessage("新增收货地址成功");

    }

    @Override
    public ServerResponse deleteShipping(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }
        //这里删除是包含userId作为条件的，即在SQL层面防止了横向越权
        int rowCount = shippingRepository.deleteShippingByUserIdAndShippingId(userId, shippingId);

        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("删除收货地址失败");
        }
        return ServerResponse.createBySuccessMessage("删除收货地址成功");
    }

    @Override
    public ServerResponse updateShipping(Integer userId, Shipping shipping) {
        if (userId == null || shipping == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }
        //同上，防止横向越权
        shipping.setUserId(userId);
        int rowCount = shippingRepository.updateByPrimaryKeySelective(shipping);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("更新收货地址失败");
        }
        return ServerResponse.createBySuccessMessage("更新收货地址成功");
    }

    @Override
    public ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }
        Shipping shipping = shippingRepository.selectShippingByIdAndUserId(userId, shippingId);

        if (shipping == null) {
            return ServerResponse.createByErrorMessage("获取收货地址失败");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse<PageInfo> getShippingList(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误");
        }
        List<Shipping> shippingList = shippingRepository.selectShippingListByUserId(userId);

        if (CollectionUtils.isEmpty(shippingList)) {
            return ServerResponse.createByErrorMessage("没有找到该用户的任何收货地址");
        }
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
