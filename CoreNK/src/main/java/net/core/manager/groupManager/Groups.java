package net.core.manager.groupManager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import cn.nukkit.Player;
import net.core.Core;
import net.core.api.WebhookAPI;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;
import net.core.mysql.MySQL;
import net.core.utils.TimeUnit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Groups {

    protected static String defaultGroup = "Player";
    protected static String nickedGroup = "Player";

    public static void load(){
        Database.createTables("CREATE TABLE IF NOT EXISTS ranks(" +
                "rank_name VARCHAR(50)," +
                "chat_prefix VARCHAR(256)," +
                "nametag VARCHAR(256)," +
                "parents VARCHAR(1024)," +
                "permissions VARCHAR(1024)," +
                "emoji VARCHAR(128)," +
                "discordID VARCHAR(128)," +
                "rankID VARCHAR(128)," +
                "colorCode VARCHAR(128)," +
                "PRIMARY KEY (rank_name));");
    }

    public static String getChatFormat(final String group) {
        return String.valueOf(get("chat_prefix", "ranks", "rank_name", group));
    }

    public static String getNameTag(final String group) {
        return String.valueOf(get("nametag", "ranks", "rank_name", group));
    }

    public static String getDiscordId(final String group) {
        return String.valueOf(get("discordID", "ranks", "rank_name", group));
    }

    public static String getRankId(final String group) {
        return String.valueOf(get("rankID", "ranks", "rank_name", group));
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

    public static String getPlayerGroup(String name){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "users" + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("rank");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return null;
        }
        return null;
    }

    public static String getPlayerGroupBefore(String name){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "users" + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("rankbefore");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return null;
        }
        return null;
    }

    public static String getGroupColor(String rank){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "ranks" + " WHERE " + "rank_name" + " = '" + rank + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("colorCode");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return "§7";
        }
        return "§7";
    }

    public static Boolean existsRank(String rank) {
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "ranks" + " WHERE " + "rank_name" + " = '" + rank + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("rank_name") != null && Objects.equals(rs.getString("rank_name"), rank);
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return false;
        }
        return false;
    }

    public static String getGroupParentsRaw(final String group) {
        return String.valueOf(get("parents", "ranks", "rank_name", group));
    }

    public static String getColoredPlayerGroup(String rank, String playerName){
        if (getGroupColor(rank) == null || getPlayerGroup(playerName) == null) return null;
        return getGroupColor(rank) + getPlayerGroup(playerName);
    }

    public static String getDefaultGroup() {
        return defaultGroup;
    }

    public static void setRank(String playerName, String updatedBy, String groupName, String time, String timeName){
        long end = -1L;
        if (!time.equals("PERMANENT")) {
            long seconds = Integer.parseInt(time);
            TimeUnit unit = TimeUnit.getByString("d");
            if (unit != null) {
                seconds = seconds * unit.getSeconds();
                end = System.currentTimeMillis() + (seconds*1000);

                String oldGroup = Groups.getPlayerGroup(playerName);
                onGroupChange(playerName, oldGroup, groupName, updatedBy);
                Database.update("UPDATE users SET `rank` = '" + groupName + "', `rankbefore` = '" + oldGroup + "', `rankduration` = '" + end + "' WHERE `name` = '" + playerName + "'");
            } else {
                throw new NullPointerException("TimeUnit can't be null.");
            }
        } else {
            String oldGroup = Groups.getPlayerGroup(playerName);
            onGroupChange(playerName, oldGroup, groupName, updatedBy);
            Database.update("UPDATE users SET `rank` = '" + groupName + "', `rankbefore` = '" + oldGroup + "', `rankduration` = '0' WHERE `name` = '" + playerName + "'");
        }
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

    public static long getRankDuration(final String name) {
        return Long.parseLong(String.valueOf(get("rankduration", "users", "name", name)));
    }

    public static boolean playerRankExpired(Player player){
        long current = System.currentTimeMillis();
        long end = getRankDuration(player.getName());

        if(end > 0) {
            return end < current;
        }
        return false;
    }

    public static void onGroupChange(String name, String oldGroup, String newGroup, String changedBy) {
        String url = WebhookAPI.getWebhookLink("group_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + "'s Group changed", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Player: ", name))
                    .addField(new WebhookEmbed.EmbedField(false, "Old Group: ", oldGroup))
                    .addField(new WebhookEmbed.EmbedField(false, "New Group: ", newGroup))
                    .addField(new WebhookEmbed.EmbedField(false, "Changed by: ", changedBy))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nickPlayer(Player player){
        Database.update("UPDATE users SET `nickname` = '" + Nicks.getRandomNickname() + "' WHERE `name` = '" + player.getName() + "'");
    }

    public void unnickPlayer(Player player){
        Database.update("UPDATE users SET `nickname` = 'null' WHERE `name` = '" + player.getName() + "'");
    }

    public static String getPlayerNickname(final String name) {
        return String.valueOf(get("nickname", "users", "name", name));
    }

    public static String getNickedGroup() {
        return nickedGroup;
    }
}