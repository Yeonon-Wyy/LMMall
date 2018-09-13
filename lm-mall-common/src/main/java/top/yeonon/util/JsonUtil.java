package top.yeonon.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;


/**
 * @Author yeonon
 * @date 2018/7/22 0022 16:16
 **/
public class JsonUtil {


    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //所有字段都参与序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //取消将Date字段转换成时间戳
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        //忽略空Bean异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //统一时间格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        //忽略在Json中存在但是在类中不存在的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转换成JSON字符串
     * @param object 对象
     * @param <T> 泛型
     * @return JSON字符串
     */
    public static <T> String objectToString(T object) {
        if (object == null) {
            return null;
        }
        try {
            return object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }

    /**
     * 将对象转换成JSON字符串（人类易读的字符串）
     * @param object 对象
     * @param <T> 泛型
     * @return JSON字符串
     */
    public static <T> String objectToPrettyString(T object) {
        if (object == null) {
            return null;
        }
        try {
            return object instanceof String ? (String) object : objectMapper.
                    writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }

    /**
     * 将JSON字符串转换成对象
     * @param jsonStr JSON字符串
     * @param clz 需要转换成的类对象
     * @param <T> 泛型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToObject(String jsonStr, Class<T> clz) {
        if (StringUtils.isBlank(jsonStr) || clz == null) {
            return null;
        }
        if (clz.equals(String.class)) {
            return (T) jsonStr;
        }
        try {
            return objectMapper.readValue(jsonStr, clz);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }

    /**
     * 将JSON字符串转换成类对象
     * @param jsonStr JSON字符串
     * @param typeReference 类型引用（这是JackSon里的一个抽象类，不能直接实例化）
     * @param <T> 泛型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToObject(String jsonStr, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonStr) || typeReference == null) {
            return null;
        }
        if (String.class.equals(typeReference.getType())) {
            return (T)jsonStr;
        }

        try {
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }

    /**
     * 将字符串转换成对象，主要用于集合
     * @param jsonStr JSON字符串
     * @param collectionType 集合类型，例如List，Map
     * @param classes 集合中元素的类型，例如User
     * @param <T> 泛型
     * @return 返回对象
     */
    public static <T> T stringToObject(String jsonStr, Class<?> collectionType, Class<?> ...classes) {

        if (StringUtils.isBlank(jsonStr) || collectionType == null || classes == null) {
            return null;
        }

        //这里将集合类型和其元素类型组合成一个类型，然后再使用
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionType, classes);

        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }

}
