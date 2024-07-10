package top.tigercrl.minebotapi.sdk.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import top.tigercrl.minebotapi.sdk.bot.Bot;
import top.tigercrl.minebotapi.sdk.records.MessageInfo;

/**
 * 消息事件，见<a href="https://github.com/botuniverse/onebot-11/blob/master/event/message.md">OneBot - 消息事件</a>
 */
public interface MessageEvent {
    Event<PrivateChat> PRIVATE_CHAT = EventFactory.createLoop();
    Event<GroupChat> GROUP_CHAT = EventFactory.createLoop();

    interface PrivateChat {
        /**
         * 私聊消息
         *
         * @param bot         触发事件的机器人
         * @param messageInfo 消息
         */
        void privateChat(Bot bot, MessageInfo messageInfo);
    }

    interface GroupChat {
        /**
         * 群消息
         *
         * @param bot         触发事件的机器人
         * @param messageInfo 消息
         */
        void groupChat(Bot bot, MessageInfo messageInfo);
    }
}
