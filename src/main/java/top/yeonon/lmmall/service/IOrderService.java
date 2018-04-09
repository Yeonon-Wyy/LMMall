package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerResponse;

import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 20:36
 **/
public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallcack(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
