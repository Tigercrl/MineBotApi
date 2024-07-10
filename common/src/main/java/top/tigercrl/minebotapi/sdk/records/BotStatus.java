package top.tigercrl.minebotapi.sdk.records;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * 机器人运行状态
 *
 * @param online 当前 QQ 在线，null 表示无法查询到在线状态
 * @param good   状态符合预期，意味着各模块正常运行、功能正常，且 QQ 在线
 * @param other  OneBot 实现自行添加的其它内容
 */
public record BotStatus(@Nullable Boolean online, boolean good, JSONObject other) {
    /**
     * 从 JSONObject 中创建机器人运行状态
     *
     * @param json JSON对象
     * @return 机器人运行状态
     */
    public static BotStatus fromJson(JSONObject json) {
        Boolean online = json.has("online") ? json.getBoolean("online") : null;
        boolean good = json.getBoolean("good");
        json.remove("online");
        json.remove("good");
        return new BotStatus(online, good, json);
    }

    /**
     * 转换为 JSONObject
     *
     * @return JSON对象
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        if (online != null) json.put("online", online);
        json.put("good", good);
        for (String key : other.keySet()) {
            json.put(key, other.get(key));
        }
        return json;
    }
}
