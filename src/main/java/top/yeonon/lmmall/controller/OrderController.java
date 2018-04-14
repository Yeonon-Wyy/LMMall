package top.yeonon.lmmall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.auth0.jwt.JWT;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
 *
 * TODO 关于订单模块是否要缓存，我认为需要上线之后根据用户情况做判断，比较缓存是在使用内存，内存也是宝贵的资源
 **/
@RestController
@RequestMapping("/order")
public class OrderController {


    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    @PostMapping
    @Consumer
    public ServerResponse createOrder(HttpServletRequest request, Integer shippingId) {
        String userId = getUserId(request);
        return orderService.createOrder(Integer.valueOf(userId), shippingId);
    }

    @DeleteMapping("{orderNo}")
    @Consumer
    public ServerResponse deleteOrder(HttpServletRequest request, @PathVariable("orderNo") Long orderNo) {
        String userId = getUserId(request);
        return orderService.deleteOrder(Integer.valueOf(userId), orderNo);
    }

    @GetMapping("order_cart_product")
    @Consumer
    public ServerResponse getOrderCartProduct(HttpServletRequest request) {
        String userId = getUserId(request);
        return orderService.getOrderCartProduct(Integer.valueOf(userId));
    }

    @GetMapping
    @Consumer
    public ServerResponse<PageInfo> getOrders(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String userId = getUserId(request);
        return orderService.getOrders(Integer.valueOf(userId), pageNum, pageSize);
    }

    @GetMapping("{orderNo}")
    @Consumer
    public ServerResponse<OrderVo> getDetails(HttpServletRequest request, @PathVariable("orderNo") Long orderNo) {
        String userId = getUserId(request);
        return orderService.getDetails(Integer.valueOf(userId) ,orderNo);
    }

    @PostMapping("{orderNo}/pay")
    @Consumer
    public ServerResponse pay(HttpServletRequest request, @PathVariable("orderNo") Long orderNo) {
        String userId = getUserId(request);
        String path = request.getServletContext().getRealPath("upload");
        return orderService.pay(Integer.valueOf(userId), orderNo, path);
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
    public ServerResponse queryOrderPayStatus(HttpServletRequest request, @PathVariable("orderNo") Long orderNo) {
        String userId = getUserId(request);
        ServerResponse response = orderService.queryOrderPayStatus(Integer.valueOf(userId), orderNo);
        if (response.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
    }
}
