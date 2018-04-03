package top.yeonon.lmmall.utils.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author yeonon
 * @date 2018/4/3 0003 20:17
 **/
@ConfigurationProperties(prefix = "lm.mall")
@Getter
@Setter
public class CoreProperties {
    private SecurityProperties security = new SecurityProperties();
}
