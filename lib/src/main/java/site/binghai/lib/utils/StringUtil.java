package site.binghai.lib.utils;

public class StringUtil {
    public static String shorten(String str, int len) {
        if (str == null || str.length() <= len) return str;
        return str.substring(0, len);
    }
}
