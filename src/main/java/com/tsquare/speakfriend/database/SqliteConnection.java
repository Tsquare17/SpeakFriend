package com.tsquare.speakfriend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {
    public static Connection getConnection() throws SQLException {
        String db = System.getProperty("user.home") + "/.speakfriend/friend.db";
        String url = "jdbc:sqlite:" + db + "?foreign_keys=true";
        return DriverManager.getConnection(url);
    }
}
