package net.core.manager.altManager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import cn.nukkit.Player;
import net.core.api.WebhookAPI;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Alts {

    public static void create(){
        Database.createTables("CREATE TABLE IF NOT EXISTS cids(" +
                "cid VARCHAR(255)," +
                "names LONGTEXT," +
                "PRIMARY KEY (cid));");

        Database.createTables("CREATE TABLE IF NOT EXISTS uuids(" +
                "uuid VARCHAR(255)," +
                "names LONGTEXT," +
                "PRIMARY KEY (uuid));");

        Database.createTables("CREATE TABLE IF NOT EXISTS xuids(" +
                "xuid VARCHAR(255)," +
                "names LONGTEXT," +
                "PRIMARY KEY (xuid));");

        Database.createTables("CREATE TABLE IF NOT EXISTS ips(" +
                "ip VARCHAR(255)," +
                "names LONGTEXT," +
                "PRIMARY KEY (ip));");
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
            return "ERROR";
        }
        return "ERROR";
    }

    public static void registerCID(Player player){
        CompletableFuture.runAsync(() -> {
            if (!Database.exists("cids", "cid", String.valueOf(player.getClientId()))) {
                Database.update("INSERT INTO " + "cids" + " (cid, names) VALUES ('" + player.getClientId().toString() + "', '" + player.getName().toLowerCase().replace(" ", "") + ";');");
            } else {
                addPlayerCID(player.getClientId().toString(), player.getName().toLowerCase().replace(" ", ""));
            }
        });
    }

    public static void registerUUID(Player player){
        CompletableFuture.runAsync(() -> {
            if (!Database.exists("uuids", "uuid", String.valueOf(player.getUniqueId()))) {
                Database.update("INSERT INTO " + "uuids" + " (uuid, names) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName().toLowerCase().replace(" ", "") + ";');");
            } else {
                addPlayerUUID(player.getUniqueId().toString(), player.getName().toLowerCase().replace(" ", ""));
            }
        });
    }

    public static void registerXUID(CorePlayer player){
        CompletableFuture.runAsync(() -> {
            if (!Database.exists("xuids", "xuid", String.valueOf(player.getXUID()))) {
                Database.update("INSERT INTO " + "xuids" + " (xuid, names) VALUES ('" + String.valueOf(player.getXUID()) + "', '" + player.getName().toLowerCase().replace(" ", "") + ";');");
            } else {
                addPlayerXUID(String.valueOf(player.getXUID()), player.getName().toLowerCase().replace(" ", ""));
            }
        });
    }

    public static void registerIP(CorePlayer player){
        CompletableFuture.runAsync(() -> {
            if (!Database.exists("ips", "ip", String.valueOf(((CorePlayer) player).getOriginalAddress()))) {
                Database.update("INSERT INTO " + "ips" + " (ip, names) VALUES ('" + String.valueOf(((CorePlayer) player).getOriginalAddress()) + "', '" + player.getName().toLowerCase().replace(" ", "") + ";');");
            } else {
                addPlayerIP(String.valueOf(((CorePlayer) player).getOriginalAddress()), player.getName().toLowerCase().replace(" ", ""));
            }
        });
    }

    public static void addPlayerCID(final String player_cid, final String name) {
        if (getCIDPlayers_RAW(player_cid).contains(name + ";")) return;
        final String names = getCIDPlayers_RAW(player_cid).replace(" ", "") + name + ";";
        Database.update("UPDATE " + "cids" + " SET names =' " + names + "' WHERE cid = '" + player_cid.replace(" ", "") + "';");
    }

    public static String getCIDPlayers_RAW(final String player_cid) {
        return String.valueOf(get("names", "cids", "cid", player_cid));
    }

    public static void addPlayerXUID(final String player_xuid, final String name) {
        if (getXUIDPlayers_RAW(player_xuid).contains(name + ";")) return;
        final String names = getXUIDPlayers_RAW(player_xuid).replace(" ", "") + name + ";";
        Database.update("UPDATE " + "xuids" + " SET names =' " + names + "' WHERE xuid = '" + player_xuid.replace(" ", "") + "';");
    }

    public static String getXUIDPlayers_RAW(final String player_xuid) {
        return String.valueOf(get("names", "xuids", "xuid", player_xuid));
    }

    public static void addPlayerUUID(final String player_uuid, final String name) {
        if (getUUIDPlayers_RAW(player_uuid).contains(name + ";")) return;
        final String names = getUUIDPlayers_RAW(player_uuid).replace(" ", "") + name + ";";
        Database.update("UPDATE " + "uuids" + " SET names =' " + names + "' WHERE uuid = '" + player_uuid.replace(" ", "") + "';");
    }

    public static String getUUIDPlayers_RAW(final String player_uuid) {
        return String.valueOf(get("names", "uuids", "uuid", player_uuid));
    }

    public static void addPlayerIP(final String player_ip, final String name) {
        if (getIPPlayers_RAW(player_ip).contains(name + ";")) return;
        final String names = getIPPlayers_RAW(player_ip).replace(" ", "") + name + ";";
        Database.update("UPDATE " + "ips" + " SET names =' " + names + "' WHERE ip = '" + player_ip + "';");
    }

    public static String getIPPlayers_RAW(final String player_ip) {
        return String.valueOf(get("names", "ips", "ip", player_ip));
    }

    public static ArrayList<String> getPlayerAccounts(Player player){
        final String uuidPlayersRaw = getUUIDPlayers_RAW(player.getUniqueId().toString().replace(" ", ""));
        final String cidPlayersRaw = getCIDPlayers_RAW(player.getClientId().toString().replace(" ", ""));
        final String ipPlayersRaw = getIPPlayers_RAW(((CorePlayer) player).getOriginalAddress().replace(" ", ""));
        final String xuidPlayersRaw = getXUIDPlayers_RAW(((CorePlayer) player).getXUID().replace(" ", ""));
        final ArrayList<String> list = new ArrayList<>();

        final String[] uuidList = uuidPlayersRaw.split(";");
        for (String names : uuidList){
            if (!names.replace(";", "").isEmpty() && !names.replace(";", "").equals("null") && !names.replace(";", "").equals("ERROR") && !list.contains(names.replace(";", ""))) {
                if (!list.contains(names.replace(";", ""))) list.add(names);
            }
        }

        final String[] cidList = cidPlayersRaw.split(";");
        for (String names : cidList){
            if (!names.replace(";", "").isEmpty() && !names.replace(";", "").equals("null") && !names.replace(";", "").equals("ERROR") && !list.contains(names.replace(";", ""))) {
                if (!list.contains(names.replace(";", ""))) list.add(names);
            }
        }

        final String[] ipList = ipPlayersRaw.split(";");
        for (String names : ipList){
            if (!names.replace(";", "").isEmpty() && !names.replace(";", "").equals("null") && !names.replace(";", "").equals("ERROR") && !list.contains(names.replace(";", ""))) {
                if (!list.contains(names.replace(";", ""))) list.add(names);
            }
        }

        final String[] xuidList = xuidPlayersRaw.split(";");
        for (String names : xuidList){
            if (!names.replace(";", "").isEmpty() && !names.replace(";", "").equals("null") && !names.replace(";", "").equals("ERROR") && !list.contains(names.replace(";", ""))) {
                if (!list.contains(names.replace(";", ""))) list.add(names);
            }
        }
        return list;
    }
}
