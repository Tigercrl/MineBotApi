package top.tigercrl.minebotapi.sdk.exceptions;

import top.tigercrl.minebotapi.sdk.bot.Bot;
import org.jetbrains.annotations.Nullable;

/**
 * OneBot发送/响应了未知消息
 */
public class UnknownMessageException extends BotException {
    private final String response;

    public UnknownMessageException(Bot bot, @Nullable String response) {
        super(bot, "机器人发送/响应了未知消息：" + response);
        this.response = response;
    }

    /**
     * 获取OneBot消息
     *
     * @return OneBot消息
     */
    public String getResponse() {
        return response;
    }
}
