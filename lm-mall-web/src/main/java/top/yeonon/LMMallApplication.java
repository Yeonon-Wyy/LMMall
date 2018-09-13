package top.yeonon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author yeonon
 * @date 2018/9/13 0013 20:02
 **/
@SpringBootApplication
public class LMMallApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(LMMallApplication.class, args);
    }
}
