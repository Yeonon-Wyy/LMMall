package top.yeonon.lmmall.token;


/**
 * @Author yeonon
 * @date 2018/4/11 0011 19:40
 *
 * Token生成器
 **/
public interface TokenGenerator<T> {

    /**
     * 生成token
     */
    String generate(T authorization) throws Exception;

    /**
     * 验证token是否合法
     */
    boolean verifyToken(String token) throws Exception;

}
