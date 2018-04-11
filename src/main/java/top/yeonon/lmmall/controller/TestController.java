package top.yeonon.lmmall.controller;

import lombok.extern.java.Log;
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
@Log
public class TestController {

    @GetMapping
    public ServerResponse testException() {
        log.info("集群1");
        int i = 0;
        int j = 5;
        int res = j / i;
        return ServerResponse.createBySuccess("ok");
    }
}
