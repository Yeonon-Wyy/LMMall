package top.yeonon.lmmall.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import top.yeonon.lmmall.properties.CoreProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 20:04
 *
 * 基于JWT 实现的token生成和校验
 **/
@Component
public class JwtTokenGenerator implements TokenGenerator<String> {

    private static final CoreProperties coreProperties = new CoreProperties();

    private static final String SECRET = coreProperties.getSecurity().getJwtSecret();

    @Override
    public String generate(String authorization) throws Exception {
        //token签发时间
        Date iatDate = new Date();
        //设置超时日期（是日期，不是时间）
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, coreProperties.getSecurity().getTokenExpire());
        Date expireTime = nowTime.getTime();

        //这个map是 jwt的头部
        Map<String, Object> map = Maps.newHashMap();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                          .withHeader(map)  //插入头部
                          .withClaim("authorization", authorization)  //插入负载，即数据
                          .withExpiresAt(expireTime)    //超期日期，必须大于签发日期
                          .withIssuedAt(iatDate)        //签发日期
                          .sign(Algorithm.HMAC256(SECRET));  //使用HMAC256算法
        return token;
    }


    @Override
    public boolean verifyToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        //创建校验器
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            //验证token是否合法，合法返回负载（数据），否则抛出异常
            //这里不能用是否为null来判断是否合法，返回值永远是非NULL
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return true;
    }

}
