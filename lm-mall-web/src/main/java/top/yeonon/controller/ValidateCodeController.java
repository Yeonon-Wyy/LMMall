package top.yeonon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.validate.ValidateCodeGenerator;
import top.yeonon.validate.imageCode.ImageCode;


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
