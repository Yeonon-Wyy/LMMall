package top.yeonon.lmmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.IUserService;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:36
 **/
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ServerResponse register(User user) {
        return userService.register(user);
    }
}
