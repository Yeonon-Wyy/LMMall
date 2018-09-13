package top.yeonon.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author yeonon
 * @date 2018/4/17 0017 17:51
 **/
@Getter
@Setter
public class TaskProperties {
    private int closeOrderTimeOut = 5000;

    //未付款的订单的超时时间,单位小时
    private int closeOrderTime = 2;
}
