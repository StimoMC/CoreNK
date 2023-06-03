package net.core.database;

import net.core.Core;
import net.core.mysql.MySQL;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class Database {

    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://" + MySQL.address + ":" + MySQL.port + "/" + MySQL.database + "?autoReconnect=true", MySQL.user, MySQL.password);
            Core.getInstance().getLogger().info("§aConnected to database.");
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (isConnected()) {
            try {
                connection.close();
                Core.getInstance().getLogger().info("§cDisconnected from database.");
            } catch (SQLException exception) {
                Core.getInstance().getLogger().info(exception.getMessage());
            }
        }
    }

    public static boolean isConnected() {
        return connection != null;
    }

    public static void update(final String qry) {
        CompletableFuture.runAsync(() -> {
            if (isConnected()) {
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(qry);
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public static ResultSet getResult(final String qry) {
        if (isConnected()) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(qry);
                return resultSet;
            } catch (SQLException exception) {
                Core.getInstance().getLogger().info(exception.getMessage());
                return null;
            }
        }
        connect();
        return null;
    }

    public static void createDefaultTables() {
        update("CREATE TABLE IF NOT EXISTS users(" +
                "name VARCHAR(50)," +
                "xuid VARCHAR(128)," +
                "coins INTEGER DEFAULT 0," +
                "rank VARCHAR(128)," +
                "rankbefore VARCHAR(128)," +
                "rankduration VARCHAR(2048)," +
                "nickname VARCHAR(128)," +
                "permissions VARCHAR(2048)," +
                "PRIMARY KEY (name));");
    }

    public static void createTables(String query) {
        update(query);
    }

    public static boolean exists(String table, String where, String search) {
        try {
            final ResultSet rs = getResult("SELECT * FROM " + table + " WHERE " + where + " = '" + search + "';");
            if (rs != null && rs.next()) {
                return rs.getString(where) != null;
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return false;
        }
        return false;
    }
}
