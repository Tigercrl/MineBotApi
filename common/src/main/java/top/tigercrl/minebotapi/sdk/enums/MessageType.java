package top.tigercrl.minebotapi.sdk.enums;

/**
 * 消息类型
 */
public enum MessageType {
    PRIVATE("private"),
    GROUP("group");

    public final String value;

    MessageType(String value) {
        this.value = value;
    }

    /**
     * 获取消息类型字符串
     *
     * @return 消息类型字符串
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串获取消息类型
     *
     * @param value 消息类型字符串
     * @return 消息类型
     */
    public static MessageType getMessageType(String value) {
        return switch (value) {
            case "private" -> PRIVATE;
            case "group" -> GROUP;
            default -> throw new IllegalArgumentException("未知消息类型");
        };
    }
}
