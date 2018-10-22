package top.yeonon.web.controller.backend;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.serivice.IUserService;
import top.yeonon.web.interceptor.authenticationAnnotation.Manager;
import top.yeonon.entity.User;
import javax.servlet.http.HttpServletRequest;
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
        String userId = getUserId(request);
        if (Integer.valueOf(userId).equals(id)) {
            return ServerResponse.createByErrorMessage("不要自杀！");
        }
        return userService.deleteUser(id);
    }

    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        return JWT.decode(token).getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
    }
}
