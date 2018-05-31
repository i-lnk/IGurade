package com.rl.geye.image;

public class UrlUtils {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String HTTPS_PROTOCOL = "https://";
    public static final String HTTP_PROTOCOL = "http://";

    public static String[] getBaseAndQuery(String url) {
        String[] params = null;
        if (url.contains("?"))
            params = url.split("\\?");
        return params;
    }

    public static boolean isAbsolutePath(String url) {
        if ((url.startsWith("http://")) || (url.startsWith("https://"))) {
            return true;
        }
        return false;
    }

}