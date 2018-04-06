package top.yeonon.lmmall.properties;

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
}
