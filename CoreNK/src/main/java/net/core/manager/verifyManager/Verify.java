package net.core.manager.verifyManager;

import net.core.Core;
import net.core.database.Database;
import net.core.manager.groupManager.Groups;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Verify {

    private static Verify instance;

    public Verify(){
        instance = this;

        Database.createTables("CREATE TABLE IF NOT EXISTS verify(" +
                "name VARCHAR(255)," +
                "code LONGTEXT," +
                "PRIMARY KEY (name));");

        Database.createTables("CREATE TABLE IF NOT EXISTS verified(" +
                "name VARCHAR(255)," +
                "discordId LONGTEXT," +
                "PRIMARY KEY (name));");
    }

    public static void setVerifyCode(String name, String code) {
        if (!playerExists(name)) {
            Database.update("INSERT INTO " + "verify" + " (name, code) VALUES ('" + name + "', '" + code + "');");
        }
    }

    public static boolean playerExists(String search) {
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "verify" + " WHERE " + "name" + " = '" + search + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("name") != null;
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return false;
        }
        return false;
    }

    public String getVerifyCode(String playerName){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "verify" + " WHERE " + "name" + " = '" + playerName + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("code");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return null;
        }
        return null;
    }

    public String getVerified(String playerName){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "verified" + " WHERE " + "name" + " = '" + playerName + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("discordId");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return null;
        }
        return null;
    }

    public static Verify getInstance() {
        return instance;
    }
}
