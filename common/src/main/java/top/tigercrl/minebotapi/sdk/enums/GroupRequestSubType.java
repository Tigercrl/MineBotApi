package top.tigercrl.minebotapi.sdk.enums;

/**
 * 请求子类型
 */
public enum GroupRequestSubType {

    /**
     * 加群请求
     */
    ADD("add"),
    /**
     * 邀请机器人入群
     */
    INVITE("invite");

    public final String value;

    GroupRequestSubType(String value) {
        this.value = value;
    }

    /**
     * 获取请求子类型字符串
     *
     * @return 请求子类型字符串
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串获取请求子类型
     *
     * @param value 请求子类型字符串
     * @return 请求子类型
     */
    public static GroupRequestSubType getSubType(String value) {
        return switch (value) {
            case "add" -> ADD;
            case "invite" -> INVITE;
            default -> throw new IllegalArgumentException("未知请求子类型");
        };
    }
}
