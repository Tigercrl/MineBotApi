package top.tigercrl.minebotapi;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = MineBotApi.MOD_ID)
public class ModConfig implements ConfigData {
    public LogSettings logSettings = new LogSettings();
    public int apiRequestTimeout = 10000;
    public int heartbeatTimeout = 5000;

    public static class LogSettings {
        public boolean api = true;
        public boolean botConnection = true;
        public boolean event = true;
        public boolean heartbeat = false;
        public boolean message = false;
    }
}
