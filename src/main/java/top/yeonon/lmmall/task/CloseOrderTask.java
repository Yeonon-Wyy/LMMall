package top.yeonon.lmmall.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.yeonon.lmmall.service.IOrderService;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * @Author yeonon
 * @date 2018/4/17 0017 15:06
 **/
@Component
@Log
public class CloseOrderTask {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

//    @Scheduled(cron = "* 0/1 * * * ?")
//    public void closeOrderV1() {
//        log.info("开启定时关单");
//        orderService.closeOrder(2);
//        log.info("定时任务完成");
//    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void closeOrderV2() {
        int timeOut = 50000;
        if (redisTemplate.opsForValue().setIfAbsent("CLOSE_ORDER_LOCK", String.valueOf(System.currentTimeMillis()) + timeOut)) {
            log.info("获取锁成功,开始执行任务");
            closeOrder(2);
        } else {
            log.info("获取锁失败，放弃任务");
        }
    }

    private void closeOrder(int hour) {
        boolean isTrue = redisTemplate.expire("CLOSE_ORDER_LOCK", 50000, TimeUnit.MILLISECONDS);
        //orderService.closeOrder(hour);
        redisTemplate.delete("CLOSE_ORDER_LOCK");
        log.info(Thread.currentThread().getName() + "释放锁");
    }

}
