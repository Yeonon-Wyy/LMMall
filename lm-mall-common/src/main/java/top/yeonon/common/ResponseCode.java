package top.yeonon.common;

import lombok.Getter;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:45
 **/
@Getter
public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    INVALID_PARAMETER(20, "INVALID_PARAMETER");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
