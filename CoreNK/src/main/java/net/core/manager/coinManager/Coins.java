package net.core.manager.coinManager;

import net.core.Core;
import net.core.database.Database;
import net.core.manager.groupManager.Groups;
import net.core.utils.TimeUnit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Coins {

    public static void load(){
    }

    public static Integer getPlayerCoins(String name){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "users" + " WHERE " + "name" + " = '" + name + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("coins");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return -1;
        }
        return -1;
    }

    public static void setCoins(String playerName, Integer coins){
        Database.update("UPDATE users SET `coins` = '" + coins + "' WHERE `name` = '" + playerName + "'");
    }

    public static void addCoins(String playerName, Integer coins){
        int result = (getPlayerCoins(playerName) + coins);
        Database.update("UPDATE users SET `coins` = '" + result + "' WHERE `name` = '" + playerName + "'");
    }

    public static void removeCoins(String playerName, Integer coins){

        int result = (getPlayerCoins(playerName) - coins);
        if (result < 0) {
            Database.update("UPDATE users SET `coins` = '" + 0 + "' WHERE `name` = '" + playerName + "'");
            return;
        }
        Database.update("UPDATE users SET `coins` = '" + result + "' WHERE `name` = '" + playerName + "'");
    }
}
