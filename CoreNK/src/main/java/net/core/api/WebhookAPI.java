package net.core.api;

import cn.nukkit.utils.Config;

public class WebhookAPI extends API{
    @Override
    public String getAuthor() {
        return "xxFLORII";
    }

    @Override
    public double getVersion() {
        return 1.0;
    }

    @Override
    public String getName() {
        return "WenhookAPI";
    }

    public static String getWebhookLink(String name){
        return (new Config("/home/debian/mcpe/.data/webhook.yml", Config.YAML)).getString(name, null);
    }


}
