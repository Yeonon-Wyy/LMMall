package top.yeonon.common.util;




import top.yeonon.common.properties.CoreProperties;


import java.security.MessageDigest;

/**
 * @Author yeonon
 * @date 2018/4/3 0003 20:14
 **/
public final class MD5Utils {

    private static CoreProperties coreProperties;

    static {
        coreProperties = new CoreProperties();
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 返回大写MD5
     *
     */
    private static String md5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception exception) {
        }
        return resultString.toUpperCase();
    }

    public static String md5EncodeUTF8(String origin) {
        origin = origin + coreProperties.getSecurity().getMd5Salt();
        return md5Encode(origin, "utf-8");
    }


    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
}
