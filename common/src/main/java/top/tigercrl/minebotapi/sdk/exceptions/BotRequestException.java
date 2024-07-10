package top.tigercrl.minebotapi.sdk.exceptions;

import top.tigercrl.minebotapi.sdk.bot.Bot;

/**
 * 机器人在通信中遇到了 HTTP非 {@code 200} 状态码 或 WebSocket 非 {@code 1200} retcode
 */
public class BotRequestException extends BotException {
    private final String wording;
    private final int code;

    public BotRequestException(Bot bot, String message, String wording, int code) {
        super(bot, message);
        this.wording = wording;
        this.code = code;
    }

    /**
     * 获取响应码含义
     *
     * @return 响应码含义
     */
    public String getWording() {
        return wording;
    }

    /**
     * 获取响应码(HTTP - 状态码 / WebSocket - retcode)
     *
     * @return 响应码
     */
    public int getCode() {
        return code;
    }
}
