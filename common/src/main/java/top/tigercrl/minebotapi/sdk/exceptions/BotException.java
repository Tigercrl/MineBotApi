package top.tigercrl.minebotapi.sdk.exceptions;

import top.tigercrl.minebotapi.sdk.bot.Bot;

/**
 * OneBot机器人异常
 */
public class BotException extends RuntimeException {
    private final Bot bot;

    public BotException(Bot bot, String message) {
        super(message);
        this.bot = bot;
    }

    /**
     * 获取发生异常的机器人
     *
     * @return 机器人
     */
    public Bot getBot() {
        return bot;
    }
}
