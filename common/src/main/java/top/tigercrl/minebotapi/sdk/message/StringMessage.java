package top.tigercrl.minebotapi.sdk.message;

import top.tigercrl.minebotapi.utils.EncodingUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息 - 字符串格式
 *
 * @param message 消息字符串
 */
public record StringMessage(String message) implements Message {
    @Override
    public Object getMessage() {
        return message;
    }

    /**
     * 转换为数组消息格式
     *
     * @return 数组消息
     */
    public ArrayMessage toArrayMessage() {
        List<MessageSegment> segments = new ArrayList<>();
        String[] messages = message.split("\\[CQ:|]");
        for (int i = 0; i < messages.length; i++) {
            String message = messages[i];
            if (i % 2 == 0) {
                if (!message.isEmpty())
                    segments.add(MessageSegment.text(EncodingUtils.decodeText(message)));
            } else {
                String[] keyValues = messages[i].split(",");
                JSONObject data = new JSONObject();
                for (int j = 1; j < keyValues.length; j++) {
                    String[] keyValue = keyValues[j].split("=");
                    data.put(keyValue[0], EncodingUtils.decodeCQCode(keyValue[1]));
                }
                segments.add(new MessageSegment(EncodingUtils.decodeCQCode(keyValues[0]), data));
            }
        }
        return new ArrayMessage(segments.toArray(new MessageSegment[0]));
    }
}
