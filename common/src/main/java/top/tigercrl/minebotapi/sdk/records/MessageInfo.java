package top.tigercrl.minebotapi.sdk.records;

import top.tigercrl.minebotapi.sdk.enums.MessageType;
import top.tigercrl.minebotapi.sdk.enums.Role;
import top.tigercrl.minebotapi.sdk.enums.Sex;
import top.tigercrl.minebotapi.sdk.message.ArrayMessage;
import top.tigercrl.minebotapi.sdk.message.StringMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * 消息数据
 *
 * @param time        number (int32)	发送时间
 * @param messageType string	消息类型
 * @param messageId   number (int32)	消息 ID
 * @param realId      number (int32)	消息真实 ID nullable
 * @param sender      发送人信息
 * @param message     消息内容
 * @param selfId      收到信息的机器人 QQ 号 nullable
 * @param subType     消息子类型 nullable
 * @param rawMessage  string	-	原始消息内容nullable
 * @param font        number (int32)	-	字体nullable
 * @param groupId     number (int64)	-	群号nullable
 * @param anonymous   object	-	匿名信息，如果不是匿名消息则为 null nullable
 */
public record MessageInfo(long time, @NotNull MessageType messageType, int messageId, @Nullable Integer realId,
                          @NotNull MessageSender sender, @NotNull ArrayMessage message, @Nullable Long selfId,
                          @Nullable SubType subType, @Nullable StringMessage rawMessage, @Nullable Integer font,
                          @Nullable Long groupId, @Nullable MessageSenderAnonymous anonymous) {
    /**
     * 从 JSONObject 中创建消息数据
     *
     * @param json JSONObject
     * @return 消息数据
     */
    public static MessageInfo fromJSON(JSONObject json) {
        if (!json.has("post_type") || !json.getString("post_type").equals("message"))
            throw new IllegalArgumentException("该JSON不是一个消息");
        return new MessageInfo(
                json.getLong("time"),
                MessageType.getMessageType(json.getString("message_type")),
                json.getInt("message_id"),
                json.has("real_id") ? json.getInt("real_id") : null,
                MessageSender.fromJSON(json.getJSONObject("sender")),
                ArrayMessage.fromJSON(json.getJSONArray("message")),
                json.has("self_id") ? json.getLong("self_id") : null,
                json.has("sub_type") ? SubType.getSubType(json.getString("sub_type")) : null,
                json.has("raw_message") ? new StringMessage(json.getString("raw_message")) : null,
                json.has("font") ? json.getInt("font") : null,
                json.has("group_id") ? json.getLong("group_id") : null,
                json.has("anonymous") ? MessageSenderAnonymous.fromJSON(json.getJSONObject("anonymous")): null);
    }

    /**
     * 转换为 JSONObject
     *
     * @return JSONObject
     */
    public JSONObject toJSON() {
        return new JSONObject()
                .put("time", time)
                .put("message_type", messageType.getValue())
                .put("message_id", messageId)
                .put("real_id", realId)
                .put("sender", sender.toJSON())
                .put("message", message.getMessage())
                .put("self_id", selfId)
                .put("sub_type", subType == null ? null : subType.getValue())
                .put("raw_message", rawMessage == null ? null : rawMessage.getMessage())
                .put("font", font)
                .put("group_id", groupId)
                .put("anonymous", anonymous == null ? null : anonymous.toJSON());

    }

    /**
     * 消息子类型
     */
    public enum SubType {
        /**
         * 普通消息（群消息事件）
         */
        NORMAL("normal"),
        /**
         * 匿名消息（群消息事件）
         */
        ANONYMOUS("anonymous"),
        /**
         * 系统通知消息（群消息事件）
         */
        NOTICE("notice"),
        /**
         * 好友消息（私聊消息事件）
         */
        FRIEND("friend"),
        /**
         * 群临时会话消息（私聊消息事件）
         */
        GROUP("group"),
        /**
         * 其他消息（私聊消息事件）
         */
        OTHER("other");

        public final String value;

        SubType(String value) {
            this.value = value;
        }

        /**
         * 获取消息子类型字符串
         *
         * @return 消息子类型字符串
         */
        public String getValue() {
            return value;
        }

        /**
         * 根据字符串获取消息子类型
         *
         * @param value 消息子类型字符串
         * @return 消息子类型
         */
        public static SubType getSubType(String value) {
            return switch (value) {
                case "anonymous" -> ANONYMOUS;
                case "notice" -> NOTICE;
                default -> NORMAL;
            };
        }
    }

    /**
     * 消息发送人信息
     *
     * @param userId   发送者 QQ 号
     * @param nickname 昵称
     * @param sex      性别
     * @param age      年龄
     * @param card     群名片／备注
     * @param area     地区
     * @param level    群成员等级
     * @param role     群角色
     * @param title    群荣誉头衔
     */
    record MessageSender(long userId, @NotNull String nickname, @Nullable Sex sex, @Nullable Integer age,
                         @Nullable String card, @Nullable String area, @Nullable String level, @Nullable Role role,
                         @Nullable String title) {
        /**
         * 转换为 JSONObject
         *
         * @return JSONObject
         */
        public JSONObject toJSON() {
            return new JSONObject()
                    .put("user_id", userId)
                    .put("nickname", nickname)
                    .put("sex", sex != null ? sex.getValue() : null)
                    .put("age", age)
                    .put("card", card)
                    .put("area", area)
                    .put("level", level)
                    .put("role", role != null ? role.getValue() : null)
                    .put("title", title);
        }

        /**
         * 从 JSONObject 中创建消息发送人信息
         *
         * @param jsonObject JSONObject
         * @return MessageSender
         */
        public static MessageSender fromJSON(JSONObject jsonObject) {
            return new MessageSender(jsonObject.getInt("user_id"),
                    jsonObject.getString("nickname"),
                    jsonObject.has("sex") ? Sex.getSex(jsonObject.getString("sex")) : null,
                    jsonObject.has("age") ? jsonObject.getInt("age") : null,
                    jsonObject.has("card") ? jsonObject.getString("card") : null,
                    jsonObject.has("area") ? jsonObject.getString("area") : null,
                    jsonObject.has("level") ? jsonObject.getString("level") : null,
                    jsonObject.has("role") ? Role.getRole(jsonObject.getString("role")) : null,
                    jsonObject.has("title") ? jsonObject.getString("title") : null);
        }
    }
}
