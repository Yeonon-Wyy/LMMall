package top.yeonon.lmmall.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 13:03
 **/
@Getter
@Setter
public class OAuthProperties {
    private OAuthGithubProperties github = new OAuthGithubProperties();
}
