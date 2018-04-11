package top.yeonon.lmmall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeonon.lmmall.common.ServerResponse;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 13:55
 **/
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public ServerResponse testException() {

        int i = 0;
        int j = 5;
        int res = j / i;
        return ServerResponse.createBySuccess("ok");
    }
}
