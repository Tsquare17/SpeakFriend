package com.tsquare.speakfriend.database.connection;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {
    private static String dbName = null;

    public static Connection getConnection(String dbName) throws SQLException {
        SqliteConnection.dbName = dbName;
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        String url = "jdbc:sqlite:" + dbName;

        return DriverManager.getConnection(url, config.toProperties());
    }

    public static Connection getConnection() throws SQLException {
        if (dbName != null) {
            return getConnection(dbName);
        }

        String db = System.getProperty("user.home") + "/.speakfriend/friend.db";

        return getConnection(db);
    }
}
