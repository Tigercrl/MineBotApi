package top.tigercrl.minebotapi.utils;

public class EncodingUtils {
    public static String encodeCQCode(String message) {
        return message
                .replace("&", "&amp;")
                .replace("[", "&#91;")
                .replace("]", "&#93;")
                .replace(",", "&#44;");
    }

    public static String decodeCQCode(String message) {
        return message
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&#44;", ",")
                .replace("&amp;", "&");
    }

    public static String encodeText(String message) {
        return message
                .replace("&", "&amp;")
                .replace("[", "&#91;")
                .replace("]", "&#93;");
    }

    public static String decodeText(String message) {
        return message
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&amp;", "&");
    }
}
