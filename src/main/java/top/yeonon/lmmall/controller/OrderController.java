package top.yeonon.lmmall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
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
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.service.IOrderService;
import top.yeonon.lmmall.vo.OrderVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
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


    @PostMapping
    @Consumer
    public ServerResponse createOrder(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return orderService.createOrder(user.getId(), shippingId);
    }

    @DeleteMapping("{orderNo}")
    @Consumer
    public ServerResponse deleteOrder(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return orderService.deleteOrder(user.getId(), orderNo);
    }

    @GetMapping("order_cart_product")
    @Consumer
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return orderService.getOrderCartProduct(user.getId());
    }

    @GetMapping
    @Consumer
    public ServerResponse<PageInfo> getOrders(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return orderService.getOrders(user.getId(), pageNum, pageSize);
    }

    @GetMapping("{orderNo}")
    @Consumer
    public ServerResponse<OrderVo> getDetails(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        return orderService.getDetails(user.getId(), orderNo);
    }

    @PostMapping("{orderNo}/pay")
    @Consumer
    public ServerResponse pay(HttpSession session, @PathVariable("orderNo") Long orderNo, HttpServletRequest request) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        String path = request.getServletContext().getRealPath("upload");
        return orderService.pay(user.getId(), orderNo, path);
    }

    @PostMapping("alipay_callback")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map requestParam = request.getParameterMap();
        for (Iterator iter = requestParam.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParam.get(name);
            String valStr = "";
            for (int i = 0; i < values.length; i++) {
                valStr = (i == values.length - 1) ? valStr + values[i] : valStr + values[i] + ",";
            }
            params.put(name, valStr);
        }
        logger.info("支付宝回调, sign{}, trade_status{}, 参数{}", params.get("sign"), params.get("trade_status"), params.toString());
        //验签前得删除参数里的sign和sign_type参数，sign在支付宝SDK里删除里，所以在这里得手动删除sign_type
        params.remove("sign_type");

        try {
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
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
    @Consumer
    public ServerResponse queryOrderPayStatus(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        ServerResponse response = orderService.queryOrderPayStatus(user.getId(), orderNo);
        if (response.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
