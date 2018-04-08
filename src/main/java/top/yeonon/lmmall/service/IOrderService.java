package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerResponse;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 20:36
 **/
public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);
}
