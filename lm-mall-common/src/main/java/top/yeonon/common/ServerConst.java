package top.yeonon.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 20:49
 **/
public interface ServerConst {


    String FORGET_PASSWORD_TOKEN_KEY = "FORGET_PASSWORD_TOKEN_KEY:";

    /**
     * 支付宝默认回调地址
     */
    String DEFAULT_ALIPAY_CALLBACK_ADDRESS = "http://www.lmmall.com/order/alipay_callback";

    interface Token {
        /**
         * 登录TOKEN的名称
         */
        String LMMALL_LOGIN_TOKEN_NAME = "login-token";

        String LMMALL_REFRESH_TOKEN_NAME = "refresh-token";

        /**
         * 放入token payload 的信息
         */
        String TOKEN_PAYLOAD_NAME = "authorization";

        String REFRESH_TOKEN_PAYLOAD_NAME = "refresh_token_";
    }

    interface ValidateCode {
        String VALIDATE_CODE_SUFFIX = "_validateCode";

        String VALIDATE_CODE_PARAM_NAME = "validateCode";
    }

    interface ProductOrderBy {
        Set<String> ORDER_BY_PRICE = Sets.newHashSet("price_asc","price_desc");
        Set<String> ORDER_BY_NAME = Sets.newHashSet("name_asc","name_desc");
    }

    interface Cart {
        int CHECKED = 1;
        int UNCHECKED = 0;

        String LIMIT_NUMBER_SUCCESS = "LIMIT_NUMBER_SUCCESS";
        String LIMIT_NUMBER_FAIL = "LIMIT_NUMBER_FAIL";
    }

    interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    enum PayPlatform {
        //支付宝平台
        ALIPY(0, "支付宝");

        private int code;
        private String desc;

        PayPlatform(int code, String desc){
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    enum OrderStatus {
        //已取消状态
        CANCELED(0, "已取消"),
        //未支付状态
        NO_PAY(10, "未支付"),
        //已付款状态
        PAID(20, "已付款"),
        //已发货状态
        SHIPPED(40, "已发货"),
        //订单完成状态
        ORDER_SUCCESS(50, "订单完成"),
        //订单关闭状态
        ORDER_CLOSE(60, "订单关闭");

        private int code;
        private String desc;

        OrderStatus(int code, String desc){
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static OrderStatus codeOf(int code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode() == code) {
                    return orderStatus;
                }
            }
            throw new RuntimeException("没有找到对应的订单状态");
        }
    }

    enum ProductStatus {
        //商品处于上架状态
        ON_SELL(1, "在售状态");

        private Integer code;
        private String desc;
        ProductStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    enum UserFieldValidate {
        //用户的email信息
        EMAIL("email"),
        //用户的phone信息
        PHONE("phone"),
        //用户的username信息
        USERNAME("username");

        private String desc;

        UserFieldValidate(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 用户角色枚举
     */
    enum Role {
        //管理员
        ADMIN(0, "ADMIN"),
        //普通用户
        USER(1, "USER");

        private int code;
        private String desc;

        Role(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    interface RedisLock {
        String CLOSE_ORDER_LOCK_KEY = "CLOSE_ORDER_LOCK_KEY";
    }
}
