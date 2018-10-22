package top.yeonon.serivice.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeonon.common.ResponseCode;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.common.properties.CoreProperties;
import top.yeonon.common.util.BigDecimalUtil;
import top.yeonon.common.util.FTPUtil;
import top.yeonon.entity.*;

import top.yeonon.repository.*;
import top.yeonon.serivice.IOrderService;
import top.yeonon.serivice.vo.OrderItemVo;
import top.yeonon.serivice.vo.OrderProductVo;
import top.yeonon.serivice.vo.OrderVo;
import top.yeonon.serivice.vo.ShippingVo;


import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * @Author yeonon
 * @date 2018/4/8 0008 20:36
 **/
@Service
public class OrderService implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private CoreProperties coreProperties;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PayInfoRepository payInfoRepository;

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartRepository.selectCheckedCartsByUserId(userId);

        //计算订单总价
        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = assembleOrder(payment, userId, shippingId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单失败");
        }

        //设置OrderItem的订单号
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //批量插入
        orderItemRepository.batchInsert(orderItemList);

        //减少库存
        reduceProductStock(orderItemList);

        //清空购物车
        clearCart(cartList);

        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setImageHost(coreProperties.getFtp().getHostPrefix());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(ServerConst.PayPlatform.ALIPY.getDesc());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(ServerConst.OrderStatus.codeOf(order.getStatus()).getDesc());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingRepository.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setSendTime(order.getSendTime());
        orderVo.setEndTime(order.getEndTime());
        orderVo.setCloseTime(order.getCloseTime());
        orderVo.setCreateTime(order.getCreateTime());

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem: orderItemList) {
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }

        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;

    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setCreateTime(orderItem.getCreateTime());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        return shippingVo;
    }

    private void clearCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartRepository.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productRepository.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productRepository.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(BigDecimal payment, Integer userId, Integer shippingId) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(ServerConst.OrderStatus.NO_PAY.getCode());
        order.setPaymentType(ServerConst.PayPlatform.ALIPY.getCode());
        order.setPostage(0);

        int rowCount = orderRepository.insert(order);
        if (rowCount <= 0) {
            return null;
        }
        return order;
    }

    private Long generateOrderNo() {
        Long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal totalPrice = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            totalPrice = BigDecimalUtil.add(orderItem.getCurrentUnitPrice().doubleValue(), totalPrice.doubleValue());
        }
        return totalPrice;
    }


    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();

        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productRepository.selectByPrimaryKey(cart.getProductId());
            if (!product.getStatus().equals(ServerConst.ProductStatus.ON_SELL.getCode())) {
                return ServerResponse.createByErrorMessage("商品" + product.getName() + "不是在售状态");
            }
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));

            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    @Override
    public ServerResponse pay(Integer userId, Long orderNo, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderRepository.selectOrderByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", order.getOrderNo().toString());

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("路漫商城 扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单")
                         .append(outTradeNo).append("购买商品共：")
                         .append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = Lists.newArrayList();

        List<OrderItem> orderItemList = orderItemRepository.selectOrderItemsByUserIdAndOrderNo(userId, orderNo);

        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(coreProperties.getAlipay().getCallbackAddress())//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");

        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File fileDir = new File(path);
                if (!fileDir.exists()) {
                    fileDir.setWritable(true);
                    fileDir.mkdir();
                }


                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path, qrFileName);

                FTPUtil.uploadFiles(Lists.newArrayList(targetFile));
                String qrUrl = coreProperties.getFtp().getHostPrefix() + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    @Override
    public ServerResponse alipayCallcack(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        Order order = orderRepository.selectOrderByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("不是路漫商城的订单！");
        }
        if (order.getStatus() >= ServerConst.OrderStatus.PAID.getCode()) {
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }

        if (ServerConst.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            try {
                order.setPaymentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.get("gmt_payment")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            order.setStatus(ServerConst.OrderStatus.PAID.getCode());
            orderRepository.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(ServerConst.PayPlatform.ALIPY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoRepository.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse deleteOrder(Integer userId, Long orderNo) {
        Order order = orderRepository.selectOrderByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有订单");
        }
        if (order.getStatus() > ServerConst.OrderStatus.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("已付款的订单无法取消");
        }
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setUserId(order.getUserId());
        newOrder.setStatus(ServerConst.OrderStatus.CANCELED.getCode());
        newOrder.setCloseTime(new Date());

        int rowCount = orderRepository.updateByPrimaryKeySelective(newOrder);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("删除订单失败");
        }
        return ServerResponse.createBySuccessMessage("删除订单成功");
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        List<Cart> cartList = cartRepository.selectCheckedCartsByUserId(userId);

        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal payment = new BigDecimal("0");
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        OrderProductVo orderProductVo = new OrderProductVo();
        orderProductVo.setImageHost(coreProperties.getFtp().getHostPrefix());
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setTotalPrice(payment);
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<PageInfo> getOrders(Integer userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "参数错误。");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderRepository.selectOrdersByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(userId, orderList);
        PageInfo pageInfo =new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<OrderVo> assembleOrderVoList(Integer userId, List<Order> orderList) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList;
            if (userId != null) {
                orderItemList = orderItemRepository.selectOrderItemsByUserIdAndOrderNo(userId, order.getOrderNo());
            } else {
                orderItemList = orderItemRepository.selectOrderItemsByOrderNo(order.getOrderNo());
            }

            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderRepository.selectOrderByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("不存在该订单");
        }
        if (order.getStatus() >= ServerConst.OrderStatus.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<OrderVo> getDetails(Integer userId, Long orderNo) {
        Order order = orderRepository.selectOrderByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("不存在该订单");
        }
        List<OrderItem> orderItemList = orderItemRepository.selectOrderItemsByUserIdAndOrderNo(userId, orderNo);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);

    }

    //backend

    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<PageInfo> getManageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderRepository.selectAllOrders();
        List<OrderVo> orderVoList = assembleOrderVoList(null, orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<OrderVo> getManageDetails(Long orderNo) {
        Order order = orderRepository.selectOrderByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemRepository.selectOrderItemsByOrderNo(orderNo);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * TODO 订单搜索暂时是精确搜索，以后会使用比较高级的技术来解决搜索问题，先做一个接口方便扩展
     */
    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderRepository.selectOrderByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemRepository.selectOrderItemsByOrderNo(orderNo);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        PageInfo pageInfo = new PageInfo(Lists.newArrayList(orderVo));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 发货
     */
    @Override
    public ServerResponse manageSendGoods(Long orderNo) {
        Order order = orderRepository.selectOrderByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if (order.getStatus() == ServerConst.OrderStatus.PAID.getCode()) {
            order.setStatus(ServerConst.OrderStatus.SHIPPED.getCode());
            order.setSendTime(new Date());
            int rowCount = orderRepository.updateByPrimaryKeySelective(order);
            if (rowCount <= 0) {
                return ServerResponse.createByErrorMessage("发货失败");
            }
            return ServerResponse.createBySuccessMessage("发货成功");
        }
        return ServerResponse.createByErrorMessage("用户未付款或者该订单已经关闭，已发货等!");
    }



    @Override
    public void closeOrder(int hour) {
        Date orderDate = DateUtils.addHours(new Date(), -hour);
        List<Order> orderList = orderRepository.selectOrdersStatusByCreateTime(ServerConst.OrderStatus.PAID.getCode(),
                DateFormatUtils.format(orderDate, "yyyy-MM-dd HH:mm:ss"));

        for (Order order : orderList) {
            List<OrderItem> orderItemList = orderItemRepository.selectOrderItemsByOrderNo(order.getOrderNo());

            for (OrderItem orderItem : orderItemList) {
                Integer stock = productRepository.selectStockByProductId(orderItem.getProductId());

                //说明该订单项中的商品已经被“删除”了
                if (stock == null) {
                    continue;
                }

                Product product = new Product();
                product.setId(orderItem.getId());
                product.setStock(orderItem.getQuantity() + stock);
                productRepository.updateByPrimaryKeySelective(product);
            }
            orderRepository.closeOrderByOrderId(order.getId());
            log.info("关闭订单号 : " + order.getOrderNo());
        }
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s ", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
