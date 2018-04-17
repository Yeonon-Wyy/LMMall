package top.yeonon.lmmall.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author yeonon
 * @date 2018/4/17 0017 15:01
 **/
@Component
public class TestTask {

//    @Scheduled(cron = "0/5 * * * * ?")
    public void TimePrint() {
        System.out.println(new Date());
    }
}
