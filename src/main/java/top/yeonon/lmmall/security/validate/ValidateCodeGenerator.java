package top.yeonon.lmmall.security.validate;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 12:06
 **/
public interface ValidateCodeGenerator extends Serializable {

    ValidateCode generate(ServletRequest request);
}
