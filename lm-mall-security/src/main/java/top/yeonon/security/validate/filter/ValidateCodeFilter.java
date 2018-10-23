package top.yeonon.security.validate.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.security.validate.ValidateCode;
import top.yeonon.security.validate.ValidateCodeException;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 13:16
 *
 * 验证码的filter，主要是为了校验验证码是否合法
 **/
@Component
public class ValidateCodeFilter implements Filter {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //Rest服务不同的请求方法会共用一个URL，这样会导致filter注册器注册过滤路径的时候造成麻烦
        //例如我要拦截/token/ post请求，我需要在注册器中写明这个URL才能使用过滤器。但是我的GET /token/并不想使用过滤功能。
        //而在注册器中无法配置POST方法还是GET方法，所以选择在这里判断请求方法的类型，来决定是否要校验验证码
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        try {
            if ("POST".equals(httpServletRequest.getMethod().toUpperCase())) {
                //校验验证码是否合法
                //Post方法才会校验
                validate(servletRequest);

            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ValidateCodeException e) {
            //Filter不在Spring MVC stack里，所有要向返回一个人性化的异常信息，只能单独处理，不能依靠@ControlerAdvice注解
            //但是Interceptor是Spring MVC 的，所有完全可以使用全局异常来捕获
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setContentType("application/json;charset=utf8");
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(ServerResponse.createByErrorMessage(e.toString())));
        }



    }

    private void validate(ServletRequest request) throws ValidateCodeException {
        String codeInRequest = request.getParameter(ServerConst.ValidateCode.VALIDATE_CODE_PARAM_NAME);
        ValidateCode codeInServer = (ValidateCode) redisTemplate.opsForValue().get(request.getRemoteAddr() + ServerConst.ValidateCode.VALIDATE_CODE_SUFFIX);
        if (codeInServer == null) {
            throw new ValidateCodeException("验证码不存在");
        }
        if (StringUtils.isEmpty(codeInRequest)) {
            throw new ValidateCodeException("验证码不能为空");
        }
        if (codeInServer.isExpire()) {
            redisTemplate.delete(request.getRemoteAddr() + ServerConst.ValidateCode.VALIDATE_CODE_SUFFIX);
            throw new ValidateCodeException("验证码已经过期");
        }
        if (!codeInServer.getCode().equals(codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }

        redisTemplate.delete(request.getRemoteAddr() + ServerConst.ValidateCode.VALIDATE_CODE_SUFFIX);
    }

    @Override
    public void destroy() {

    }
}
