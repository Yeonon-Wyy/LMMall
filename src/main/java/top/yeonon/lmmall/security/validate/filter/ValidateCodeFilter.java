package top.yeonon.lmmall.security.validate.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.exception.ValidateCodeException;
import top.yeonon.lmmall.security.validate.ValidateCode;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 13:16
 **/
@Component
public class ValidateCodeFilter implements Filter {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (httpServletRequest.getMethod().toUpperCase().equals("POST")) {
            //校验验证码是否合法
            //Post方法才会校验
            validate(servletRequest);
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private void validate(ServletRequest request) {
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
