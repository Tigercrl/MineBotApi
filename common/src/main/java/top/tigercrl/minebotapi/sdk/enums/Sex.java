package top.tigercrl.minebotapi.sdk.enums;

/**
 * 性别
 */
public enum Sex {
    MALE("male"),
    FEMALE("female"),
    UNKNOWN("unknown");

    private final String value;

    Sex(String value) {
        this.value = value;
    }

    /**
     * 获取性别字符串
     *
     * @return 性别字符串
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串获取性别
     *
     * @param value 性别字符串
     * @return 性别
     */
    public static Sex getSex(String value) {
        return switch (value) {
            case "male" -> MALE;
            case "female" -> FEMALE;
            default -> UNKNOWN;
        };
    }
}