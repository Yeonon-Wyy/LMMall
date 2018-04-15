package top.yeonon.lmmall.security.validate.imageCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.yeonon.lmmall.properties.CoreProperties;
import top.yeonon.lmmall.security.validate.ValidateCodeGenerator;

import javax.servlet.ServletRequest;
import java.util.UUID;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 12:13
 **/
@Component
public class ImageCodeGenerator implements ValidateCodeGenerator {

    @Autowired
    private CoreProperties coreProperties;

    @Override
    public ImageCode generate(ServletRequest request) {
        String uuid = UUID.randomUUID().toString();
        String code = uuid.substring(0, coreProperties.getValidateCode().getImage().getLength());
        return new ImageCode(code, coreProperties.getValidateCode().getImage().getExpireIn(),
                coreProperties.getValidateCode().getImage().getLength());
    }
}
