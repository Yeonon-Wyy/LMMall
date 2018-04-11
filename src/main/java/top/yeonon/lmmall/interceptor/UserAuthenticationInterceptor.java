package top.yeonon.lmmall.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Manager;
import top.yeonon.lmmall.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 14:34
 **/
@Component
public class UserAuthenticationInterceptor implements HandlerInterceptor {

    private final static String CONSUMER = "CONSUMER";
    private final static String MANAGER = "MANAGER";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        boolean isPass = true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String permissionType = CONSUMER;
        if (method.getAnnotation(Consumer.class) != null) {
            isPass = isConsumer(session);
            permissionType = CONSUMER;
        }
        else if (method.getAnnotation(Manager.class) != null) {
            isPass = isManager(session);
            permissionType = MANAGER;
        }

        if (!isPass) {
            ServerResponse serverResponse = null;
            if (permissionType.equals(CONSUMER)) {
                serverResponse = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要登录");
            }
            else {
                serverResponse = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "管理员需要登录");
            }
            response.setContentType("application/json");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(serverResponse));
        }


        return isPass;
    }

    private boolean isManager(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return false;
        }
        return true;
    }

    private boolean isConsumer(HttpSession session) {
        User user = (User) session.getAttribute(ServerConst.SESSION_KEY_FOR_CURRENT);
        if (user == null) {
            return false;
        }
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return false;
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
