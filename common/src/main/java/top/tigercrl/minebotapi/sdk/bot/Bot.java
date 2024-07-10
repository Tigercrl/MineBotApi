package top.tigercrl.minebotapi.sdk.bot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import top.tigercrl.minebotapi.sdk.enums.*;
import top.tigercrl.minebotapi.sdk.events.RequestEvent;
import top.tigercrl.minebotapi.sdk.message.ArrayMessage;
import top.tigercrl.minebotapi.sdk.message.Message;
import top.tigercrl.minebotapi.sdk.message.MessageSegment;
import top.tigercrl.minebotapi.sdk.records.APIResponse;
import top.tigercrl.minebotapi.sdk.records.BotStatus;
import top.tigercrl.minebotapi.sdk.records.MessageInfo;
import top.tigercrl.minebotapi.sdk.records.MessageSenderAnonymous;

import java.util.UUID;

/**
 * 基于OneBot的机器人
 * API见https://github.com/botuniverse/onebot-11/blob/master/api/public.md
 */
public interface Bot {
    /**
     * 机器人UUID
     */
    UUID uuid = UUID.randomUUID();

    /**
     * 创建正向WebSocket机器人
     *
     * @param oneBotServerUrl OneBot服务器地址
     * @return 机器人
     */
    static Bot createWebSocketBot(@NotNull String oneBotServerUrl) {
        return createWebSocketBot(oneBotServerUrl, null);
    }

    /**
     * 创建正向WebSocket机器人
     *
     * @param oneBotServerUrl OneBot服务器地址
     * @param accessToken     访问令牌，为 {@code null} 或为空字符串就不使用
     * @return 机器人
     */
    static Bot createWebSocketBot(@NotNull String oneBotServerUrl, @Nullable String accessToken) {
        if (!oneBotServerUrl.startsWith("ws://") && !oneBotServerUrl.startsWith("wss://"))
            throw new IllegalArgumentException("OneBot WebSocket服务器链接必须以 ws:// 或 wss:// 开头");
        if (!oneBotServerUrl.endsWith("/"))
            oneBotServerUrl += "/";
        if (accessToken != null && !accessToken.isEmpty())
            oneBotServerUrl += "?access_token=" + accessToken;
        return new WSBot(oneBotServerUrl);
    }

    /**
     * 发送私聊消息
     *
     * @param userId      用户ID
     * @param message     消息内容
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return 消息ID
     */
    default APIResponse<Integer> sendPrivateMsg(long userId, @NotNull Message message, @Nullable APIRequestType requestType) {
        return sendPrivateMsg(userId, message, false, requestType);
    }

    /**
     * 发送私聊消息
     *
     * @param userId      对方 QQ 号
     * @param message     要发送的内容
     * @param autoEscape  消息内容是否作为纯文本发送（即不解析 CQ 码），只在 message 字段是字符串时有效
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return 消息ID
     */
    APIResponse<Integer> sendPrivateMsg(long userId, @NotNull Message message, boolean autoEscape, @Nullable APIRequestType requestType);

    /**
     * 发送群消息
     *
     * @param groupId     群号
     * @param message     消息内容
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return 消息ID
     */
    default APIResponse<Integer> sendGroupMsg(long groupId, @NotNull Message message, @Nullable APIRequestType requestType) {
        return sendGroupMsg(groupId, message, false, requestType);
    }

    /**
     * 发送群消息
     *
     * @param groupId     群号
     * @param message     要发送的内容
     * @param autoEscape  消息内容是否作为纯文本发送（即不解析 CQ 码），只在 message 字段是字符串时有效
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return 消息ID
     */
    APIResponse<Integer> sendGroupMsg(long groupId, @NotNull Message message, boolean autoEscape, @Nullable APIRequestType requestType);

    /**
     * 发送消息
     *
     * @param messageType 消息类型，如不传入，则根据传入的 {@code id} 参数判断
     * @param userId      对方 QQ 号（消息类型为 {@link MessageType#PRIVATE} 时需要）
     * @param groupId     群号（消息类型为 {@link MessageType#GROUP} 时需要）
     * @param message     要发送的内容
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return 消息ID
     */
    default APIResponse<Integer> sendMsg(@Nullable MessageType messageType, long userId, long groupId, @NotNull Message message, @Nullable APIRequestType requestType) {
        return sendMsg(messageType, userId, groupId, message, false, requestType);
    }

    /**
     * 发送消息
     *
     * @param messageType 消息类型，如不传入，则根据传入的 {@code id} 参数判断
     * @param userId      对方 QQ 号（消息类型为 {@link MessageType#PRIVATE} 时需要）
     * @param groupId     群号（消息类型为 {@link MessageType#GROUP} 时需要）
     * @param message     要发送的内容
     * @param autoEscape  消息内容是否作为纯文本发送（即不解析 CQ 码），只在 {@code message} 字段是字符串时有效
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return 消息ID
     */
    APIResponse<Integer> sendMsg(@Nullable MessageType messageType, long userId, long groupId, @NotNull Message message, boolean autoEscape, @Nullable APIRequestType requestType);

    /**
     * 撤回消息
     *
     * @param messageId   消息ID
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> recallMsg(int messageId, @Nullable APIRequestType requestType);

    /**
     * 获取消息
     *
     * @param messageId   消息ID
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 消息信息
     */
    APIResponse<MessageInfo> getMsg(int messageId, @Nullable APIRequestType requestType);

    /**
     * 获取合并转发消息
     *
     * @param id          合并转发 ID
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 消息内容，使用 {@link ArrayMessage} 表示，数组中的消息段全部为 {@link MessageSegment#node(String)} 消息段
     */
    APIResponse<ArrayMessage> getForwardMsg(@NotNull String id, @Nullable APIRequestType requestType);

    /**
     * 发送好友赞
     *
     * @param userId      对方 QQ 号
     * @param times       赞的次数，每个好友每天最多 10 次
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> sendLike(long userId, int times, @Nullable APIRequestType requestType);

    /**
     * 群组踢人
     *
     * @param groupId     群号
     * @param userId      要踢的 QQ 号
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    default APIResponse<Void> setGroupKick(long groupId, long userId, @Nullable APIRequestType requestType) {
        return setGroupKick(groupId, userId, false, requestType);
    }

    /**
     * 群组踢人
     *
     * @param groupId          群号
     * @param userId           要踢的 QQ 号
     * @param rejectAddRequest 是否拒绝此人的加群请求
     * @param requestType      请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupKick(long groupId, long userId, boolean rejectAddRequest, @Nullable APIRequestType requestType);

    /**
     * 群组单人禁言
     *
     * @param groupId     群号
     * @param userId      要禁言的 QQ 号
     * @param duration    禁言时长，单位秒，0 表示取消禁言
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupMute(long groupId, long userId, long duration, @Nullable APIRequestType requestType);

    /**
     * 群组匿名用户禁言
     *
     * @param groupId     群号
     * @param anonymous   可选，要禁言的匿名用户对象
     * @param flag        可选，要禁言的匿名用户的 {@code flag}
     * @param duration    禁言时长，单位秒，无法取消匿名用户禁言
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupAnonymousMute(long groupId, @Nullable MessageSenderAnonymous anonymous, @Nullable String flag, long duration, @Nullable APIRequestType requestType);

    /**
     * 群组全员禁言
     *
     * @param groupId     群号
     * @param enable      是否禁言
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupWholeMute(long groupId, boolean enable, @Nullable APIRequestType requestType);

    /**
     * 群组设置管理员
     *
     * @param groupId     群号
     * @param userId      要设置管理员的 QQ 号
     * @param enable      {@code true} 为设置，{@code false} 为取消
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupAdmin(long groupId, long userId, boolean enable, @Nullable APIRequestType requestType);

    /**
     * 群组匿名
     *
     * @param groupId     群号
     * @param enable      是否允许匿名聊天
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupAnonymous(long groupId, boolean enable, @Nullable APIRequestType requestType);

    /**
     * 设置群名片（群昵称）
     *
     * @param groupId     群号
     * @param userId      要设置的 QQ 号
     * @param card        群名片内容，{@code null} 或空字符串表示删除群名片
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupCard(long groupId, long userId, @Nullable String card, @Nullable APIRequestType requestType);

    /**
     * 设置群名
     *
     * @param groupId     群号
     * @param groupName   新群名
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupName(long groupId, @NotNull String groupName, @Nullable APIRequestType requestType);

    /**
     * 退出群组
     *
     * @param groupId     群号
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    default APIResponse<Void> setGroupLeave(long groupId, @Nullable APIRequestType requestType) {
        return setGroupLeave(groupId, false, requestType);
    }

    /**
     * 退出群组
     *
     * @param groupId     群号
     * @param isDismiss   是否解散，如果登录号是群主，则仅在此项为 {@code true} 时能够解散
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupLeave(long groupId, boolean isDismiss, @Nullable APIRequestType requestType);

    /**
     * 设置群荣誉专属头衔
     *
     * @param groupId      群号
     * @param userId       要设置的 QQ 号
     * @param specialTitle 专属头衔，{@code null} 或空字符串表示删除专属头衔
     * @param duration     专属头衔有效期，单位秒，{@code -1} 表示永久，不过此项似乎没有效果，可能是只有某些特殊的时间长度有效，有待测试
     * @param requestType  请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupSpecialTitle(long groupId, long userId, @Nullable String specialTitle, long duration, @Nullable APIRequestType requestType);

    /**
     * 处理加好友请求
     *
     * @param flag        加好友请求的 {@code flag}（需从 {@link RequestEvent.Friend} 中获得）
     * @param approve     是否同意
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    default APIResponse<Void> setFriendAddRequest(String flag, boolean approve, @Nullable APIRequestType requestType) {
        return setFriendAddRequest(flag, approve, null, requestType);
    }

    /**
     * 处理加好友请求
     *
     * @param flag        加好友请求的 {@code flag}（需从 {@link RequestEvent.Friend} 中获得）
     * @param approve     是否同意
     * @param remark      添加后的好友备注（仅在同意时有效）
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setFriendAddRequest(String flag, boolean approve, @Nullable String remark, @Nullable APIRequestType requestType);

    /**
     * 处理加群请求/邀请
     *
     * @param flag        加群请求的 {@code flag}（需从 {@link RequestEvent.Group} 中获得）
     * @param subType     加群请求类型
     * @param approve     是否同意
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    default APIResponse<Void> setGroupAddRequest(String flag, GroupRequestSubType subType, boolean approve, @Nullable APIRequestType requestType) {
        return setGroupAddRequest(flag, subType, approve, null, requestType);
    }

    /**
     * 处理加群请求/邀请
     *
     * @param flag        加群请求的 {@code flag}（需从 {@link RequestEvent.Group} 中获得）
     * @param subType     加群请求类型
     * @param approve     是否同意
     * @param reason      拒绝理由（仅在拒绝时有效）
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> setGroupAddRequest(String flag, GroupRequestSubType subType, boolean approve, @Nullable String reason, @Nullable APIRequestType requestType);

    /**
     * 获取登录号信息
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 登录号信息
     */
    APIResponse<LoginInfo> getLoginInfo(@Nullable APIRequestType requestType);

    /**
     * 登录号信息
     *
     * @param userId   QQ 号
     * @param nickname QQ 昵称
     */
    record LoginInfo(long userId, String nickname) {
        /**
         * 从 JSON 创建登录号信息
         *
         * @param json JSON对象
         * @return 登录号信息
         */
        public static LoginInfo fromJSON(JSONObject json) {
            return new LoginInfo(json.getLong("user_id"), json.getString("nickname"));
        }

        /**
         * 转换为 JSON
         *
         * @return JSON
         */
        public JSONObject toJSON() {
            return new JSONObject().put("user_id", userId).put("nickname", nickname);
        }
    }

    /**
     * 获取陌生人信息
     *
     * @param userId      QQ 号
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 陌生人信息
     */
    default APIResponse<StrangerInfo> getStrangerInfo(long userId, @Nullable APIRequestType requestType) {
        return getStrangerInfo(userId, false, requestType);
    }

    /**
     * 获取陌生人信息
     *
     * @param userId      QQ 号
     * @param noCache     是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 陌生人信息
     */
    APIResponse<StrangerInfo> getStrangerInfo(long userId, boolean noCache, @Nullable APIRequestType requestType);

    /**
     * 陌生人信息
     *
     * @param userId   QQ 号
     * @param nickname 昵称
     * @param sex      性别
     * @param age      年龄
     */
    record StrangerInfo(long userId, String nickname, Sex sex, @Nullable Integer age) {
        /**
         * 从 JSON 创建陌生人信息
         *
         * @param json JSON对象
         * @return 陌生人信息
         */
        public static StrangerInfo fromJSON(JSONObject json) {
            return new StrangerInfo(json.getLong("user_id"),
                    json.getString("nickname"),
                    Sex.getSex(json.getString("sex")),
                    json.has("age") ? json.getInt("age") : null);
        }

        /**
         * 转换为 JSON
         *
         * @return JSON
         */
        public JSONObject toJSON() {
            return new JSONObject().put("user_id", userId)
                    .put("nickname", nickname)
                    .put("sex", sex.getValue())
                    .put("age", age);
        }
    }

    /**
     * 获取好友列表
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 好友列表
     */
    APIResponse<FriendInfo[]> getFriendList(@Nullable APIRequestType requestType);

    /**
     * 好友信息
     *
     * @param userId   QQ 号
     * @param nickname 昵称
     * @param remark   备注
     */
    record FriendInfo(long userId, String nickname, @Nullable String remark) {
        /**
         * 从 JSON 创建好友信息
         *
         * @param json JSON对象
         * @return 好友信息
         */
        public static FriendInfo fromJSON(JSONObject json) {
            return new FriendInfo(json.getLong("user_id"),
                    json.getString("nickname"),
                    json.has("remark") ? json.getString("remark") : null);
        }

        /**
         * 转换为 JSON
         *
         * @return JSON
         */
        public JSONObject toJSON() {
            return new JSONObject().put("user_id", userId)
                    .put("nickname", nickname)
                    .put("remark", remark);
        }
    }

    /**
     * 获取群信息
     *
     * @param groupId     群号
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 群信息
     */
    default APIResponse<GroupInfo> getGroupInfo(long groupId, @Nullable APIRequestType requestType) {
        return getGroupInfo(groupId, false, requestType);
    }

    /**
     * 获取群信息
     *
     * @param groupId     群号
     * @param noCache     是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 群信息
     */
    APIResponse<GroupInfo> getGroupInfo(long groupId, boolean noCache, @Nullable APIRequestType requestType);

    /**
     * 群信息
     *
     * @param groupId        群号
     * @param groupName      群名
     * @param memberCount    成员数
     * @param maxMemberCount 最大成员数（群容量）
     */
    record GroupInfo(long groupId, String groupName, int memberCount, int maxMemberCount) {
        /**
         * 从 JSON 创建群信息
         *
         * @param json JSON对象
         * @return 群信息
         */
        public static GroupInfo fromJSON(JSONObject json) {
            return new GroupInfo(json.getLong("group_id"),
                    json.getString("group_name"),
                    json.getInt("member_count"),
                    json.getInt("max_member_count"));
        }

        /**
         * 转换为 JSON
         *
         * @return JSON
         */
        public JSONObject toJSON() {
            return new JSONObject().put("group_id", groupId)
                    .put("group_name", groupName)
                    .put("member_count", memberCount)
                    .put("max_member_count", maxMemberCount);
        }
    }

    /**
     * 获取群成员信息
     *
     * @param groupId     群号
     * @param userId      QQ 号
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 群成员信息
     */
    default APIResponse<GroupMemberInfo> getGroupMemberInfo(long groupId, long userId, @Nullable APIRequestType requestType) {
        return getGroupMemberInfo(groupId, userId, false, requestType);
    }

    /**
     * 获取群成员信息
     *
     * @param groupId     群号
     * @param userId      QQ 号
     * @param noCache     是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 群成员信息
     */
    APIResponse<GroupMemberInfo> getGroupMemberInfo(long groupId, long userId, boolean noCache, @Nullable APIRequestType requestType);

    /**
     * 群成员信息
     *
     * @param groupId         群号
     * @param userId          QQ 号
     * @param nickname        昵称
     * @param card            群名片
     * @param sex             性别
     * @param age             年龄
     * @param area            地区
     * @param joinTime        加群时间戳
     * @param lastSentTime    最后发言时间戳
     * @param level           群等级
     * @param role            角色
     * @param unfriendly      是否不良记录成员
     * @param title           专属头衔
     * @param titleExpireTime 专属头衔过期时间戳
     * @param cardChangeable  是否允许修改群名片
     */
    record GroupMemberInfo(long groupId, long userId, String nickname, @Nullable String card, Sex sex,
                           @Nullable Integer age, @Nullable String area, long joinTime, long lastSentTime, int level,
                           Role role, boolean unfriendly, @Nullable String title, long titleExpireTime,
                           boolean cardChangeable) {
        /**
         * 从 JSON 创建群成员信息
         *
         * @param json JSON对象
         * @return 群成员信息
         */
        public static GroupMemberInfo fromJSON(JSONObject json) {
            return new GroupMemberInfo(json.getLong("group_id"),
                    json.getLong("user_id"),
                    json.getString("nickname"),
                    json.has("card") ? json.getString("card") : null,
                    Sex.getSex(json.getString("sex")),
                    json.has("age") ? json.getInt("age") : null,
                    json.has("area") ? json.getString("area") : null,
                    json.getLong("join_time"),
                    json.getLong("last_sent_time"),
                    json.getInt("level"),
                    Role.getRole(json.getString("role")),
                    json.getBoolean("unfriendly"),
                    json.has("title") ? json.getString("title") : null,
                    json.getLong("title_expire_time"),
                    json.getBoolean("card_changeable"));
        }

        /**
         * 转换为 JSON
         *
         * @return JSON
         */
        public JSONObject toJSON() {
            return new JSONObject().put("group_id", groupId)
                    .put("user_id", userId)
                    .put("nickname", nickname)
                    .put("card", card)
                    .put("sex", sex.getValue())
                    .put("age", age)
                    .put("area", area)
                    .put("join_time", joinTime)
                    .put("last_sent_time", lastSentTime)
                    .put("level", level)
                    .put("role", role.getValue())
                    .put("unfriendly", unfriendly)
                    .put("title", title)
                    .put("title_expire_time", titleExpireTime)
                    .put("card_changeable", cardChangeable);
        }
    }

    /**
     * 获取群成员列表
     *
     * @param groupId     群号
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 群成员列表
     */
    APIResponse<GroupMemberInfo[]> getGroupMemberList(long groupId, @Nullable APIRequestType requestType);

    /**
     * 获取群荣誉信息
     *
     * @param groupId     群号
     * @param type        群荣誉类型
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 群荣誉信息
     */
    APIResponse<GroupHonorInfo> getGroupHonorInfo(long groupId, HonorType type, @Nullable APIRequestType requestType);

    /**
     * 群荣誉信息
     *
     * @param groupId          群号
     * @param currentTalkative 当前龙王，仅 {@code type} 为 {@link HonorType#TALKATIVE} 或 {@link HonorType#ALL} 时有数据
     * @param talkativeList    历史龙王，仅 {@code type} 为 {@link HonorType#TALKATIVE} 或 {@link HonorType#ALL} 时有数据
     * @param performerList    群聊之火，仅 {@code type} 为 {@link HonorType#PERFORMER} 或 {@link HonorType#ALL} 时有数据
     * @param legendList       群聊炽焰，仅 {@code type} 为 {@link HonorType#LEGEND} 或 {@link HonorType#ALL} 时有数据
     * @param strongNewbieList 冒尖小春笋，仅 {@code type} 为 {@link HonorType#STRONG_NEWBIE} 或 {@link HonorType#ALL} 时有数据
     * @param emotionList      快乐源泉，仅 {@code type} 为 {@link HonorType#PERFORMER} 或 {@link HonorType#ALL} 时有数据
     */
    record GroupHonorInfo(long groupId, @Nullable CurrentTalkative currentTalkative,
                          @Nullable HonorListItem[] talkativeList, @Nullable HonorListItem[] performerList,
                          @Nullable HonorListItem[] legendList, @Nullable HonorListItem[] strongNewbieList,
                          @Nullable HonorListItem[] emotionList) {
        /**
         * 从 JSON 创建群荣誉信息
         *
         * @param json JSON对象
         * @return 群荣誉信息
         */
        public static GroupHonorInfo fromJSON(JSONObject json) {
            return new GroupHonorInfo(json.getLong("group_id"),
                    json.has("current_talkative") ? CurrentTalkative.fromJSON(json.getJSONObject("current_talkative")) : null,
                    json.has("talkative_list") ? toHonorListItem(json.getJSONArray("talkative_list")) : null,
                    json.has("performer_list") ? toHonorListItem(json.getJSONArray("performer_list")) : null,
                    json.has("legend_list") ? toHonorListItem(json.getJSONArray("legend_list")) : null,
                    json.has("strong_newbie_list") ? toHonorListItem(json.getJSONArray("strong_newbie_list")) : null,
                    json.has("emotion_list") ? toHonorListItem(json.getJSONArray("emotion_list")) : null);
        }

        private static HonorListItem[] toHonorListItem(JSONArray jsonArray) {
            HonorListItem[] items = new HonorListItem[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                items[i] = HonorListItem.fromJSON(jsonArray.getJSONObject(i));
            }
            return items;
        }

        public JSONObject toJSON() {
            return new JSONObject().put("group_id", groupId)
                    .put("current_talkative", currentTalkative == null ? null : currentTalkative.toJSON())
                    .put("talkative_list", talkativeList == null ? null : toHonorJSONArray(talkativeList))
                    .put("performer_list", performerList == null ? null : toHonorJSONArray(performerList))
                    .put("legend_list", legendList == null ? null : toHonorJSONArray(legendList))
                    .put("strong_newbie_list", strongNewbieList == null ? null : toHonorJSONArray(strongNewbieList))
                    .put("emotion_list", emotionList == null ? null : toHonorJSONArray(emotionList));
        }

        private static JSONArray toHonorJSONArray(HonorListItem[] items) {
            JSONArray jsonArray = new JSONArray();
            for (HonorListItem item : items) {
                jsonArray.put(item.toJSON());
            }
            return jsonArray;
        }

        /**
         * 当前龙王
         *
         * @param userId   QQ 号
         * @param nickname 昵称
         * @param avatar   头像 URL
         * @param dayCount 持续天数
         */
        record CurrentTalkative(String userId, String nickname, String avatar, int dayCount) {
            /**
             * 从 JSON 创建当前龙王
             *
             * @param json JSON对象
             * @return 当前龙王
             */
            public static CurrentTalkative fromJSON(JSONObject json) {
                return new CurrentTalkative(json.getString("user_id"),
                        json.getString("nickname"),
                        json.getString("avatar"),
                        json.getInt("day_count"));
            }

            /**
             * 转换为 JSON
             *
             * @return JSON对象
             */
            public JSONObject toJSON() {
                return new JSONObject().put("user_id", userId)
                        .put("nickname", nickname)
                        .put("avatar", avatar)
                        .put("day_count", dayCount);
            }
        }

        /**
         * 荣誉列表
         *
         * @param userId      QQ 号
         * @param nickname    昵称
         * @param avatar      头像 URL
         * @param description 荣誉描述
         */
        record HonorListItem(String userId, String nickname, String avatar, String description) {
            /**
             * 从 JSON 创建荣誉列表
             *
             * @param json JSON对象
             * @return 荣誉列表
             */
            public static HonorListItem fromJSON(JSONObject json) {
                return new HonorListItem(json.getString("user_id"),
                        json.getString("nickname"),
                        json.getString("avatar"),
                        json.getString("description"));
            }

            /**
             * 转换为 JSON
             *
             * @return JSON对象
             */
            public JSONObject toJSON() {
                return new JSONObject().put("user_id", userId)
                        .put("nickname", nickname)
                        .put("avatar", avatar)
                        .put("description", description);
            }
        }
    }

    /**
     * 获取 Cookies
     *
     * @param domain      需要获取 cookies 的域名
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return Cookies
     */
    APIResponse<String> getCookies(String domain, @Nullable APIRequestType requestType);

    /**
     * 获取 CSRF Token
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return CSRF Token
     */
    APIResponse<Integer> getCsrfToken(@Nullable APIRequestType requestType);

    /**
     * 获取 QQ 相关接口凭证
     * 即 {@link Bot#getCookies} 和 {@link Bot#getCsrfToken} 两个接口的合并。
     *
     * @param domain      需要获取 cookies 的域名
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return Cookies 和 CSRF Token
     */
    APIResponse<Credentials> getCredentials(String domain, @Nullable APIRequestType requestType);

    /**
     * QQ 相关接口凭证
     *
     * @param cookies   Cookies
     * @param csrfToken CSRF Token
     */
    record Credentials(String cookies, int csrfToken) {
        /**
         * 从 JSON 创建 QQ 相关接口凭证
         *
         * @param json JSON对象
         * @return QQ 相关接口凭证
         */
        public static Credentials fromJSON(JSONObject json) {
            return new Credentials(json.getString("cookies"), json.getInt("csrf_token"));
        }

        /**
         * 转换为 JSON
         *
         * @return JSON对象
         */
        public JSONObject toJSON() {
            return new JSONObject().put("cookies", cookies)
                    .put("csrf_token", csrfToken);
        }
    }

    /**
     * 获取语音
     *
     * @param file        收到的语音文件名（{@link MessageSegment#record}的 file 参数）
     * @param outFormat   要转换到的格式
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 转换后的语音文件路径
     */
    APIResponse<String> getRecord(String file, RecordOutFormat outFormat, @Nullable APIRequestType requestType);

    /**
     * 语音格式
     */
    enum RecordOutFormat {
        MP3("mp3"),
        AMR("amr"),
        WMA("wma"),
        M4A("m4a"),
        SPX("spx"),
        OGG("ogg"),
        WAV("wav"),
        FLAC("flac");

        final String value;

        RecordOutFormat(String value) {
            this.value = value;
        }

        /**
         * 获取语音格式字符串
         *
         * @return 语音格式字符串
         */
        public String getValue() {
            return value;
        }

        /**
         * 根据字符串获取语音格式
         *
         * @param value 语音格式字符串
         * @return 语音格式
         */
        public static RecordOutFormat getRecordOutFormat(String value) {
            for (RecordOutFormat format : values()) {
                if (format.getValue().equals(value)) {
                    return format;
                }
            }
            throw new IllegalArgumentException("未知音频格式");
        }
    }

    /**
     * 获取图片
     *
     * @param file        收到的图片文件名（{@link MessageSegment#image} 的 file 参数
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 下载后的图片文件路径
     */
    APIResponse<String> getImage(String file, @Nullable APIRequestType requestType);

    /**
     * 检查是否可以发送图片
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 是或否
     */
    APIResponse<Boolean> canSendImage(@Nullable APIRequestType requestType);

    /**
     * 检查是否可以发送语音
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 是或否
     */
    APIResponse<Boolean> canSendRecord(@Nullable APIRequestType requestType);

    /**
     * 获取运行状态
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 运行状态
     */
    APIResponse<BotStatus> getStatus(@Nullable APIRequestType requestType);

    /**
     * 获取版本信息
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是没有意义的
     * @return 版本信息
     */
    APIResponse<VersionInfo> getVersionInfo(@Nullable APIRequestType requestType);

    /**
     * 版本信息
     *
     * @param appName         应用标识，如 {@code mirai-native}
     * @param appVersion      应用版本，如 {@code 1.2.3}
     * @param protocolVersion 协议版本，如 {@code v11}
     * @param other           OneBot 实现自行添加的其它内容
     */
    record VersionInfo(String appName, String appVersion, String protocolVersion, JSONObject other) {
        /**
         * 从 JSONObject 中创建版本信息
         *
         * @param json JSON对象
         * @return 版本信息
         */
        public static VersionInfo fromJson(JSONObject json) {
            String appName = json.getString("app_name");
            String appVersion = json.getString("app_version");
            String protocolVersion = json.getString("protocol_version");
            json.remove("app_name");
            json.remove("app_version");
            json.remove("protocol_version");
            return new VersionInfo(appName, appVersion, protocolVersion, json);
        }

        /**
         * 转换为 JSONObject
         *
         * @return JSON对象
         */
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("app_name", appName);
            json.put("app_version", appVersion);
            json.put("protocol_version", protocolVersion);
            for (String key : other.keySet()) {
                json.put(key, other.get(key));
            }
            return json;
        }
    }

    /**
     * 重启 OneBot 实现
     * 由于重启 OneBot 实现同时需要重启 API 服务，这意味着当前的 API 请求会被中断，因此需要异步地重启，{@link APIResponse#status()} 返回的是 {@link APIResponse.Status#ASYNC}
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是不必要的
     * @return API返回数据
     */
    default APIResponse<Void> restart(@Nullable APIRequestType requestType) {
        return restart(0, requestType);
    }

    /**
     * 重启 OneBot 实现
     * 由于重启 OneBot 实现同时需要重启 API 服务，这意味着当前的 API 请求会被中断，因此需要异步地重启，{@link APIResponse#status()} 返回的是 {@link APIResponse.Status#ASYNC}
     *
     * @param delay       要延迟的毫秒数，如果默认情况下无法重启，可以尝试设置延迟为 2000 左右
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}，此处使用 {@link APIRequestType#ASYNC} 是不必要的
     * @return API返回数据
     */
    APIResponse<Void> restart(int delay, @Nullable APIRequestType requestType);

    /**
     * 清理缓存
     *
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @return API返回数据
     */
    APIResponse<Void> cleanCache(@Nullable APIRequestType requestType);

    /**
     * 调用自定义API
     *
     * @param action      自定义API名称
     * @param requestType 请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
     * @param params      自定义参数
     * @return API返回数据
     */
    APIResponse<JSONObject> customApi(String action, JSONObject params, @Nullable APIRequestType requestType);
}
