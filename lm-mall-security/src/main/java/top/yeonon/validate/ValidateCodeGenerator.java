package top.yeonon.validate;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 12:06
 * 验证码生成器即可，不同类型的验证码，生成逻辑不同
 **/
public interface ValidateCodeGenerator extends Serializable {

    ValidateCode generate(ServletRequest request);
}
