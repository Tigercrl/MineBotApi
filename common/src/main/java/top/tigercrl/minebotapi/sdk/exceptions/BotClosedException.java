package top.tigercrl.minebotapi.sdk.exceptions;

import top.tigercrl.minebotapi.sdk.bot.Bot;

/**
 * 机器人已关闭但仍在请求API
 */
public class BotClosedException extends BotException {

    public BotClosedException(Bot bot) {
        super(bot, "机器人已关闭！");
    }
}
