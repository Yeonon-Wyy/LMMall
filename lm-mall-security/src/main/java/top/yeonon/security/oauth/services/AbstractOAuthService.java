package top.yeonon.security.oauth.services;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import top.yeonon.entity.OAuthUser;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 12:03
 **/
public abstract class AbstractOAuthService implements CustomerOAuthService {


    private OAuthService oAuthService;
    private String oAuthType;
    private String authorizeUrl;

    public AbstractOAuthService(OAuthService oAuthService, String type) {
        super();
        this.oAuthService = oAuthService;
        this.oAuthType = type;
        this.authorizeUrl = oAuthService.getAuthorizationUrl(null);
    }

    @Override
    public Token getRequestToken() {
        return oAuthService.getRequestToken();
    }

    @Override
    public Token getAccessToken(Token requestToken, Verifier verifier) {
        return oAuthService.getAccessToken(requestToken, verifier);
    }

    @Override
    public void signRequest(Token accessToken, OAuthRequest request) {
        oAuthService.signRequest(accessToken, request);
    }

    @Override
    public String getVersion() {
        return oAuthService.getVersion();
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return oAuthService.getAuthorizationUrl(requestToken);
    }

    @Override
    public String getAuthorizationUrl() {
        return this.authorizeUrl;
    }

    public String getoAuthType() {
        return this.oAuthType;
    }

    public abstract OAuthUser getOAuthUser(Token accessToken);


}
