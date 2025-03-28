package com.tsquare.speakfriend.database.schema;

import com.tsquare.speakfriend.config.AppConfig;
import com.tsquare.speakfriend.controller.update.UpdateController;
import com.tsquare.speakfriend.database.connection.SqliteConnection;
import com.tsquare.speakfriend.database.exception.DatabaseFileNotFoundException;
import com.tsquare.speakfriend.database.model.SystemSettingsModel;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final List<String> tables = new ArrayList<>();

    public void up() throws SQLException, DatabaseFileNotFoundException {
        up(false);
    }

    public void up(Boolean throwDbFileNotFound) throws SQLException, DatabaseFileNotFoundException {
        if (throwDbFileNotFound) {
            AppConfig appConfig = AppConfig.getInstance();

            String db = appConfig.getDbFile();

            // check for file existence
            File file = new File(db);
            if (!file.exists()) {
                throw new DatabaseFileNotFoundException();
            }
        }

        Connection connection = SqliteConnection.getConnection();

        DatabaseMetaData databaseMetaData = connection.getMetaData();

        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] {"TABLE"});

        while (resultSet.next()) {
            String name = resultSet.getString("TABLE_NAME");

            tables.add(name);
        }

        resultSet.close();
        connection.close();

        Builder builder = new Builder();

        if (!tables.contains("users")) {
            builder.createUsersTable();
        }

        if (!tables.contains("accounts")) {
            builder.createAccountsTable();
        }

        if (!tables.contains("system_settings")) {
            builder.createSystemSettingsTable();

            SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
            systemSettingsModel.createSystemSetting("version", UpdateController.upToDateSysVersion);
            systemSettingsModel.close();
        }

        if (!tables.contains("user_settings")) {
            builder.createUserSettingsTable();
        }

        if (!tables.contains("account_tags")) {
            builder.createAccountTagsTables();
        }

        builder.close();
    }
}
