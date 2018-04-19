package top.yeonon.lmmall.oauth.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import edu.princeton.cs.algs4.In;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import top.yeonon.lmmall.entity.OAuthUser;
import top.yeonon.lmmall.oauth.OAuthTypes;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 12:20
 **/
public class GithubOAuthService extends AbstractOAuthService {

    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    public GithubOAuthService(OAuthService oAuthService) {
        super(oAuthService, OAuthTypes.GITHUB);
    }

    @Override
    public OAuthUser getOAuthUser(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.GET,PROTECTED_RESOURCE_URL);
        this.signRequest(accessToken, request);
        Response response = request.send();
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setOauthType(getoAuthType());
        Object result = JSON.parse(response.getBody());
        oAuthUser.setOauthId(Integer.valueOf(JSONPath.eval(result, "$.id").toString()));
        return oAuthUser;
    }
}
