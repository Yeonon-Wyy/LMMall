package top.yeonon.lmmall.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.SignatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yeonon.lmmall.oauth.api.GithubApi;
import top.yeonon.lmmall.oauth.api.WeixinApi;
import top.yeonon.lmmall.oauth.services.AbstractOAuthService;
import top.yeonon.lmmall.oauth.services.CustomerOAuthService;
import top.yeonon.lmmall.oauth.services.GithubOAuthService;
import top.yeonon.lmmall.oauth.services.WeixinOAuthService;
import top.yeonon.lmmall.properties.CoreProperties;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 11:51
 **/
@Configuration
public class OAuthConfig {

    private static final String GITHUB_CALLBACK_URL = "%s/oauth/%s/callback";

    private static final String WEIXIN_CALLBACK_URL = "http://www.pinzhi365.com/oauth/%s/callback";

    @Value("${lm.mall.oauth.github.clientId}")
    private String githubClientId;

    @Value("${lm.mall.oauth.github.clientSecret}")
    private String githubClientSecret;

    @Value("${lm.mall.oauth.github.callbackHost}")
    private String githubHost;

    @Value("${lm.mall.oauth.github.state}")
    private String githubState;

    @Value("${lm.mall.oauth.weixin.clientId}")
    private String weixinClientId;

    @Value("${lm.mall.oauth.weixin.clientSecret}")
    private String weixinClientSecret;

    @Value("${lm.mall.oauth.weixin.state}")
    private String weixinClientIdState;

    @Value("${lm.mall.oauth.weixin.callback.host}")
    private String weixinClientIdbHost;

    @Bean
    public GithubApi githubApi() {
        return new GithubApi(githubState);
    }

    @Bean
    public CustomerOAuthService githubOAuthService() {
        return new GithubOAuthService(new ServiceBuilder()
                                        .provider(githubApi())
                                        .apiKey(githubClientId)
                                        .apiSecret(githubClientSecret)
                                        .callback(String.format(GITHUB_CALLBACK_URL, githubHost, OAuthTypes.GITHUB))
                                        .build());
    }

    @Bean
    public CustomerOAuthService weixinOAuthService(){
        return new WeixinOAuthService(new WeixinApi(),new org.scribe.model.OAuthConfig(weixinClientId, weixinClientSecret, String.format(WEIXIN_CALLBACK_URL, OAuthTypes.WEIXIN), SignatureType.Header, "snsapi_login", null));
    }
}
