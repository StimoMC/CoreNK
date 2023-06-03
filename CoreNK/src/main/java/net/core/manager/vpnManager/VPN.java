package net.core.manager.vpnManager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.core.api.WebhookAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;

public class VPN {

    public static double requestIPScore(String ip) {
        try {
            URL url = new URL("https://check.getipintel.net/check.php?ip=" + ip + "&contact=florian@stimomc.com");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return Double.parseDouble(content.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static void onVPN(String name, String address) {
        String url = WebhookAPI.getWebhookLink("vpn_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + " uses a VPN!", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Address:", address))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
