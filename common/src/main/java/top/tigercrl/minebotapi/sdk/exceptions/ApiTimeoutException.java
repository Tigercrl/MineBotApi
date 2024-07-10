package top.tigercrl.minebotapi.sdk.exceptions;

import top.tigercrl.minebotapi.sdk.bot.Bot;

/**
 * HTTP通信方式（HTTP POST）中签名不正确时抛出此异常
 */
public class ApiTimeoutException extends BotException {
    public ApiTimeoutException(Bot bot) {
        super(bot, "API响应超时！");
    }
}
