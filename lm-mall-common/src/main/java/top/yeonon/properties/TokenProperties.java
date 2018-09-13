package top.yeonon.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author yeonon
 * @date 2018/4/12 0012 22:13
 **/
@Getter
@Setter
public class TokenProperties {
    //超时时间默认单位是分钟
    private int accessTokenExpireIn = 30;
    private int refreshTokenExpireIn = 10080; //7天

    private String jwtSecret = "lmmall_secret";
}
