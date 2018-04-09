package top.yeonon.lmmall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.IOrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/8 0008 19:56
 **/
@RestController
@RequestMapping("/order")
public class OrderController {


    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    @PostMapping("{orderNo}/pay")
    public ServerResponse pay(HttpSession session, @PathVariable("orderNo") Long orderNo, HttpServletRequest request) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }
        String path = request.getServletContext().getRealPath("upload");
        return orderService.pay(user.getId(), orderNo, path);
    }

    @GetMapping("alipay_callback")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map requestParam = request.getParameterMap();
        for (Object object : requestParam.keySet()) {
            String name = (String) object;
            String[] values = (String[]) requestParam.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调, sign{}, trade_status{}, 参数{}", params.get("sign"), params.get("trade_status"), params.toString());
        //验签前得删除参数里的sign和sign_type参数，sign在支付宝SDK里删除里，所以在这里得手动删除sign_type
        params.remove("sign_type");

        try {
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckV2) {
                return ServerResponse.createByErrorMessage("非法请求！");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常");
            e.printStackTrace();
        }
        ServerResponse response = orderService.alipayCallcack(params);
        if (response.isSuccess()) {
            return ServerConst.AlipayCallback.RESPONSE_SUCCESS;
        }
        return ServerConst.AlipayCallback.RESPONSE_FAILED;
    }

    @GetMapping("{orderNo}/pay_status")
    public ServerResponse queryOrderPayStatus(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户需要登录！");
        }

        ServerResponse response = orderService.queryOrderPayStatus(user.getId(), orderNo);
        if (response.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
