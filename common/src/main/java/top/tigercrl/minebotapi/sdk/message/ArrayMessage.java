package top.tigercrl.minebotapi.sdk.message;

import org.json.JSONArray;

/**
 * 消息 - 数组格式
 *
 * @param messages 消息段数组
 */
public record ArrayMessage(MessageSegment... messages) implements Message {
    @Override
    public Object getMessage() {
        JSONArray jsonArray = new JSONArray();
        for (MessageSegment message : messages) {
            jsonArray.put(message.toJSON());
        }
        return jsonArray;
    }

    /**
     * 从 JSONArray 中创建数组格式消息
     *
     * @param array JSONArray
     */
    public static ArrayMessage fromJSON(JSONArray array) {
        MessageSegment[] segments = new MessageSegment[array.length()];
        for (int i = 0; i < array.length(); i++) {
            segments[i] = MessageSegment.fromJSON(array.getJSONObject(i));
        }
        return new ArrayMessage(segments);
    }

    /**
     * 转换为字符串消息格式
     *
     * @return 字符串消息
     */
    public StringMessage toStringMessage() {
        StringBuilder sb = new StringBuilder();
        for (MessageSegment segment : messages) {
            sb.append(segment.toCQCode());
        }
        return new StringMessage(sb.toString());
    }
}
