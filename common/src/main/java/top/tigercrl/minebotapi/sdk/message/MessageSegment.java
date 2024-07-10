package top.tigercrl.minebotapi.sdk.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import top.tigercrl.minebotapi.sdk.bot.Bot;
import top.tigercrl.minebotapi.sdk.enums.APIRequestType;
import top.tigercrl.minebotapi.sdk.records.MessageInfo;
import top.tigercrl.minebotapi.utils.EncodingUtils;

import java.util.Arrays;

/**
 * 消息段
 *
 * @param type 消息段类型
 * @param data 数据
 */
public record MessageSegment(String type, JSONObject data) {
    /**
     * 转换为 JSONObject
     *
     * @return JSON对象
     */
    public JSONObject toJSON() {
        return new JSONObject().put("type", type).put("data", data);
    }

    /**
     * 从 JSONObject 中创建消息段
     *
     * @param json JSON对象
     * @return 消息段
     */
    public static MessageSegment fromJSON(@NotNull JSONObject json) {
        return new MessageSegment(json.getString("type"), json.getJSONObject("data"));
    }

    /**
     * 转换为CQ码格式
     *
     * @return CQ码
     */
    public String toCQCode() {
        if (type.equals("text")) {
            return EncodingUtils.encodeText(data.getString("text"));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[CQ:").append(type);
            for (String key : data.keySet()) {
                sb.append(',').append(EncodingUtils.encodeCQCode(key)).append('=').append(EncodingUtils.encodeCQCode(data.getString(key)));
            }
            return sb.append(']').toString();
        }
    }

    /**
     * 消息段 - 纯文本，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E7%BA%AF%E6%96%87%E6%9C%AC">纯文本</a>
     *
     * @param text 纯文本内容<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment text(@NotNull String text) {
        return new MessageSegment("text", new JSONObject().put("text", text));
    }

    /**
     * 消息段 - QQ表情，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#qq-%E8%A1%A8%E6%83%85">QQ表情</a>
     *
     * @param id QQ表情ID，见<a href="https://github.com/kyubotics/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8">QQ表情ID表</a><i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment face(@NotNull String id) {
        return new MessageSegment("text", new JSONObject().put("id", id));
    }

    /**
     * 消息段 - 图片，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%9B%BE%E7%89%87">图片</a>
     *
     * @param file    图片文件名，发送时还可使用文件绝对路径、网站URL或Base64编码（见OneBot GitHub说明）<i>（收/发）</i>
     * @param type    图片类型，<i>（收/发）</i>
     * @param url     图片 URL<i>（收）</i>
     * @param cache   只在通过网络URL发送时有效，表示是否使用已缓存的文件，默认true<i>（发）</i>
     * @param proxy   只在通过网络URL发送时有效，表示是否通过代理下载文件（需通过环境变量或配置文件配置代理），默认true<i>（发）</i>
     * @param timeout 只在通过网络URL发送时有效，单位秒，表示下载网络文件的超时时间，小于等于0为不超时，默认不超时<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment image(@NotNull String file, @Nullable ImageType type, @Nullable String url, @Nullable Boolean cache, @Nullable Boolean proxy, @Nullable Integer timeout) {
        JSONObject data = new JSONObject();
        data.put("file", file);
        if (type == ImageType.FLASH) data.put("type", "flash");
        if (url != null) data.put("url", url);
        data.put("cache", Boolean.TRUE.equals(cache) ? 1 : 0);
        data.put("proxy", Boolean.TRUE.equals(proxy) ? 1 : 0);
        if (timeout != null && timeout > 0) data.put("timeout", timeout);
        return new MessageSegment("image", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - 语音，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E8%AF%AD%E9%9F%B3">语音</a>
     *
     * @param file    语音文件名，发送时还可使用文件绝对路径、网站URL或Base64编码（见OneBot GitHub说明）<i>（收/发）</i>
     * @param magic   发送时可选，设置为true表示变声<i>（收/发）</i>
     * @param url     语音 URL<i>（收）</i>
     * @param cache   只在通过网络URL发送时有效，表示是否使用已缓存的文件，默认true<i>（发）</i>
     * @param proxy   只在通过网络URL发送时有效，表示是否通过代理下载文件（需通过环境变量或配置文件配置代理），默认true<i>（发）</i>
     * @param timeout 只在通过网络URL发送时有效，单位秒，表示下载网络文件的超时时间，小于等于0为不超时，默认不超时<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment record(@NotNull String file, @NotNull Boolean magic, @Nullable String url, @Nullable Boolean cache, @Nullable Boolean proxy, @Nullable Integer timeout) {
        JSONObject data = new JSONObject();
        data.put("file", file);
        data.put("magic", magic ? 1 : 0);
        if (url != null) data.put("url", url);
        data.put("cache", Boolean.TRUE.equals(cache) ? 1 : 0);
        data.put("proxy", Boolean.TRUE.equals(proxy) ? 1 : 0);
        if (timeout != null && timeout > 0) data.put("timeout", timeout);
        return new MessageSegment("record", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - 短视频，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E7%9F%AD%E8%A7%86%E9%A2%91">短视频</a>
     *
     * @param file    视频文件名，发送时还可使用文件绝对路径、网站URL或Base64编码（见OneBot GitHub说明）<i>（收/发）</i>
     * @param url     视频 URL<i>（收）</i>
     * @param cache   只在通过网络URL发送时有效，表示是否使用已缓存的文件，默认true<i>（发）</i>
     * @param proxy   只在通过网络URL发送时有效，表示是否通过代理下载文件（需通过环境变量或配置文件配置代理），默认true<i>（发）</i>
     * @param timeout 只在通过网络URL发送时有效，单位秒，表示下载网络文件的超时时间，小于等于0为不超时，默认不超时<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment video(@NotNull String file, @Nullable String url, @Nullable Boolean cache, @Nullable Boolean proxy, @Nullable Integer timeout) {
        JSONObject data = new JSONObject();
        data.put("file", file);
        if (url != null) data.put("url", url);
        data.put("cache", Boolean.TRUE.equals(cache) ? 1 : 0);
        data.put("proxy", Boolean.TRUE.equals(proxy) ? 1 : 0);
        if (timeout != null && timeout > 0) data.put("timeout", timeout);
        return new MessageSegment("video", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - @某人，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%9F%90%E4%BA%BA">@某人</a>
     *
     * @param qq @的 QQ 号，或者为 all 表示全体成员<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment at(@NotNull String qq) {
        if (!qq.equals("all") && !qq.matches("^[1-9][0-9]{4,10}$")) {
            throw new IllegalArgumentException("的QQ号");
        }
        return new MessageSegment("at", new JSONObject().put("qq", qq));
    }

    /**
     * 消息段 - 猜拳魔法表情，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E7%8C%9C%E6%8B%B3%E9%AD%94%E6%B3%95%E8%A1%A8%E6%83%85">猜拳魔法表情</a>
     *
     * @return 消息段
     */
    public static MessageSegment rps() {
        return new MessageSegment("rps", new JSONObject());
    }

    /**
     * 消息段 - 掷骰子魔法表情，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%8E%B7%E9%AA%B0%E5%AD%90%E9%AD%94%E6%B3%95%E8%A1%A8%E6%83%85">掷骰子魔法表情</a>
     *
     * @return 消息段
     */
    public static MessageSegment dice() {
        return new MessageSegment("dice", new JSONObject());
    }

    /**
     * 消息段 - 窗口抖动（戳一戳），见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E7%AA%97%E5%8F%A3%E6%8A%96%E5%8A%A8%E6%88%B3%E4%B8%80%E6%88%B3-">窗口抖动（戳一戳）</a>
     *
     * @return 消息段
     */
    public static MessageSegment shake() {
        return new MessageSegment("shake", new JSONObject());
    }

    /**
     * 消息段 - 戳一戳，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%88%B3%E4%B8%80%E6%88%B3">戳一戳</a>
     *
     * @param poke 戳一戳表情<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment poke(@NotNull PokeType poke) {
        return new MessageSegment("poke", new JSONObject().put("type", poke.getType()).put("id", poke.getId()));
    }

    /**
     * 消息段 - 匿名发消息，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%8C%BF%E5%90%8D%E5%8F%91%E6%B6%88%E6%81%AF-badge-text%E5%8F%91">匿名消息</a>
     * 当收到匿名消息时，需要通过 {@link MessageInfo} 的 anonymous 字段判断
     *
     * @param ignore 表示无法匿名时是否继续发送，默认false<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment anonymous(@Nullable Boolean ignore) {
        return new MessageSegment("anonymous", new JSONObject().put("ignore", Boolean.TRUE.equals(ignore) ? 1 : 0));
    }

    /**
     * 消息段 - 链接分享，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E9%93%BE%E6%8E%A5%E5%88%86%E4%BA%AB">链接分享</a>
     *
     * @param url     URL<i>（收/发）</i>
     * @param title   标题<i>（收/发）</i>
     * @param content 发送时可选，内容描述<i>（收/发）</i>
     * @param image   发送时可选，图片 URL<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment share(@NotNull String url, @NotNull String title, @NotNull String content, @NotNull String image) {
        JSONObject data = new JSONObject();
        data.put("url", url);
        data.put("title", title);
        data.put("content", content);
        data.put("image", image);
        return new MessageSegment("share", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - 推荐群，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%8E%A8%E8%8D%90%E7%BE%A4">推荐群</a>
     *
     * @param id 群号<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment contact(@NotNull String id) {
        if (!id.matches("^[1-9][0-9]{4,10}$")) {
            throw new IllegalArgumentException("无效的群号");
        }
        return new MessageSegment("contact", new JSONObject().put("id", id));
    }

    /**
     * 消息段 - 位置，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E4%BD%8D%E7%BD%AE">位置</a>
     *
     * @param latitude  纬度<i>（收/发）</i>
     * @param longitude 经度<i>（收/发）</i>
     * @param title     发送时可选，标题<i>（收/发）</i>
     * @param content   发送时可选，内容描述<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment location(@NotNull String latitude, @NotNull String longitude, @Nullable String title, @Nullable String content) {
        JSONObject data = new JSONObject();
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        if (title != null) data.put("title", title);
        if (content != null) data.put("content", content);
        return new MessageSegment("location", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - 音乐分享，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E9%9F%B3%E4%B9%90%E5%88%86%E4%BA%AB-badge-text%E5%8F%91">音乐分享</a>
     *
     * @param type 音乐类型<i>（发）</i>
     * @param id   歌曲 ID<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment music(@NotNull MusicType type, @NotNull String id) {
        return new MessageSegment("music", new JSONObject().put("type", type.getValue()).put("id", id));
    }

    /**
     * 消息段 - 音乐自定义分享，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E9%9F%B3%E4%B9%90%E8%87%AA%E5%AE%9A%E4%B9%89%E5%88%86%E4%BA%AB-badge-text%E5%8F%91">音乐自定义分享</a>
     *
     * @param url     点击后跳转目标 URL<i>（发）</i>
     * @param audio   音乐 URL<i>（发）</i>
     * @param title   标题<i>（发）</i>
     * @param content 发送时可选，内容描述<i>（发）</i>
     * @param image   发送时可选，图片 URL<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment musicCustom(@NotNull String url, @NotNull String audio, @NotNull String title, @Nullable String content, @Nullable String image) {
        JSONObject data = new JSONObject();
        data.put("type", "custom");
        data.put("url", url);
        data.put("audio", audio);
        data.put("title", title);
        if (content != null) data.put("content", content);
        if (image != null) data.put("image", image);
        return new MessageSegment("music", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - 合并转发，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91-">合并转发</a>
     *
     * @param id 合并转发 ID，需通过 {@link Bot#getForwardMsg} API 获取具体内容<i>（收）</i>
     * @return 消息段
     */
    public static MessageSegment forward(@NotNull String id) {
        return new MessageSegment("forward", new JSONObject().put("id", id));
    }

    /**
     * 消息段 - 合并转发节点，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91%E8%8A%82%E7%82%B9-">合并转发节点</a>
     *
     * @param id 转发的消息 ID<i>（发）</i>
     * @return 消息段
     */
    public static MessageSegment node(@NotNull String id) {
        return new MessageSegment("node", new JSONObject().put("id", id));
    }

    /**
     * 消息段 - 合并转发自定义节点，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91%E8%87%AA%E5%AE%9A%E4%B9%89%E8%8A%82%E7%82%B9">合并转发自定义节点</a>
     * 接收时，此消息段不会直接出现在消息事件的 message 中，需通过 {@link Bot#getForwardMsg(String, APIRequestType)} API 获取。
     *
     * @param userId   发送者 QQ 号<i>（收/发）</i>
     * @param nickname 发送者昵称<i>（收/发）</i>
     * @param content  消息内容，支持发送消息时的 {@link Message} 数据类型，见 <a href="https://github.com/botuniverse/onebot-11/tree/master/api#%E5%8F%82%E6%95%B0">API 的参数</a><i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment nodeCustom(@NotNull String userId, @NotNull String nickname, @NotNull ArrayMessage[] content) {
        JSONObject data = new JSONObject();
        data.put("user_id", userId);
        data.put("nickname", nickname);
        JSONArray array = new JSONArray();
        for (Message message : content) {
            array.put(message.getMessage());
        }
        data.put("content", array);
        return new MessageSegment("node", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - XML 消息，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#xml-%E6%B6%88%E6%81%AF">XML 消息</a>
     *
     * @param data XML 内容<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment xml(@NotNull String data) {
        return new MessageSegment("xml", new JSONObject().put("data", data));
    }

    /**
     * 消息段 - JSON 消息，见<a href="https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#json-%E6%B6%88%E6%81%AF">JSON 消息</a>
     *
     * @param data JSON 内容<i>（收/发）</i>
     * @return 消息段
     */
    public static MessageSegment json(@NotNull String data) {
        return new MessageSegment("json", new JSONObject().put("data", data));
    }

    /**
     * 图片类型
     */
    public enum ImageType {
        /**
         * 普通图片
         */
        NORMAL("normal"),
        /**
         * 闪图
         */
        FLASH("flash");
        private final String value;

        ImageType(String value) {
            this.value = value;
        }

        /**
         * 获取图片类型字符串
         *
         * @return 图片类型字符串
         */
        public String getValue() {
            return value;
        }

        /**
         * 根据字符串获取图片类型
         *
         * @param value 图片类型字符串
         * @return 图片类型
         */
        public static ImageType getImageType(String value) {
            return value != null && value.equals("flash") ? FLASH : NORMAL;
        }
    }

    /**
     * 戳一戳表情
     */
    public enum PokeType {
        /**
         * 戳一戳
         */
        POKE("戳一戳", 1, -1),

        /**
         * 比心
         */
        SHOW_LOVE("比心", 2, -1),

        /**
         * 点赞
         */
        LOKE("点赞", 3, -1),

        /**
         * 心碎
         */
        HEART_BROKEN("心碎", 4, -1),

        /**
         * 666
         */
        SIX_SIX_SIX("666", 5, -1),

        /**
         * 放大招
         */
        FANG_DA_ZHAO("放大招", 6, -1),

        /**
         * 宝贝球 (SVIP)
         */
        BAO_BEI_QIU("宝贝球", 126, 2011),

        /**
         * 玫瑰花 (SVIP)
         */
        ROSE("玫瑰花", 126, 2007),

        /**
         * 召唤术 (SVIP)
         */
        ZHAO_HUAN_SHU("召唤术", 126, 2006),

        /**
         * 让你皮 (SVIP)
         */
        RANG_NI_PI("让你皮", 126, 2009),

        /**
         * 结印 (SVIP)
         */
        JIE_YIN("结印", 126, 2005),

        /**
         * 手雷 (SVIP)
         */
        SHOU_LEI("手雷", 126, 2004),

        /**
         * 勾引
         */
        GOU_YIN("勾引", 126, 2003),

        /**
         * 抓一下 (SVIP)
         */
        ZHUA_YI_XIA("抓一下", 126, 2001),

        /**
         * 碎屏 (SVIP)
         */
        SUI_PING("碎屏", 126, 2002),

        /**
         * 敲门 (SVIP)
         */
        QIAO_MEN("敲门", 126, 2002);
        private final String name;
        private final int type;
        private final int id;

        PokeType(String name, int type, int id) {
            this.name = name;
            this.type = type;
            this.id = id;
        }

        /**
         * 获取表情名称
         *
         * @return 名称
         */
        public String getName() {
            return name;
        }

        /**
         * 获取表情类型
         *
         * @return 类型
         */
        public int getType() {
            return type;
        }

        /**
         * 获取表情ID
         *
         * @return ID
         */
        public int getId() {
            return id;
        }

        /**
         * 根据类型和ID获取表情
         *
         * @param type 类型
         * @param id   ID
         * @return 表情
         */
        public static PokeType getPokeType(int type, int id) {
            return Arrays.stream(values()).filter(pokeType -> pokeType.getType() == type && pokeType.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("未知表情"));
        }

        /**
         * 根据名称获取表情
         *
         * @param name 名称
         * @return 表情
         */
        public static PokeType getPokeType(String name) {
            return Arrays.stream(values()).filter(pokeType -> pokeType.getName().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("未知表情"));
        }
    }

    /**
     * 音乐类型
     */
    public enum MusicType {
        QQMUSIC("qq"),
        WANG_YI("163"),
        XIAMI("xm");
        public final String value;

        MusicType(String value) {
            this.value = value;
        }

        /**
         * 根据字符串获取音乐类型
         *
         * @param type 音乐类型字符串
         * @return 音乐类型
         */
        public static MusicType getMusicType(String type) {
            return switch (type) {
                case "163" -> WANG_YI;
                case "xm" -> XIAMI;
                case "qq" -> QQMUSIC;
                default -> throw new IllegalArgumentException("未知音乐类型");
            };
        }

        /**
         * 获取音乐类型字符串
         *
         * @return 音乐类型字符串
         */
        public String getValue() {
            return value;
        }
    }
}
