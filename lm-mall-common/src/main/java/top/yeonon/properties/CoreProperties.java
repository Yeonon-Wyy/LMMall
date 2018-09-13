package top.yeonon.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yeonon
 * @date 2018/4/3 0003 20:17
 **/
@ConfigurationProperties(prefix = "lm.mall")
@Configuration
@Getter
@Setter
public class CoreProperties {
    private SecurityProperties security = new SecurityProperties();

    private FtpProperties ftp = new FtpProperties();

    private AlipayProperties alipay = new AlipayProperties();

    private ValidateCodeProperties validateCode = new ValidateCodeProperties();

    private TaskProperties task = new TaskProperties();

    private OAuthProperties oauth = new OAuthProperties();
}
