package top.tigercrl.minebotapi.sdk.bot;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import top.tigercrl.minebotapi.MineBotApi;
import top.tigercrl.minebotapi.sdk.enums.APIRequestType;
import top.tigercrl.minebotapi.sdk.enums.GroupRequestSubType;
import top.tigercrl.minebotapi.sdk.enums.HonorType;
import top.tigercrl.minebotapi.sdk.enums.MessageType;
import top.tigercrl.minebotapi.sdk.events.*;
import top.tigercrl.minebotapi.sdk.exceptions.ApiTimeoutException;
import top.tigercrl.minebotapi.sdk.exceptions.BotClosedException;
import top.tigercrl.minebotapi.sdk.exceptions.BotRequestException;
import top.tigercrl.minebotapi.sdk.exceptions.UnknownMessageException;
import top.tigercrl.minebotapi.sdk.message.ArrayMessage;
import top.tigercrl.minebotapi.sdk.message.Message;
import top.tigercrl.minebotapi.sdk.records.APIResponse;
import top.tigercrl.minebotapi.sdk.records.BotStatus;
import top.tigercrl.minebotapi.sdk.records.MessageInfo;
import top.tigercrl.minebotapi.sdk.records.MessageSenderAnonymous;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WSBot extends WebSocketClient implements Bot {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<UUID, JSONObject> messages = new HashMap<>();
    private long lastHeartbeat = 0;
    private long heartbeatInterval = 0;

    WSBot(String oneBotServerUrl) {
        super(URI.create(oneBotServerUrl));
        BotLifecycleEvent.WEBSOCKET_START.invoker().start(this);
        this.connect();
        if (MineBotApi.config.logSettings.botConnection)
            LOGGER.info("正向WebSocket机器人已被创建，服务器URL：{}，机器人UUID：{}", oneBotServerUrl, uuid);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        BotLifecycleEvent.WEBSOCKET_CONNECT.invoker().connect(this, handshakeData);
        if (MineBotApi.config.logSettings.botConnection) LOGGER.info("机器人已连接到服务器，机器人UUID：{}", uuid);
        TickEvent.SERVER_POST.register(this::tick);
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        // 机器人消息事件
        BotLifecycleEvent.BOT_MESSAGE.invoker().message(this, json);
        if (MineBotApi.config.logSettings.message) LOGGER.info("收到服务器消息，机器人UUID：{}，消息内容：{}", uuid, json);

        if (json.has("echo") && json.get("echo") != JSONObject.NULL) { // API调用
            messages.put(UUID.fromString(json.getString("echo")), json);
        } else if (json.has("post_type")) { // 机器人事件
            switch (json.getString("post_type")) {
                case "message": // 消息事件
                    switch (json.getString("message_type")) {
                        case "private":
                            MessageEvent.PRIVATE_CHAT.invoker().privateChat(this, MessageInfo.fromJSON(json));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发私聊消息事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group":
                            MessageEvent.GROUP_CHAT.invoker().groupChat(this, MessageInfo.fromJSON(json));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群聊消息事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                    }
                    break;
                case "meta_event": // 元事件
                    switch (json.getString("meta_event_type")) {
                        case "lifecycle":
                            MetaEvent.LIFECYCLE.invoker().lifecycle(this, json.getLong("time"), json.getLong("self_id"), MetaEvent.Lifecycle.SubType.getSubType(json.getString("sub_type")));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发生命周期事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "heartbeat":
                            lastHeartbeat = System.currentTimeMillis();
                            heartbeatInterval = json.getLong("interval");
                            if (MineBotApi.config.logSettings.heartbeat)
                                LOGGER.info("机器人收到心跳，机器人UUID：{}，事件信息：{}", uuid, json);
                            MetaEvent.HEARTBEAT.invoker().heartbeat(this, json.getLong("time"), json.getLong("self_id"), BotStatus.fromJson(json.getJSONObject("status")), json.getLong("interval"));
                            break;
                    }
                    break;
                case "notice": // 通知事件
                    switch (json.getString("notice_type")) {
                        case "group_upload":
                            NoticeEvent.GROUP_UPLOAD.invoker().groupUpload(this, json.getLong("time"), json.getLong("self_id"), json.getLong("group_id"), json.getLong("user_id"), NoticeEvent.GroupUpload.FileInfo.fromJson(json.getJSONObject("file")));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群文件上传事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group_admin":
                            NoticeEvent.GROUP_ADMIN.invoker().groupAdmin(this, json.getLong("time"), json.getLong("self_id"), NoticeEvent.GroupAdmin.SubType.getSubType(json.getString("sub_type")), json.getLong("group_id"), json.getLong("user_id"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群管理员变动事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group_decrease":
                            NoticeEvent.GROUP_DECREASE.invoker().groupDecrease(this, json.getLong("time"), json.getLong("self_id"), NoticeEvent.GroupDecrease.SubType.getSubType(json.getString("sub_type")), json.getLong("group_id"), json.getLong("operator_id"), json.getLong("user_id"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群成员减少事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group_increase":
                            NoticeEvent.GROUP_INCREASE.invoker().groupIncrease(this, json.getLong("time"), json.getLong("self_id"), NoticeEvent.GroupIncrease.SubType.getSubType(json.getString("sub_type")), json.getLong("group_id"), json.getLong("operator_id"), json.getLong("user_id"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群成员增加事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group_ban":
                            NoticeEvent.GROUP_MUTE.invoker().groupMute(this, json.getLong("time"), json.getLong("self_id"), NoticeEvent.GroupMute.SubType.getSubType(json.getString("sub_type")), json.getLong("group_id"), json.getLong("operator_id"), json.getLong("user_id"), json.getLong("duration"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群禁言事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "friend_add":
                            NoticeEvent.FRIEND_ADD.invoker().friendAdd(this, json.getLong("time"), json.getLong("self_id"), json.getLong("user_id"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发好友添加事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group_recall":
                            NoticeEvent.GROUP_RECALL.invoker().groupRecall(this, json.getLong("time"), json.getLong("self_id"), json.getLong("group_id"), json.getLong("user_id"), json.getLong("operator_id"), json.getLong("message_id"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发群消息撤回事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "friend_recall":
                            NoticeEvent.FRIEND_RECALL.invoker().friendRecall(this, json.getLong("time"), json.getLong("self_id"), json.getLong("user_id"), json.getLong("message_id"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发好友消息撤回事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "notify":
                            switch (json.getString("sub_type")) {
                                case "poke":
                                    NoticeEvent.SHAKE.invoker().shake(this, json.getLong("time"), json.getLong("self_id"), json.getLong("group_id"), json.getLong("user_id"), json.getLong("target_id"));
                                    if (MineBotApi.config.logSettings.event)
                                        LOGGER.info("机器人触发戳一戳事件，机器人UUID：{}，事件信息：{}", uuid, json);
                                    break;
                                case "lucky_king":
                                    NoticeEvent.LUCKY_KING.invoker().luckyKing(this, json.getLong("time"), json.getLong("self_id"), json.getLong("group_id"), json.getLong("user_id"), json.getLong("target_id"));
                                    if (MineBotApi.config.logSettings.event)
                                        LOGGER.info("机器人触发群红包运气王事件，机器人UUID：{}，事件信息：{}", uuid, json);
                                    break;
                                case "honor":
                                    NoticeEvent.HONOR.invoker().honor(this, json.getLong("time"), json.getLong("self_id"), json.getLong("group_id"), HonorType.getHonorType(json.getString("honor_type")), json.getLong("user_id"));
                                    if (MineBotApi.config.logSettings.event)
                                        LOGGER.info("机器人触发群荣誉事件，机器人UUID：{}，事件信息：{}", uuid, json);
                                    break;
                            }
                            break;
                    }
                    break;
                case "request":
                    switch (json.getString("request_type")) {
                        case "friend":
                            RequestEvent.FRIEND.invoker().friend(this, json.getLong("time"), json.getLong("self_id"), json.getLong("user_id"), json.getString("comment"), json.getString("flag"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发加好友请求事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                        case "group":
                            RequestEvent.GROUP.invoker().group(this, json.getLong("time"), json.getLong("self_id"), GroupRequestSubType.getSubType(json.getString("sub_type")), json.getLong("group_id"), json.getLong("user_id"), json.getString("comment"), json.getString("flag"));
                            if (MineBotApi.config.logSettings.event)
                                LOGGER.info("机器人触发加群请求事件，机器人UUID：{}，事件信息：{}", uuid, json);
                            break;
                    }
            }
        } else {
            throw new UnknownMessageException(this, message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        BotLifecycleEvent.WEBSOCKET_DISCONNECT.invoker().disconnect(this, code, reason, remote);
        TickEvent.SERVER_POST.unregister(this::tick);
        if (MineBotApi.config.logSettings.botConnection) LOGGER.info("机器人已断开服务器，机器人UUID：{}", uuid);
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.info("机器人发生错误！机器人UUID：{}", uuid);
        ex.printStackTrace();
    }

    private void tick(MinecraftServer server) {
        if (heartbeatInterval != 0 && lastHeartbeat != 0) {
            long timeout = heartbeatInterval + MineBotApi.config.heartbeatTimeout;
            long interval = System.currentTimeMillis() - lastHeartbeat;
            if (interval - timeout > 0 && (interval - timeout) % 5000 < 50) {
                LOGGER.warn("机器人心跳超时！机器人是否发生了异常？机器人UUID：{}", uuid);
            }
        }
    }

    private JSONObject sendToApi(String action, JSONObject params, @Nullable APIRequestType requestType) {
        UUID messageUUID = UUID.randomUUID();
        action += requestType == null ? "" : requestType.getSuffix();
        if (MineBotApi.config.logSettings.api)
            LOGGER.info("机器人发送API请求，机器人UUID：{}，API：{}，参数：{}，请求UUID：{}", uuid, action, params, messageUUID);
        while (true) {
            if (this.isClosed()) throw new BotClosedException(this);
            else if (this.isOpen()) break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        send(new JSONObject().put("action", action).put("params", params).put("echo", messageUUID.toString()).toString());
        long requestTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - requestTime < MineBotApi.config.apiRequestTimeout) {
            if (messages.containsKey(messageUUID)) {
                JSONObject message = messages.get(messageUUID);
                int retcode = message.getInt("retcode");
                if (retcode == 0) {
                    return messages.get(messageUUID);
                } else {
                    throw new BotRequestException(this, message.getString("message"), message.getString("wording"), retcode);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        throw new ApiTimeoutException(this);
    }

    @Override
    public APIResponse<Integer> sendPrivateMsg(long userId, @NotNull Message message, boolean autoEscape, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("send_private_msg", new JSONObject().put("user_id", userId).put("message", message.getMessage()).put("autoEscape", autoEscape), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getInt("message_id"));
    }

    @Override
    public APIResponse<Integer> sendGroupMsg(long groupId, @NotNull Message message, boolean autoEscape, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("send_group_msg", new JSONObject().put("user_id", groupId).put("message", message.getMessage()).put("autoEscape", autoEscape), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getInt("message_id"));
    }

    @Override
    public APIResponse<Integer> sendMsg(@Nullable MessageType messageType, long userId, long groupId, @NotNull Message message, boolean autoEscape, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("send_msg", new JSONObject().put("message_type", messageType == null ? null : messageType.getValue()).put("user_id", userId).put("group_id", groupId).put("message", message.getMessage()).put("autoEscape", autoEscape), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getInt("message_id"));
    }

    @Override
    public APIResponse<Void> recallMsg(int messageId, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("send_msg", new JSONObject().put("message_id", messageId), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<MessageInfo> getMsg(int messageId, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_msg", new JSONObject().put("message_id", messageId), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), MessageInfo.fromJSON(response.getJSONObject("data")));
    }

    @Override
    public APIResponse<ArrayMessage> getForwardMsg(@NotNull String id, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_forward_msg", new JSONObject().put("id", id), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), ArrayMessage.fromJSON(responseData.getJSONArray("messages")));
    }

    @Override
    public APIResponse<Void> sendLike(long userId, int times, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("send_like", new JSONObject().put("user_id", userId).put("times", times), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupKick(long groupId, long userId, boolean rejectAddRequest, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_kick", new JSONObject().put("group_id", groupId).put("user_id", userId).put("reject_add_request", rejectAddRequest), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupMute(long groupId, long userId, long duration, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_ban", new JSONObject().put("group_id", groupId).put("user_id", userId).put("duration", duration), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupAnonymousMute(long groupId, @Nullable MessageSenderAnonymous anonymous, @Nullable String flag, long duration, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_anonymous_ban", new JSONObject().put("group_id", groupId).put("anonymous", anonymous == null ? null : anonymous.toJSON()).put("anonymous_flag", flag).put("duration", duration), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupWholeMute(long groupId, boolean enable, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_whole_ban", new JSONObject().put("group_id", groupId).put("enable", enable), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupAdmin(long groupId, long userId, boolean enable, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_admin", new JSONObject().put("group_id", groupId).put("user_id", userId).put("enable", enable), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupAnonymous(long groupId, boolean enable, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_anonymous", new JSONObject().put("group_id", groupId).put("enable", enable), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupCard(long groupId, long userId, @Nullable String card, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_card", new JSONObject().put("group_id", groupId).put("user_id", userId).put("card", card), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupName(long groupId, @NotNull String groupName, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_name", new JSONObject().put("group_id", groupId).put("group_name", groupName), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupLeave(long groupId, boolean isDismiss, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_card", new JSONObject().put("group_id", groupId).put("is_dismiss", isDismiss), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupSpecialTitle(long groupId, long userId, @Nullable String specialTitle, long duration, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_special_title", new JSONObject().put("group_id", groupId).put("user_id", userId).put("special_title", specialTitle).put("duration", duration), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setFriendAddRequest(String flag, boolean approve, @Nullable String remark, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_friend_add_request", new JSONObject().put("flag", flag).put("approve", approve).put("remark", remark), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> setGroupAddRequest(String flag, GroupRequestSubType subType, boolean approve, @Nullable String reason, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_add_request", new JSONObject().put("flag", flag).put("sub_type", subType.getValue()).put("approve", approve).put("reason", reason), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<LoginInfo> getLoginInfo(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_login_info", new JSONObject(), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), LoginInfo.fromJSON(responseData));
    }

    @Override
    public APIResponse<StrangerInfo> getStrangerInfo(long userId, boolean noCache, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_stranger_info", new JSONObject().put("user_id", userId).put("no_cache", noCache), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), StrangerInfo.fromJSON(responseData));
    }

    @Override
    public APIResponse<FriendInfo[]> getFriendList(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_friend_list", new JSONObject(), requestType);
        JSONArray responseData = response.getJSONArray("data");
        FriendInfo[] friends = new FriendInfo[responseData.length()];
        for (int i = 0; i < responseData.length(); i++) {
            friends[i] = FriendInfo.fromJSON(responseData.getJSONObject(i));
        }
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), friends);
    }

    @Override
    public APIResponse<GroupInfo> getGroupInfo(long groupId, boolean noCache, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_group_add_request", new JSONObject().put("group_id", groupId).put("no_cache", noCache), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), GroupInfo.fromJSON(responseData));
    }

    @Override
    public APIResponse<GroupMemberInfo> getGroupMemberInfo(long groupId, long userId, boolean noCache, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_group_member_info", new JSONObject().put("group_id", groupId).put("user_id", userId).put("no_cache", noCache), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), GroupMemberInfo.fromJSON(responseData));
    }

    @Override
    public APIResponse<GroupMemberInfo[]> getGroupMemberList(long groupId, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_group_member_list", new JSONObject().put("group_id", groupId), requestType);
        JSONArray responseData = response.getJSONArray("data");
        GroupMemberInfo[] groupMemberList = new GroupMemberInfo[responseData.length()];
        for (int i = 0; i < responseData.length(); i++) {
            groupMemberList[i] = GroupMemberInfo.fromJSON(responseData.getJSONObject(i));
        }
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), groupMemberList);
    }

    @Override
    public APIResponse<GroupHonorInfo> getGroupHonorInfo(long groupId, HonorType type, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_group_honor_info", new JSONObject().put("group_id", groupId).put("type", type.getValue()), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), GroupHonorInfo.fromJSON(responseData));
    }

    @Override
    public APIResponse<String> getCookies(String domain, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_cookies", new JSONObject().put("domain", domain), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getString("cookies"));
    }

    @Override
    public APIResponse<Integer> getCsrfToken(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_csrf_token", new JSONObject(), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getInt("token"));
    }

    @Override
    public APIResponse<Credentials> getCredentials(String domain, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_credentials", new JSONObject().put("domain", domain), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), Credentials.fromJSON(responseData));
    }

    @Override
    public APIResponse<String> getRecord(String file, RecordOutFormat outFormat, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_record", new JSONObject().put("file", file).put("out_format", outFormat), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getString("file"));
    }

    @Override
    public APIResponse<String> getImage(String file, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_image", new JSONObject().put("file", file), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getString("file"));
    }

    @Override
    public APIResponse<Boolean> canSendImage(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("can_send_image", new JSONObject(), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getBoolean("yes"));
    }

    @Override
    public APIResponse<Boolean> canSendRecord(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("can_send_record", new JSONObject(), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), responseData.getBoolean("yes"));
    }

    @Override
    public APIResponse<BotStatus> getStatus(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_status", new JSONObject(), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), BotStatus.fromJson(responseData));
    }

    @Override
    public APIResponse<VersionInfo> getVersionInfo(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("get_version_info", new JSONObject(), requestType);
        JSONObject responseData = response.getJSONObject("data");
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), VersionInfo.fromJson(responseData));
    }

    @Override
    public APIResponse<Void> restart(int delay, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("set_restart", new JSONObject().put("delay", delay), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<Void> cleanCache(@Nullable APIRequestType requestType) {
        JSONObject response = sendToApi("clean_cache", new JSONObject(), requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), null);
    }

    @Override
    public APIResponse<JSONObject> customApi(String action, JSONObject params, @Nullable APIRequestType requestType) {
        JSONObject response = sendToApi(action, params, requestType);
        return new APIResponse<>(APIResponse.Status.getStatus(response.getString("status")), response.getInt("retcode"), response.getString("message"), response.getString("wording"), response.getJSONObject("data"));
    }
}
