package com.tsquare.speakfriend.database.connection;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            String db = System.getProperty("user.home") + "/.speakfriend/friend.db";
            String url = "jdbc:sqlite:" + db;
            connection = DriverManager.getConnection(url, config.toProperties());
        }

        return connection;
    }

    public static Connection getConnection(boolean inMemory) throws SQLException {
        if (connection == null && inMemory) {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            String url = "jdbc:sqlite::memory:";
            connection = DriverManager.getConnection(url, config.toProperties());
        } else if (connection == null) {
            return getConnection();
        }

        return connection;
    }
}
