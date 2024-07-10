package top.tigercrl.minebotapi;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class MineBotApi {
    public static final String MOD_ID = "minebotapi";
    public static ModConfig config;

    public static void init() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
