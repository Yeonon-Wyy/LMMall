package top.yeonon.lmmall.properties;

import lombok.Getter;
import lombok.Setter;
import top.yeonon.lmmall.common.ServerConst;

/**
 * @Author yeonon
 * @date 2018/4/9 0009 18:38
 **/
@Getter
@Setter
public class AlipayProperties {

    private String callbackAddress = ServerConst.DEFAULT_ALIPAY_CALLBACK_ADDRESS;
}
