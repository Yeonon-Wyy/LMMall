package top.yeonon.security.validate.imageCode;

import lombok.Getter;
import lombok.Setter;
import top.yeonon.security.validate.ValidateCode;


/**
 * @Author yeonon
 * @date 2018/4/15 0015 12:03
 * 图形验证码的实现类，比基类多了一个length长度字段
 **/
@Getter
@Setter
public class ImageCode extends ValidateCode {

    private int length;

    public ImageCode(String code, int expireIn, int length) {
        super(code, expireIn);
        this.length = length;
    }

}
