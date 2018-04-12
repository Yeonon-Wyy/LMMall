package top.yeonon.lmmall.controller.backend;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.interceptor.authenticationAnnotation.Manager;
import top.yeonon.lmmall.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/4 0004 22:58
 **/
@RestController
@RequestMapping("manage/users/")
public class UserManagerController {

    @Autowired
    private IUserService userService;

    /**
     * 获取用户列表，需要管理员的身份
     * Ps：这里先这样写是为了把业务逻辑先写，后面会抽离出来单独作为一个项目
     */
    @GetMapping
    @Manager
    public ServerResponse<List<User>> getUserList() {
        return userService.getUserList();
    }

    @DeleteMapping("/{id}")
    @Manager
    public ServerResponse deleteUser(HttpServletRequest request, @PathVariable("id") Integer id) {
        Integer userId = getUserId(request);
        if (userId.equals(id)) {
            return ServerResponse.createByErrorMessage("不要自杀！");
        }
        return userService.deleteUser(id);
    }

    private Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.TOKEN_PAYLOAD_NAME).asInt();
    }
}
