package top.yeonon.oauth.services;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import top.yeonon.entity.OAuthUser;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 15:09
 **/
public interface CustomerOAuthService extends OAuthService {
    String getoAuthType();

    String getAuthorizationUrl();

    OAuthUser getOAuthUser(Token accessToken);
}
