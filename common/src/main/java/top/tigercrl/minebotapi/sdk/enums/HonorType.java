package top.tigercrl.minebotapi.sdk.enums;

/**
 * 荣誉类型
 */
public enum HonorType {
    /**
     * 龙王
     */
    TALKATIVE("talkative"),
    /**
     * 群聊之火
     */
    PERFORMER("performer"),
    /**
     * 群聊炽焰
     */
    LEGEND("legend"),
    /**
     * 冒尖小春笋
     */
    STRONG_NEWBIE("strong_newbie"),
    /**
     * 快乐源泉
     */
    EMOTION("emotion"),
    /**
     * 全部
     */
    ALL("all");

    private final String value;

    HonorType(String value) {
        this.value = value;
    }

    /**
     * 获取荣誉类型字符串
     *
     * @return 荣誉类型字符串
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串获取荣誉类型
     *
     * @param value 荣誉类型字符串
     * @return 荣誉类型
     */
    public static HonorType getHonorType(String value) {
        return switch (value) {
            case "talkative" -> TALKATIVE;
            case "performer" -> PERFORMER;
            case "emotion" -> EMOTION;
            default -> throw new IllegalArgumentException("未知荣誉类型");
        };
    }
}
