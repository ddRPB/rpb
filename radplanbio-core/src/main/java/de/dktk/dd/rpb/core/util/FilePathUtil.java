package de.dktk.dd.rpb.core.util;

public class FilePathUtil {
    public static String verifyTrailingSlash(String path) {
        if (path.endsWith("/")) {
            return path;
        } else {
            return path + "/";
        }
    }
}
