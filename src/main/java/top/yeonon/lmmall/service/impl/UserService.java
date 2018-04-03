package top.yeonon.lmmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.IUserService;
import top.yeonon.lmmall.utils.MD5Utils;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:34
 **/
@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    /**
     *用户注册
     */
    @Override
    public ServerResponse register(User user) {
        ServerResponse serverResponse = checkValid(user);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        user.setRole(ServerConst.Role.USER.getCode());
        user.setPassword(MD5Utils.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userRepository.insert(user);
        if (resultCount <= 0) {
            return ServerResponse.createByErrorMessage("注册用户失败，可能是服务器故障");
        }
        return ServerResponse.createBySuccessMessage("注册用户成功");
    }


    /**
     * 校验提交上来的用户信息是否重复
     */
    private ServerResponse checkValid(User user) {
        int resultCount = userRepository.checkUserField(user.getUsername(),
                ServerConst.UserFieldValidate.USERNAME.getDesc());

        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        resultCount = userRepository.checkUserField(user.getEmail(),
                ServerConst.UserFieldValidate.EMAIL.getDesc());

        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        resultCount = userRepository.checkUserField(user.getPhone(),
                ServerConst.UserFieldValidate.PHONE.getDesc());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("手机号已存在");
        }
        return ServerResponse.createBySuccess();
    }
}
