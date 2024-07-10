package top.tigercrl.minebotapi.sdk.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import top.tigercrl.minebotapi.sdk.bot.Bot;
import top.tigercrl.minebotapi.sdk.enums.GroupRequestSubType;

/**
 * 请求事件，见<a href="https://github.com/botuniverse/onebot-11/blob/master/event/request.md">OneBot - 请求事件</a>
 */
public interface RequestEvent {
    Event<Friend> FRIEND = EventFactory.createLoop();
    Event<Group> GROUP = EventFactory.createLoop();

    interface Friend {
        /**
         * 加好友请求
         *
         * @param bot     触发事件的机器人
         * @param time    事件发生的时间戳
         * @param selfId  收到事件的机器人 QQ 号
         * @param comment 验证信息
         * @param flag    请求 flag，在调用 {@link Bot#setFriendAddRequest} API 时需要传入
         */
        void friend(Bot bot, long time, long selfId, long userId, String comment, String flag);
    }

    interface Group {
        /**
         * 加群请求／邀请
         *
         * @param bot     触发事件的机器人
         * @param time    事件发生的时间戳
         * @param selfId  收到事件的机器人 QQ 号
         * @param subType 请求子类型
         * @param groupId 群号
         * @param userId  发送请求的 QQ 号
         * @param comment 验证信息
         * @param flag    请求 flag，在调用 {@link Bot#setGroupAddRequest} API 时需要传入
         */
        void group(Bot bot, long time, long selfId, GroupRequestSubType subType, long groupId, long userId, String comment, String flag);
    }
}
