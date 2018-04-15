package top.yeonon.lmmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.security.validate.imageCode.ImageCode;

import top.yeonon.lmmall.security.validate.ValidateCodeGenerator;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yeonon
 * @date 2018/4/15 0015 13:07
 **/
@RestController
@RequestMapping("validateCode")
public class ValidateCodeController {


    @Autowired
    private ValidateCodeGenerator imageCodeGenerator;


    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("image")
    public ServerResponse getImageCode(HttpServletRequest request) {
        ImageCode imageCode = (ImageCode) imageCodeGenerator.generate(request);
        redisTemplate.opsForValue().set(request.getRemoteAddr() + ServerConst.ValidateCode.VALIDATE_CODE_SUFFIX, imageCode);
        return ServerResponse.createBySuccess(imageCode.getCode());
    }


}
