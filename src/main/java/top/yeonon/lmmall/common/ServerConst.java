package top.yeonon.lmmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 20:49
 **/
public interface ServerConst {

    String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_";
    /**
     * 保存用户信息的SESSION KEY
     */
    String SESSION_KEY_FOR_CURRENT = SESSION_KEY_PREFIX + "CURRENT_USER";

    String FORGET_PASSWORD_TOKEN_KEY = "FORGET_PASSWORD_TOKEN_KEY:";


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

    enum ProductStatus {
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
        EMAIL("email"),
        PHONE("phone"),
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
        ADMIN(0, "ADMIN"),
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
}
