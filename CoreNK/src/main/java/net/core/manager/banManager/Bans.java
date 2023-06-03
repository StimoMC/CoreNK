package net.core.manager.banManager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import net.core.api.WebhookAPI;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;
import net.core.utils.TimeUnit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Bans {
    public static void create(){
        Database.createTables("CREATE TABLE IF NOT EXISTS bans(" +
                "name VARCHAR(50)," +
                "uuid VARCHAR(256)," +
                "ip VARCHAR(256)," +
                "bannedBy VARCHAR(256)," +
                "mutedBy VARCHAR(256)," +
                "bannedUntil VARCHAR(2048)," +
                "mutedUntil VARCHAR(2048)," +
                "banId VARCHAR(1024)," +
                "muteId VARCHAR(1024)," +
                "banCount  INTEGER DEFAULT 0," +
                "muteCount  INTEGER DEFAULT 0," +
                "PRIMARY KEY (name));");
    }

    public static Object get(final String select, final String database, final String where, final String where_result) {
        final ResultSet rs = Database.getResult("SELECT " + select + " FROM " + database + " WHERE " + where + " = '" + where_result + "';");
        try {
            assert rs != null;
            if (rs.next()) {
                return rs.getObject(select);
            }
        }
        catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
        return null;
    }

    public static void registerBanPlayer(Player player){
        CompletableFuture.runAsync(() -> {

            String address = ((CorePlayer) player).getOriginalAddress();
            String uuid = player.getUniqueId().toString();

            if (!Database.exists("bans", "name", player.getName())) {
                Database.update("INSERT INTO " + "bans" + " (name, uuid, ip, bannedBy, mutedBy, bannedUntil, mutedUntil, banId, muteId, banCount, muteCount) VALUES ('" + player.getName().toLowerCase() + "', '" + uuid + "', '" + address + "', 'null', 'null', '-1', '-1', '0', '0', '0', '0');");
            }
        });
    }

    public static Integer getBanTimeById(int id){
        return Integer.parseInt(String.valueOf((new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML)).getNested("reasons." + String.valueOf(id) + ".time")));
    }

    public static String getBanUnitById(int id){
        return String.valueOf((new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML)).getNested("reasons." + String.valueOf(id) + ".unit"));
    }

    public static String getBanTypeById(int id){
        return String.valueOf((new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML)).getNested("reasons." + String.valueOf(id) + ".type"));
    }

    public static String getBanReasonById(int id){
        return String.valueOf((new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML)).getNested("reasons." + String.valueOf(id) + ".reason"));
    }

    public static Boolean existsBanId(int id){
        return (new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML)).getNested("reasons." + String.valueOf(id)) != null;
    }

    public static Boolean getNextBanStronger(){
        return (new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML)).getBoolean("nextBanStronger");
    }

    public static List<Integer> getReasonList(){
        ArrayList<Integer> list = new ArrayList<>();
        Config banConfig = new Config("/home/debian/mcpe/.data/core/bans.yml", Config.YAML);
        ConfigSection keys = banConfig.getSection("reasons");
        List<Integer> keysInt = new ArrayList<>();
        for (String key : keys.getKeys()) {
            String[] parts = key.split("\\.");
            int keyInt = Integer.parseInt(parts[0]);
            if (!keysInt.contains(keyInt)) keysInt.add(keyInt);
        }

        return keysInt;
    }

    public static void banPlayer(String player, String bannedBy, int reason){
        long end = -1L;
        if (getBanTimeById(reason) != 0) {
            long seconds = getBanTimeById(reason);
            TimeUnit unit = TimeUnit.getByString(getBanUnitById(reason));
            if (unit != null) {
                seconds = seconds * unit.getSeconds();
                end = System.currentTimeMillis() + (seconds*1000);
                if (getBanTypeById(reason).equals("ban")) {
                    int banCount = Integer.parseInt(getBanCountRaw(player));
                    if (getNextBanStronger()){
                        banCount = Integer.parseInt(getBanCountRaw(player)) + 1;
                    }

                    end = end * banCount;
                    if (banCount < 4) {
                        onBan(player, bannedBy, getBanTimeById(reason), getBanUnitById(reason), getBanReasonById(reason));
                        Database.update("UPDATE bans SET `name` = '" + player + "', `bannedBy` = '" + bannedBy + "', `bannedUntil` = '" + end + "', `banId` = '" + reason + "', `banCount` = '" + banCount + "' WHERE `name` = '" + player + "'");
                    } else {
                        onBan(player, bannedBy, 0, getBanUnitById(reason), getBanReasonById(reason));
                        Database.update("UPDATE bans SET `name` = '" + player + "', `bannedBy` = '" + bannedBy + "', `bannedUntil` = '" + '0' + "', `banId` = '" + reason + "', `banCount` = '" + banCount + "' WHERE `name` = '" + player + "'");
                    }
                } else {
                    int muteCount = Integer.parseInt(getBanCountRaw(player));
                    if (getNextBanStronger()){
                        muteCount = Integer.parseInt(getBanCountRaw(player)) + 1;
                    }

                    end = end * muteCount;
                    if (muteCount < 4) {
                        onMute(player, bannedBy, getBanTimeById(reason), getBanUnitById(reason), getBanReasonById(reason));
                        Database.update("UPDATE bans SET `name` = '" + player + "', `mutedBy` = '" + bannedBy + "', `mutedUntil` = '" + end + "', `muteId` = '" + reason + "', `muteCount` = '" + muteCount + "' WHERE `name` = '" + player + "'");
                    } else {
                        onMute(player, bannedBy, 0, getBanUnitById(reason), getBanReasonById(reason));
                        Database.update("UPDATE bans SET `name` = '" + player + "', `mutedBy` = '" + bannedBy + "', `mutedUntil` = '" + '0' + "', `muteId` = '" + reason + "', `muteCount` = '" + muteCount + "' WHERE `name` = '" + player + "'");
                    }
                }
            } else {
                throw new NullPointerException("TimeUnit can't be null.");
            }
        } else {
            if (getBanTypeById(reason).equals("ban")) {
                int banCount = Integer.parseInt(getBanCountRaw(player));
                if (getNextBanStronger()){
                    banCount = Integer.parseInt(getBanCountRaw(player)) + 1;
                }
                onBan(player, bannedBy, getBanTimeById(reason), getBanUnitById(reason), getBanReasonById(reason));
                Database.update("UPDATE bans SET `name` = '" + player + "', `bannedBy` = '" + bannedBy + "', `bannedUntil` = '" + '0' + "', `banId` = '" + reason + "', `banCount` = '" + banCount + "' WHERE `name` = '" + player + "'");
            } else {
                int muteCount = Integer.parseInt(getMuteCountRaw(player));
                if (getNextBanStronger()){
                    muteCount = Integer.parseInt(getMuteCountRaw(player)) + 1;
                }
                onMute(player, bannedBy, getBanTimeById(reason), getBanUnitById(reason), getBanReasonById(reason));
                Database.update("UPDATE bans SET `name` = '" + player + "', `mutedBy` = '" + bannedBy + "', `mutedUntil` = '" + '0' + "', `muteId` = '" + reason + "', `muteCount` = '" + muteCount + "' WHERE `name` = '" + player + "'");
            }
        }
    }

    public static void unbanPlayer(String player, String unbannedBy, boolean falseBan, boolean automatic) {
        int banCount = Integer.parseInt(getBanCountRaw(player));
        if (getNextBanStronger() && falseBan) {
            banCount = Integer.parseInt(getBanCountRaw(player)) - 1;
        }
        onunBan(player, unbannedBy);
        Database.update("UPDATE bans SET `name` = '" + player + "', `bannedBy` = 'null', `bannedUntil` = '-1', `banId` = '0', `banCount` = '" + banCount + "' WHERE `name` = '" + player + "'");
    }

    public static void unmutePlayer(String player, String unmutedBy, boolean falseMute, boolean automatic){
        int muteCount = Integer.parseInt(getMuteCountRaw(player));
        if (getNextBanStronger() && falseMute){
            muteCount = Integer.parseInt(getMuteCountRaw(player)) - 1;
        }
        onunMute(player, unmutedBy);
        Database.update("UPDATE bans SET `name` = '" + player + "', `mutedBy` = 'null', `mutedUntil` = '-1', `muteId` = '0', `muteCount` = '" + muteCount + "' WHERE `name` = '" + player + "'");
    }

    public static String getBanCountRaw(final String player) {
        return String.valueOf(get("banCount", "bans", "name", player));
    }

    public static long getBannedUntil(final String player) {
        return Long.parseLong(String.valueOf(get("bannedUntil", "bans", "name", player)));
    }

    public static long getMutedUntil(final String player) {
        return Long.parseLong(String.valueOf(get("mutedUntil", "bans", "name", player)));
    }

    public static Integer getBanIdRaw(final String player) {
        return Integer.parseInt(String.valueOf(get("banId", "bans", "name", player)));
    }

    public static Integer getMuteIdRaw(final String player) {
        return Integer.parseInt(String.valueOf(get("muteId", "bans", "name", player)));
    }

    public static String getBannedByRaw(final String player) {
        return String.valueOf(get("bannedBy", "bans", "name", player));
    }

    public static String getMuteCountRaw(final String player) {
        return String.valueOf(get("muteCount", "bans", "name", player));
    }

    public static String getMutedByRaw(final String player) {
        return String.valueOf(get("mutedBy", "bans", "name", player));
    }

    public static Boolean isMuted(final String player) {

        if (get("mutedUntil", "bans", "name", player) == null){
            return false;
        }

        return Long.parseLong(String.valueOf(get("mutedUntil", "bans", "name", player))) != -1;
    }

    public static Boolean isBanned(final String player) {

        if (get("bannedUntil", "bans", "name", player) == null){
            return false;
        }

        return Long.parseLong(String.valueOf(get("bannedUntil", "bans", "name", player))) != -1;
    }

    public static String getEnd(long duration) {
        if (duration > 0) {
            long millis = duration - System.currentTimeMillis();
            int days = 0;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            while (millis >= 1000) {
                seconds++;
                millis -= 1000;
            }
            while (seconds >= 60) {
                minutes++;
                seconds -= 60;
            }
            while (minutes >= 60) {
                hours++;
                minutes -= 60;
            }
            while (hours >= 24) {
                days++;
                hours -= 24;
            }
            return days + "§eday(s)§r, " + hours + "§ehour(s)§r, " + minutes + "§eminute(s)§r, " + seconds + "§esecond(s)";
        } else {
            return "§cNEVER";
        }
    }

    public static String getBanMessage(Player player){
        String message = "   §l§cBAN   §r\n";
        message += "§8» §r§cYou were banned.\n";
        message += "§8» §r§fReason: §e" + getBanReasonById(getBanIdRaw(player.getName())) + "§r\n";
        message += "§8» §r§fExpires in: §e" + getEnd(getBannedUntil(player.getName())) + "§r\n";
        message += "§8» §r§fUnban: §ehttps://stimomc.net/go/discord/";

        return message;
    }

    public static String getBanMessage(String player){
        String message = "   §l§cBAN   §r\n";
        message += "§8» §r§cYou were banned.\n";
        message += "§8» §r§fReason: §e" + getBanReasonById(getBanIdRaw(player)) + "§r\n";
        message += "§8» §r§fExpires in: §e" + getEnd(getBannedUntil(player)) + "§r\n";
        message += "§8» §r§fUnban: §ehttps://stimomc.net/go/discord/";

        return message;
    }

    public static String getMuteMessage(Player player){
        String message = "   §l§cMUTE   §r\n";
        message += "§8» §r§cYou were muted.\n";
        message += "§8» §r§fReason: §e" + getBanReasonById(getMuteIdRaw(player.getName())) + "§r\n";
        message += "§8» §r§fExpires in: §e" + getEnd(getMutedUntil(player.getName())) + "§r\n";
        message += "§8» §r§fUnmute: §ehttps://stimomc.net/go/discord/";

        return message;
    }

    public static String getMuteMessage(String player){
        String message = "   §l§cMUTE   §r\n";
        message += "§8» §r§cYou were muted.\n";
        message += "§8» §r§fReason: §e" + getBanReasonById(getMuteIdRaw(player)) + "§r\n";
        message += "§8» §r§fExpires in: §e" + getEnd(getMutedUntil(player)) + "§r\n";
        message += "§8» §r§fUnmute: §ehttps://stimomc.net/go/discord/";

        return message;
    }

    public static String getKickMessage(Player player, String reason){
        String message = "   §l§cKICK   §r\n";
        message += "§8» §r§cYou were kicked.\n";
        message += "§8» §r§fReason: §e" + reason + "§r\n";
        message += "§8» §r§fQuestions: §ehttps://stimomc.net/go/discord/";

        return message;
    }

    public static boolean checkExpired(Player player, boolean mute){
        long current = System.currentTimeMillis();

        long end;
        if (!mute) {
            end = getBannedUntil(player.getName());
        } else {
            end = getMutedUntil(player.getName());
        }

        if(end > 0) {
            return end < current;
        }
        return false;
    }

    public static boolean checkExpired(String player, boolean mute){
        long current = System.currentTimeMillis();

        long end;
        if (!mute) {
            end = getBannedUntil(player);
        } else {
            end = getMutedUntil(player);
        }

        if(end > 0) {
            return end < current;
        }
        return false;
    }

    public static void onBan(String name, String bannedBy, Integer time, String unit, String reason) {
        String url = WebhookAPI.getWebhookLink("security_webhook");

        String expireIn;
        if (time == 0){
            expireIn = "NEVER";
        } else {
            expireIn = time + unit;
        }

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + " were banned", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Player: ", name))
                    .addField(new WebhookEmbed.EmbedField(false, "Banned by: ", bannedBy))
                    .addField(new WebhookEmbed.EmbedField(false, "Expire in: ", expireIn))
                    .addField(new WebhookEmbed.EmbedField(false, "Reason: ", reason))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onMute(String name, String bannedBy, Integer time, String unit, String reason) {
        String url = WebhookAPI.getWebhookLink("security_webhook");

        String expireIn;
        if (time == 0){
            expireIn = "NEVER";
        } else {
            expireIn = time + unit;
        }

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + " was muted", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Player: ", name))
                    .addField(new WebhookEmbed.EmbedField(false, "Muted by: ", bannedBy))
                    .addField(new WebhookEmbed.EmbedField(false, "Expire in: ", expireIn))
                    .addField(new WebhookEmbed.EmbedField(false, "Reason: ", reason))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onunBan(String name, String unbannedBy) {
        String url = WebhookAPI.getWebhookLink("security_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + " was unbanned", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Player: ", name))
                    .addField(new WebhookEmbed.EmbedField(false, "Unbanned by: ", unbannedBy))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onunMute(String name, String unbannedBy) {
        String url = WebhookAPI.getWebhookLink("security_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + " was unmuted", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Player: ", name))
                    .addField(new WebhookEmbed.EmbedField(false, "Unmuted by: ", unbannedBy))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
