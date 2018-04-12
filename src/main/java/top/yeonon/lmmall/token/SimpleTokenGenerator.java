package top.yeonon.lmmall.token;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 19:42
 **/
@Component("simpleTokenGenerator")
public class SimpleTokenGenerator implements TokenGenerator<String, Boolean> {

    @Override
    public String generate(String authorization, int expireIn) {
        return UUID.randomUUID().toString() + ":" + authorization;
    }

    @Override
    public Boolean verifyToken(String token) throws Exception {
        //最简单的UUID不需要解密，也就不需要验证什么了
        return true;
    }
}
