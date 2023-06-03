package net.core.api;

import cn.nukkit.Player;
import net.core.Core;
import net.core.database.Database;
import net.core.manager.groupManager.Groups;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class StatsAPI extends API {

    public static void createStatsTable(String name){
        String tableName = name+"_stats";
        Database.createTables("CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "name VARCHAR(50), " +
                "kills INTEGER DEFAULT 0, " +
                "deaths INTEGER DEFAULT 0, " +
                "wins INTEGER DEFAULT 0, " +
                "loses INTEGER DEFAULT 0, " +
                "beds INTEGER DEFAULT 0, " +
                "elo INTEGER DEFAULT 1000, " +
                "rounds INTEGER DEFAULT 0, " +
                "PRIMARY KEY (name));");
    }

    public static void registerStatsPlayer(String player, String statsTable){
        CompletableFuture.runAsync(() -> {
            if (Database.exists(statsTable, "name", player)) return;
            Database.update("INSERT INTO " + statsTable + " (name, kills, deaths, wins, loses, beds, elo, rounds) VALUES ('" + player.toLowerCase() + "', '0', 0, '0', '0', '0', '1000', '0');");
        });
    }

    public static Integer getKills(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("kills");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addKills(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getKills(playerName, statsTable) + i);
            Database.update("UPDATE " + statsTable + " SET `kills` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static Integer getDeaths(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("deaths");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addDeaths(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getDeaths(playerName, statsTable) + i);
            Database.update("UPDATE " + statsTable + " SET `deaths` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static Integer getWins(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("wins");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addWins(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getWins(playerName, statsTable) + i);
            Database.update("UPDATE " + statsTable + " SET `wins` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static Integer getLoses(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("loses");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addLoses(String playerName, Integer i, String statsTable){
        int result = (getLoses(playerName, statsTable) + i);
        Database.update("UPDATE " + statsTable + " SET `loses` = '" + result + "' WHERE `name` = '" + playerName + "'");
    }

    public static Integer getBeds(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("beds");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addBeds(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getBeds(playerName, statsTable) + i);
            Database.update("UPDATE " + statsTable + " SET `beds` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static Integer getElo(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("elo");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addElo(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getElo(playerName, statsTable) + i);
            Database.update("UPDATE " + statsTable + " SET `elo` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static void removeElo(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getElo(playerName, statsTable) - i);
            Database.update("UPDATE " + statsTable + " SET `elo` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static Integer getRounds(String name, String statsTable){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + statsTable + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("rounds");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void addRounds(String playerName, Integer i, String statsTable){
        CompletableFuture.runAsync(() -> {
            int result = (getRounds(playerName, statsTable) + i);
            Database.update("UPDATE " + statsTable + " SET `rounds` = '" + result + "' WHERE `name` = '" + playerName + "'");
        });
    }

    public static String getEloRank(String name, String statsTable){
        if (getElo(name, statsTable) < 1050) return "§7Noob";
        else if (getElo(name, statsTable) < 1100) return "§aNoobv2";
        else if (getElo(name, statsTable) < 1200) return "§6Pro";
        else if (getElo(name, statsTable) < 1500) return "§cExpert";
        else if (getElo(name, statsTable) < 1800) return "§eChampion";
        else return "§4Elite";
    }

    public static String getNextEloRank(String name, String statsTable){
        switch (getEloRank(name, statsTable)){
            case "§7Noob":
                return "§aNoobv2";
            case "§aNoobv2":
                return "§6Pro";
            case "§6Pro":
                return "§cExpert";
            case "§cExpert":
                return "§eChampion";
            case "§eChampion":
                return "§4Elite";
            default:
                return "§4Highest rank reached.";
        }
    }

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
        return "StatsAPI";
    }
}
