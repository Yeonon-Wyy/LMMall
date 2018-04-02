package top.yeonon.lmmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.IUserService;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:34
 **/
@Component
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User getUserById(Integer id) {
        return userRepository.selectByPrimaryKey(id);
    }
}
