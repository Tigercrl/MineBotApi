package top.tigercrl.minebotapi.sdk.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import org.json.JSONObject;
import top.tigercrl.minebotapi.sdk.bot.Bot;
import top.tigercrl.minebotapi.sdk.enums.HonorType;

/**
 * 通知事件，见<a href="https://github.com/botuniverse/onebot-11/blob/master/event/notice.md">OneBot - 通知事件</a>
 */
public interface NoticeEvent {
    Event<GroupUpload> GROUP_UPLOAD = EventFactory.createLoop();
    Event<GroupAdmin> GROUP_ADMIN = EventFactory.createLoop();
    Event<GroupDecrease> GROUP_DECREASE = EventFactory.createLoop();
    Event<GroupIncrease> GROUP_INCREASE = EventFactory.createLoop();
    Event<GroupMute> GROUP_MUTE = EventFactory.createLoop();
    Event<FriendAdd> FRIEND_ADD = EventFactory.createLoop();
    Event<GroupRecall> GROUP_RECALL = EventFactory.createLoop();
    Event<FriendRecall> FRIEND_RECALL = EventFactory.createLoop();
    Event<Shake> SHAKE = EventFactory.createLoop();
    Event<LuckyKing> LUCKY_KING = EventFactory.createLoop();
    Event<Honor> HONOR = EventFactory.createLoop();

    interface GroupUpload {
        /**
         * 群文件上传
         *
         * @param bot     触发事件的机器人
         * @param time    事件发生的时间戳
         * @param selfId  收到事件的机器人 QQ 号
         * @param groupId 群号
         * @param userId  发送者 QQ 号
         * @param file    文件信息
         */
        void groupUpload(Bot bot, long time, long selfId, long groupId, long userId, FileInfo file);

        /**
         * 文件信息
         *
         * @param id    文件 ID
         * @param name  文件名
         * @param size  文件大小（字节数）
         * @param busId 目前不清楚有什么作用
         */
        record FileInfo(String id, String name, long size, long busId) {

            /**
             * 从 JSON 创建文件信息
             *
             * @param json JSON对象
             * @return 文件信息
             */
            public static FileInfo fromJson(JSONObject json) {
                return new FileInfo(json.getString("id"), json.getString("name"), json.getLong("size"), json.getLong("busid"));
            }

            /**
             * 转换为 JSON
             *
             * @return JSON对象
             */
            public JSONObject toJson() {
                return new JSONObject()
                        .put("id", id)
                        .put("name", name)
                        .put("size", size)
                        .put("busid", busId);
            }
        }
    }

    interface GroupAdmin {
        /**
         * 群管理员变动
         *
         * @param bot     触发事件的机器人
         * @param time    事件发生的时间戳
         * @param selfId  收到事件的机器人 QQ 号
         * @param subType 事件子类型
         * @param groupId 群号
         * @param userId  管理员 QQ 号
         */
        void groupAdmin(Bot bot, long time, long selfId, SubType subType, long groupId, long userId);

        /**
         * 事件子类型
         */
        enum SubType {
            /**
             * 设置管理员
             */
            SET("set"),
            /**
             * 取消管理员
             */
            UNSET("unset");

            final String value;

            SubType(String value) {
                this.value = value;
            }

            /**
             * 获取事件子类型字符串
             *
             * @return 事件子类型字符串
             */
            String getValue() {
                return value;
            }

            /**
             * 根据字符串获取事件子类型
             *
             * @param value 事件子类型字符串
             * @return 事件子类型
             */
            public static SubType getSubType(String value) {
                return switch (value) {
                    case "set" -> SET;
                    case "unset" -> UNSET;
                    default -> throw new IllegalArgumentException("未知事件子类型");
                };
            }
        }
    }

    interface GroupDecrease {
        /**
         * 群成员增加
         *
         * @param bot        触发事件的机器人
         * @param time       事件发生的时间戳
         * @param selfId     收到事件的机器人 QQ 号
         * @param subType    事件子类型
         * @param groupId    群号
         * @param operatorId 操作者 QQ 号（如果是主动退群，则和 {@code userId} 相同）
         * @param userId     离开者 QQ 号
         */
        void groupDecrease(Bot bot, long time, long selfId, SubType subType, long groupId, long operatorId, long userId);

        /**
         * 事件子类型
         */
        enum SubType {
            /**
             * 主动退群
             */
            LEAVE("leave"),
            /**
             * 成员被踢
             */
            KICK("kick"),
            /**
             * 机器人被踢
             */
            KICK_SELF("kick_me");

            final String value;

            SubType(String value) {
                this.value = value;
            }

            /**
             * 获取事件子类型字符串
             *
             * @return 事件子类型字符串
             */
            String getValue() {
                return value;
            }

            /**
             * 根据字符串获取事件子类型
             *
             * @param value 事件子类型字符串
             * @return 事件子类型
             */
            public static SubType getSubType(String value) {
                return switch (value) {
                    case "kick" -> KICK;
                    case "kick_me" -> KICK_SELF;
                    case "leave" -> LEAVE;
                    default -> throw new IllegalArgumentException("未知事件子类型");
                };
            }
        }
    }

    interface GroupIncrease {
        /**
         * @param bot        触发事件的机器人
         * @param time       事件发生的时间戳
         * @param selfId     收到事件的机器人 QQ 号
         * @param subType    事件子类型
         * @param groupId    群号
         * @param operatorId 操作者 QQ 号
         * @param userId     加入者 QQ 号
         */
        void groupIncrease(Bot bot, long time, long selfId, SubType subType, long groupId, long operatorId, long userId);

        /**
         * 事件子类型
         */
        enum SubType {
            /**
             * 管理员已同意入群
             */
            APPROVE("approve"),
            /**
             * 管理员邀请入群
             */
            INVITE("invite");

            final String value;

            SubType(String value) {
                this.value = value;
            }

            /**
             * 获取事件子类型字符串
             *
             * @return 事件子类型字符串
             */
            String getValue() {
                return value;
            }

            /**
             * 根据字符串获取事件子类型
             *
             * @param value 事件子类型字符串
             * @return 事件子类型
             */
            public static SubType getSubType(String value) {
                return switch (value) {
                    case "approve" -> APPROVE;
                    case "invite" -> INVITE;
                    default -> throw new IllegalArgumentException("未知事件子类型");
                };
            }
        }
    }

    interface GroupMute {
        /**
         * 群禁言
         *
         * @param bot        触发事件的机器人
         * @param time       事件发生的时间戳
         * @param selfId     收到事件的机器人 QQ 号
         * @param subType    事件子类型
         * @param groupId    群号
         * @param operatorId 操作者 QQ 号
         * @param userId     被禁言 QQ 号
         * @param duration   禁言时长，单位秒
         */
        void groupMute(Bot bot, long time, long selfId, SubType subType, long groupId, long operatorId, long userId, long duration);

        /**
         * 事件子类型
         */
        enum SubType {
            /**
             * 禁言
             */
            MUTE("mute"),
            /**
             * 解除禁言
             */
            UNMUTE("unmute");

            final String value;

            SubType(String value) {
                this.value = value;
            }

            /**
             * 获取事件子类型字符串
             *
             * @return 事件子类型字符串
             */
            String getValue() {
                return value;
            }

            /**
             * 根据字符串获取事件子类型
             *
             * @param value 事件子类型字符串
             * @return 事件子类型
             */
            public static SubType getSubType(String value) {
                return switch (value) {
                    case "mute" -> MUTE;
                    case "unmute" -> UNMUTE;
                    default -> throw new IllegalArgumentException("未知事件子类型");
                };
            }
        }
    }

    interface FriendAdd {
        /**
         * 好友添加
         *
         * @param bot    触发事件的机器人
         * @param time   事件发生的时间戳
         * @param selfId 收到事件的机器人 QQ 号
         * @param userId 新添加好友 QQ 号
         */
        void friendAdd(Bot bot, long time, long selfId, long userId);
    }

    interface GroupRecall {
        /**
         * 群消息撤回
         *
         * @param bot        触发事件的机器人
         * @param time       事件发生的时间戳
         * @param selfId     收到事件的机器人 QQ 号
         * @param groupId    群号
         * @param userId     消息发送者 QQ 号
         * @param operatorId 操作者 QQ 号
         * @param messageId  被撤回的消息 ID
         */
        void groupRecall(Bot bot, long time, long selfId, long groupId, long userId, long operatorId, long messageId);
    }

    interface FriendRecall {
        /**
         * 好友消息撤回
         *
         * @param bot       触发事件的机器人
         * @param time      事件发生的时间戳
         * @param selfId    收到事件的机器人 QQ 号
         * @param userId    好友 QQ 号
         * @param messageId 被撤回的消息 ID
         */
        void friendRecall(Bot bot, long time, long selfId, long userId, long messageId);
    }

    interface Shake {
        /**
         * 群内戳一戳
         *
         * @param bot      触发事件的机器人
         * @param time     事件发生的时间戳
         * @param selfId   收到事件的机器人 QQ 号
         * @param groupId  群号
         * @param userId   发起戳一戳 QQ 号
         * @param targetId 被戳者 QQ 号
         */
        void shake(Bot bot, long time, long selfId, long groupId, long userId, long targetId);
    }

    interface LuckyKing {
        /**
         * 群红包运气王
         *
         * @param bot      触发事件的机器人
         * @param time     事件发生的时间戳
         * @param selfId   收到事件的机器人 QQ 号
         * @param groupId  群号
         * @param userId   红包发送者 QQ 号
         * @param targetId 运气王 QQ 号
         */
        void luckyKing(Bot bot, long time, long selfId, long groupId, long userId, long targetId);
    }

    interface Honor {
        /**
         * 群成员荣誉变更
         *
         * @param bot       触发事件的机器人
         * @param time      事件发生的时间戳
         * @param selfId    收到事件的机器人 QQ 号
         * @param groupId   群号
         * @param honorType 荣誉类型，仅可为 TALKATIVE（龙王）、PERFORMER（群聊之火）和EMOTION（快乐源泉）
         * @param userId    成员 QQ 号
         */
        void honor(Bot bot, long time, long selfId, long groupId, HonorType honorType, long userId);
    }
}
