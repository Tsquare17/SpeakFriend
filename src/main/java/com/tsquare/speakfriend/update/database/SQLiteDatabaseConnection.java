package com.tsquare.speakfriend.update.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseConnection {
    public Connection connect() throws SQLException {
        String db = System.getProperty("user.home") + "/.speakfriend/friend.db";
        String connectionUrl = "jdbc:sqlite:" +db + "?foreign_keys=true";

        return DriverManager.getConnection(connectionUrl);
    }
}
