package top.tigercrl.minebotapi.sdk.enums;

/**
 * 群角色
 */
public enum Role {
    OWNER("owner"),
    ADMIN("admin"),
    MEMBER("member");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    /**
     * 获取角色字符串
     *
     * @return 角色字符串
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串获取角色
     *
     * @param value 角色字符串
     * @return 角色
     */
    public static Role getRole(String value) {
        return switch (value) {
            case "owner" -> OWNER;
            case "admin" -> ADMIN;
            case "member" -> MEMBER;
            default -> throw new IllegalArgumentException("未知角色");
        };
    }
}