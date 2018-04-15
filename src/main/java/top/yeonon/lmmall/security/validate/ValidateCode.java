package top.yeonon.lmmall.security.validate;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 12:06
 *
 * 验证码的基类，无论是哪种实现，都至少需要code和expireTime两个字段
 * 为了防止太多重复，就抽象出来一个基类
 **/
@Getter
@Setter
public class ValidateCode implements Serializable {

    String code;
    LocalDateTime expireTime;

    public ValidateCode(String code, LocalDateTime expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    //设置过期时间，单位是秒
    public ValidateCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    //判断是否过期
    public boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
