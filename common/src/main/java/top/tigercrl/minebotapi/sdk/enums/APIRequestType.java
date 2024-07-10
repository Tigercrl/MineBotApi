package top.tigercrl.minebotapi.sdk.enums;

import top.tigercrl.minebotapi.sdk.records.APIResponse;

/**
 * API请求类型，为 {@code null} 默认为 {@link APIRequestType#NORMAL}
 */
public enum APIRequestType {
    /**
     * 普通请求
     */
    NORMAL(""),
    /**
     * 异步调用
     * 异步调用的响应中，{@link APIResponse#status()} 为 {@link APIResponse.Status#ASYNC}
     * 需要注意的是，虽然说以 getXxx 的那些接口也可以进行异步调用，但实际上客户端没有办法得知最终的调用结果，所以对这部分接口进行异步调用是没有意义的
     * 另外，有一些接口本身就是异步执行的（返回的 {@link APIResponse#status()} 为 {@link APIResponse.Status#ASYNC}），此时使用 {@code ASYNC} 来调用不会产生本质上的区别
     */
    ASYNC("_async"),
    /**
     * 限速调用
     * 所有 API 都可以进行限速调用，不过主要还是用在发送消息接口上，以避免消息频率过快导致腾讯封号
     * 所有限速调用将会以指定速度排队执行，这个速度可在OneBot配置中指定
     * 限速调用的响应中，{@link APIResponse#status()} 为 {@link APIResponse.Status#ASYNC}
     */
    RATE_LIMITED("_rate_limited");

    private final String suffix;

    APIRequestType(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获取API请求后缀
     *
     * @return API请求后缀
     */
    public String getSuffix() {
        return suffix;
    }
}
