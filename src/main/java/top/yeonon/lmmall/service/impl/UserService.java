package top.yeonon.lmmall.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yeonon.lmmall.common.ResponseCode;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.repository.UserRepository;
import top.yeonon.lmmall.service.IUserService;
import top.yeonon.lmmall.utils.MD5Utils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:34
 **/
@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     *用户注册
     */
    @Override
    public ServerResponse register(User user) {
        ServerResponse serverResponse = checkValid(user.getUsername(),
                ServerConst.UserFieldValidate.USERNAME.getDesc());
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        serverResponse = checkValid(user.getEmail(),
                ServerConst.UserFieldValidate.EMAIL.getDesc());

        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        serverResponse = checkValid(user.getPhone(),
                ServerConst.UserFieldValidate.PHONE.getDesc());
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("电话号码已存在");
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
     *获取用户密保问题
     */
    @Override
    public ServerResponse<String> getQuestionByUsername(String username) {
        ServerResponse serverResponse = checkValid(username,
                ServerConst.UserFieldValidate.USERNAME.getDesc());
        //成功表示Username不存在
        if (serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String question = userRepository.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("用户的密保问题为空");
        }
        return ServerResponse.createBySuccess(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int rowCount = userRepository.checkAnswer(username, question, answer);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("密保问题答案错误");
        }

        String forgetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(ServerConst.FORGET_PASSWORD_TOKEN_KEY + username,
                forgetToken, 10, TimeUnit.MINUTES);
        return ServerResponse.createBySuccess(forgetToken);
    }

    @Override
    public ServerResponse updatePassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.INVALID_PARAMETER.getCode(),
                    "传入的Token参数为空，请重新请求");
        }

        String token = (String) redisTemplate.opsForValue().get(ServerConst.FORGET_PASSWORD_TOKEN_KEY + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("用户没有申请重置密码或者token已经过期");
        }
        if (!StringUtils.equals(forgetToken, token)) {
            return ServerResponse.createByErrorMessage("Token不正确！");
        }

        String md5Password = MD5Utils.MD5EncodeUtf8(newPassword);
        int rowCount = userRepository.updatePassword(username, md5Password);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("修改密码失败");
        }
        redisTemplate.delete(ServerConst.FORGET_PASSWORD_TOKEN_KEY + username);
        return ServerResponse.createBySuccessMessage("修改密码成功");

    }

    @Override
    public ServerResponse<User> getUserInfo(Integer userId) {
        User user = userRepository.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("不存在该用户");
        }
        user.setPassword("");
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse updateUserInfo(User user) {
        int rowCount = userRepository.checkEmailByUserId(user.getEmail(), user.getId());
        if (rowCount > 0) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        rowCount = userRepository.checkPhoneByUserId(user.getPhone(), user.getId());
        if (rowCount > 0) {
            return ServerResponse.createByErrorMessage("手机号已存在");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());

        //updateByPrimaryKeySelective这个方法是只有在字段不为空的情况下才更新
        //也就是说上面创建了一个新的User对象，但是只设置一部分字段，其他字段为空，所以
        //只会更新上面set的几个字段
        int updateCount = userRepository.updateByPrimaryKeySelective(updateUser);

        if (updateCount <= 0) {
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccessMessage("更新成功");
    }

    @Override
    public ServerResponse deleteUser(Integer id) {
        int rowCount = userRepository.deleteByPrimaryKey(id);
        if (rowCount <= 0) {
            return ServerResponse.createByErrorMessage("删除失败，可能是没有这个用户");
        }
        return ServerResponse.createBySuccessMessage("删除用户成功！");
    }

    @Override
    public ServerResponse<List<User>> getUserList() {
        List<User> userList = userRepository.selectUserList();
        if (CollectionUtils.isEmpty(userList)) {
            return ServerResponse.createByErrorMessage("当前系统中没有用户");
        }
        return ServerResponse.createBySuccess(userList);
    }

    /**
     * 校验提交上来的用户信息是否重复
     */
    private ServerResponse checkValid(String data, String type) {
        int resultCount = userRepository.checkUserField(data,
                type);
        if (resultCount > 0) {
            return ServerResponse.createByError();
        }
        return ServerResponse.createBySuccess();
    }


    //backend


    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().equals(ServerConst.Role.ADMIN.getCode())) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
