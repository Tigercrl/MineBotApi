package top.tigercrl.minebotapi.fabric;

import net.fabricmc.api.ModInitializer;
import top.tigercrl.minebotapi.MineBotApi;

public class MineBotApiFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MineBotApi.init();
    }
}