package top.tigercrl.minebotapi.sdk.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import top.tigercrl.minebotapi.sdk.bot.Bot;
import top.tigercrl.minebotapi.sdk.records.BotStatus;

/**
 * 元事件，见<a href="https://github.com/botuniverse/onebot-11/blob/master/event/meta.md">OneBot - 元事件</a>
 */
public interface MetaEvent {
    Event<Lifecycle> LIFECYCLE = EventFactory.createLoop();
    Event<Heartbeat> HEARTBEAT = EventFactory.createLoop();

    interface Lifecycle {
        /**
         * 生命周期
         *
         * @param bot     触发事件的机器人
         * @param time    事件发生的时间戳
         * @param selfId  收到事件的机器人 QQ 号
         * @param subType 事件子类型
         */
        void lifecycle(Bot bot, long time, long selfId, SubType subType);

        /**
         * 事件子类型
         */
        enum SubType {
            /**
             * OneBot 启用（仅限HTTP POST）
             */
            ENABLE("enable"),
            /**
             * OneBot 停用（仅限HTTP POST）
             */
            DISABLE("disable"),
            /**
             * WebSocket 连接成功（仅限正向/反向WebSocket）
             */
            CONNECT("connect");

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
                    case "enable" -> ENABLE;
                    case "disable" -> DISABLE;
                    case "connect" -> CONNECT;
                    default -> throw new IllegalArgumentException("未知事件子类型");
                };
            }
        }
    }

    interface Heartbeat {
        /**
         * 群消息
         *
         * @param bot      触发事件的机器人
         * @param time     事件发生的时间戳
         * @param selfId   收到事件的机器人 QQ 号
         * @param status   状态信息
         * @param interval 到下次心跳的间隔，单位毫秒
         */
        void heartbeat(Bot bot, long time, long selfId, BotStatus status, long interval);
    }
}
