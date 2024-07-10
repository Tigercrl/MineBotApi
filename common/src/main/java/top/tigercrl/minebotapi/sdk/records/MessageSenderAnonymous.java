package top.tigercrl.minebotapi.sdk.records;

import top.tigercrl.minebotapi.sdk.bot.Bot;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * 匿名消息发送人信息
 *
 * @param id   匿名用户 ID
 * @param name 匿名用户名称
 * @param flag 匿名用户 flag，在调用 {@link Bot#setGroupAnonymousMute} 时需要传入
 */
public record MessageSenderAnonymous(long id, @NotNull String name, @NotNull String flag) {
    /**
     * 转换为 JSONObject
     *
     * @return JSON 对象
     */
    public JSONObject toJSON() {
        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("flag", flag);
    }

    /**
     * 从 JSONObject 中创建匿名消息发送人信息
     *
     * @param jsonObject JSON 对象
     * @return 匿名消息发送人信息
     */
    public static MessageSenderAnonymous fromJSON(JSONObject jsonObject) {
        return new MessageSenderAnonymous(
                jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("flag")
        );
    }
}
