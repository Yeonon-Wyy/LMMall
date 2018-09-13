package top.yeonon.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 14:54
 **/
@Getter
@Setter
public class FtpProperties {

    private String hostPrefix = PropertiesConst.FTP_SERVER_HOST_PREFIX;
    private String ip = PropertiesConst.FTP_IP;
    private String username = PropertiesConst.FTP_USERNAME;
    private String password = PropertiesConst.FTP_PASSWORD;
}
