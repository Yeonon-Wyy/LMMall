package top.yeonon.common.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author yeonon
 * @date 2018/4/3 0003 20:18
 **/
@Getter
@Setter
public class SecurityProperties {
    private String md5Salt = PropertiesConst.MD5_SALT;

    private TokenProperties token = new TokenProperties();
}
