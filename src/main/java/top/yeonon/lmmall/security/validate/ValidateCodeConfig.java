package top.yeonon.lmmall.security.validate;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yeonon.lmmall.security.validate.filter.ValidateCodeFilter;

import javax.servlet.FilterRegistration;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 13:29
 * 验证码总配置类
 **/
@Configuration
public class ValidateCodeConfig {

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    //filter注册器，注册filter
    @Bean
    public FilterRegistrationBean validateCoedFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(validateCodeFilter);

        //设置拦截的URL路径，这里好像不能直接设置POST方法或者GET方法，这对Rest服务不是很友好
        List<String> urls = Lists.newArrayList();
        urls.add("/token/");
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }
}
