package top.yeonon.token;


/**
 * @Author yeonon
 * @date 2018/4/11 0011 19:40
 *
 * Token生成器
 **/
public interface TokenGenerator<T, R> {

    /**
     * 生成token
     */
    String generate(T authorization, int expireIn) throws Exception;

    /**
     * 验证token是否合法
     */
    R verifyToken(String token) throws Exception;

}
