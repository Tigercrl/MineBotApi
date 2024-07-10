package top.tigercrl.minebotapi.sdk.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import top.tigercrl.minebotapi.sdk.bot.Bot;

/**
 * MineBot API 提供的一些事件
 */
public interface BotLifecycleEvent {
    Event<BotMessage> BOT_MESSAGE = EventFactory.createLoop();
    Event<WebSocketStart> WEBSOCKET_START = EventFactory.createLoop();
    Event<WebSocketConnect> WEBSOCKET_CONNECT = EventFactory.createLoop();
    Event<WebSocketDisconnect> WEBSOCKET_DISCONNECT = EventFactory.createLoop();
    Event<WebSocketReverseStart> WEBSOCKET_REVERSE_START = EventFactory.createLoop();
    Event<WebSocketReverseConnect> WEBSOCKET_REVERSE_CONNECT = EventFactory.createLoop();
    Event<WebSocketReverseDisconnect> WEBSOCKET_REVERSE_DISCONNECT = EventFactory.createLoop();
    Event<WebSocketReverseStop> WEBSOCKET_REVERSE_STOP = EventFactory.createLoop();
    Event<HttpPostStart> HTTP_POST_START = EventFactory.createLoop();
    Event<HttpPostStop> HTTP_POST_STOP = EventFactory.createLoop();

    interface BotMessage {
        /**
         * 接收到消息
         *
         * @param bot     触发事件的机器人
         * @param message 消息内容
         */
        void message(Bot bot, JSONObject message);
    }

    interface WebSocketStart {
        /**
         * 正向WebSocket客户端启动
         *
         * @param bot 触发事件的机器人
         */
        void start(Bot bot);
    }

    interface WebSocketConnect {
        /**
         * 正向WebSocket连接到服务器
         *
         * @param bot           触发事件的机器人
         * @param handshakeData 握手数据
         */
        void connect(Bot bot, ServerHandshake handshakeData);
    }

    interface WebSocketDisconnect {
        /**
         * 正向WebSocket断开连接
         *
         * @param bot    触发事件的机器人
         * @param code   状态码
         * @param reason 原因
         * @param remote 是否由远程主机断开
         */
        void disconnect(Bot bot, int code, String reason, boolean remote);
    }

    interface WebSocketReverseStart {
        /**
         * 反向WebSocket服务器启动
         *
         * @param bot 触发事件的机器人
         */
        void start(Bot bot);
    }

    interface WebSocketReverseConnect {
        /**
         * 反向WebSocket客户端连接
         *
         * @param bot           触发事件的机器人
         * @param conn          连接的WebSocket
         * @param handshakeData 握手数据
         */
        void connect(Bot bot, WebSocket conn, ClientHandshake handshakeData);
    }

    interface WebSocketReverseDisconnect {
        /**
         * 反向WebSocket客户端断开连接
         *
         * @param bot    触发事件的机器人
         * @param conn   断开连接的WebSocket
         * @param code   状态码
         * @param reason 原因
         * @param remote 是否由远程主机断开
         */
        void disconnect(Bot bot, WebSocket conn, int code, String reason, boolean remote);
    }

    interface WebSocketReverseStop {
        /**
         * 反向WebSocket服务器关闭
         *
         * @param bot 触发事件的机器人
         */
        void stop(Bot bot);
    }

    interface HttpPostStart {
        /**
         * HTTP POST服务器启动
         *
         * @param bot 触发事件的机器人
         */
        void start(Bot bot);
    }

    interface HttpPostStop {
        /**
         * HTTP POST服务器关闭
         *
         * @param bot 触发事件的机器人
         */
        void stop(Bot bot);
    }
}
