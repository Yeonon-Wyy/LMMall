package top.yeonon.common.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 13:03
 **/
@Getter
@Setter
public class OAuthGithubProperties {

    private String clientId;
    private String clientSecret;
    private String callbackHost;
    private String state;
}
