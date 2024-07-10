package top.tigercrl.minebotapi.sdk.records;

import org.jetbrains.annotations.Nullable;

/**
 * API响应
 *
 * @param status  状态
 * @param retcode 响应码
 * @param message 响应消息
 * @param wording 响应码含义
 * @param data    数据
 * @param <E>     数据类型
 */
public record APIResponse<E>(Status status, int retcode, String message, String wording, @Nullable E data) {
    /**
     * 状态
     */
    public enum Status {
        SUCCESS("ok"),
        FAIL("failed"),
        ASYNC("async");
        final String value;

        Status(String value) {
            this.value = value;
        }

        /**
         * 获取状态字符串
         *
         * @return 状态字符串
         */
        public String getValue() {
            return value;
        }

        /**
         * 根据字符串获取状态
         *
         * @param value 状态字符串
         * @return 状态
         */
        public static Status getStatus(String value) {
            for (Status status : Status.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }
            return null;
        }
    }
}
