package top.yeonon.lmmall.security.validate.imageCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.yeonon.lmmall.security.validate.ValidateCode;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 12:03
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
