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
 **/
@Configuration
public class ValidateCodeConfig {

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Bean
    public FilterRegistrationBean validateCoedFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(validateCodeFilter);

        List<String> urls = Lists.newArrayList();
        urls.add("/token/");
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }
}
