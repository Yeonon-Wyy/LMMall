package top.yeonon.lmmall.oauth;

import org.scribe.builder.ServiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yeonon.lmmall.oauth.api.GithubApi;
import top.yeonon.lmmall.oauth.services.AbstractOAuthService;
import top.yeonon.lmmall.oauth.services.GithubOAuthService;
import top.yeonon.lmmall.properties.CoreProperties;

/**
 * @Author yeonon
 * @date 2018/4/19 0019 11:51
 **/
@Configuration
public class OAuthConfig {

    private static final String CALLBACK_URL = "%s/oauth/%s/callback";


    @Value("${lm.mall.oauth.github.clientId}")
    private String clientId;

    @Value("${lm.mall.oauth.github.clientSecret}")
    private String clientSecret;

    @Value("${lm.mall.oauth.github.callbackHost}")
    private String host;

    @Value("${lm.mall.oauth.github.state}")
    private String state;

    @Bean
    public GithubApi githubApi() {
        return new GithubApi(state);
    }

    @Bean
    public AbstractOAuthService abstractOAuthService() {
        return new GithubOAuthService(new ServiceBuilder()
                                        .provider(githubApi())
                                        .apiKey(clientId)
                                        .apiSecret(clientSecret)
                                        .callback(String.format(CALLBACK_URL, host, OAuthTypes.GITHUB))
                                        .build());
    }
}
