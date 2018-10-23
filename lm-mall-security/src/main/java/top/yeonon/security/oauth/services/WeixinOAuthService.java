package top.yeonon.security.oauth.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.*;
import org.scribe.oauth.OAuth20ServiceImpl;
import top.yeonon.security.oauth.OAuthTypes;
import top.yeonon.entity.OAuthUser;


/**
 * @Author yeonon
 * @date 2018/4/19 0019 15:19
 **/
public class WeixinOAuthService extends OAuth20ServiceImpl implements CustomerOAuthService {

    private final DefaultApi20 api;
    private final OAuthConfig oAuthConfig;
    private final String authorizationUrl;

    public WeixinOAuthService(DefaultApi20 api, OAuthConfig config) {
        super(api, config);
        this.api = api;
        this.oAuthConfig = config;
        this.authorizationUrl = getAuthorizationUrl(null);
    }

    @Override
    public Token getAccessToken(Token requestToken, Verifier verifier) {
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        request.addQuerystringParameter("appid", oAuthConfig.getApiKey());
        request.addQuerystringParameter("secret", oAuthConfig.getApiSecret());
        request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
        if (oAuthConfig.hasScope()) {
            request.addQuerystringParameter(OAuthConstants.SCOPE, oAuthConfig.getScope());
        }
        Response response = request.send();
        Object result = JSON.parse(response.getBody());
        return new Token(JSONPath.eval(result, "$.access_token").toString(),"", response.getBody());
    }

    @Override
    public OAuthUser getOAuthUser(Token accessToken) {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setOauthType(getoAuthType());
        Object result = JSON.parse(accessToken.getRawResponse());
        oAuthUser.setOauthId(JSONPath.eval(result, "$.openid").toString());
        return oAuthUser;
    }



    @Override
    public String getoAuthType() {
        return OAuthTypes.WEIXIN;
    }

    @Override
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }


}
