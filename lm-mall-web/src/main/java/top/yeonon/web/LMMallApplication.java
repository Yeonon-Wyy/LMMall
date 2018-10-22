package top.yeonon.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author yeonon
 * @date 2018/9/13 0013 20:02
 **/
@SpringBootApplication
@ComponentScan(basePackages = "top.yeonon")
@MapperScan(basePackages = "top.yeonon")
public class LMMallApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(LMMallApplication.class, args);
    }
}
