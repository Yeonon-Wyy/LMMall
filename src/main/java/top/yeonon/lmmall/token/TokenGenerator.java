package top.yeonon.lmmall.token;


/**
 * @Author yeonon
 * @date 2018/4/11 0011 19:40
 **/
public interface TokenGenerator<T> {

    String generate(T authorization) throws Exception;

    boolean verifyToken(String token) throws Exception;

}
