package com.tsquare.speakfriend.database.schema;

import com.tsquare.speakfriend.database.connection.SqliteConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    Connection connection = SqliteConnection.getConnection();

    private final List<String> tables = new ArrayList<>();

    public Schema() throws SQLException {}

    public void up() throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();

        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] {"TABLE"});

        while (resultSet.next()) {
            String name = resultSet.getString("TABLE_NAME");

            tables.add(name);
        }

        Connection connection = SqliteConnection.getConnection();
        Builder builder = new Builder(connection);

        if (!tables.contains("users")) {
            builder.createUsersTable();
        }

        if (!tables.contains("accounts")) {
            builder.createAccountsTable();
        }

        if (!tables.contains("system_settings")) {
            builder.createSystemSettingsTable();
        }

        if (!tables.contains("user_settings")) {
            builder.createUserSettingsTable();
        }
    }
}
