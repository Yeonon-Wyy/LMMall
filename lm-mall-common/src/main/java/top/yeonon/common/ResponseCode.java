package top.yeonon.common;

import lombok.Getter;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:45
 **/
@Getter
public enum ResponseCode {

    //成功
    SUCCESS(0, "SUCCESS"),
    //有错误
    ERROR(1, "ERROR"),
    //需要登录
    NEED_LOGIN(10, "NEED_LOGIN"),
    //非法参数
    INVALID_PARAMETER(20, "INVALID_PARAMETER");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
