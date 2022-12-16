package com.tsquare.speakfriend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Builder {
    Connection connection = SqliteConnection.getConnection();

    public Builder() throws SQLException {}

    public void createUsersTable() throws SQLException {
        String sql = """
            create table users
            (
                id   INTEGER
                    primary key autoincrement,
                name VARCHAR(255) not null,
                pass VARCHAR(255) not null
            );

            create unique index users_name
                on users (name);
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);
    }

    public void createAccountsTable() throws SQLException {
        String sql = """
            create table accounts (\n
                id       INTEGER\n
                primary key autoincrement,\n
                user_id  INT not null\n
                constraint fk_accounts_user_id_id\n
                references users\n
                on update restrict on delete cascade,\n
                name     VARCHAR(255),\n
                user     VARCHAR(255),\n
                pass     VARCHAR(255),\n
                url      VARCHAR(255),\n
                notes    TEXT
            );

            create index accounts_user_id\n
                on accounts (user_id);""";

        Statement statement = connection.createStatement();

        statement.execute(sql);
    }

    public void createSettingsTable() throws SQLException {
        String sql = """
            create table settings
            (
                id      INTEGER
                    primary key autoincrement,
                user_id INT          not null
                    constraint fk_settings_user_id_id
                        references users
                        on update restrict on delete cascade,
                option  VARCHAR(255) not null,
                value   VARCHAR(255) not null
            );

            create unique index settings_option
                on settings (option);

            create index settings_user_id
                on settings (user_id);
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);
    }

    public void createSystemSettingsTable() throws SQLException {
        String sql = """
            create table system_settings
            (
                id     INTEGER
                    primary key autoincrement,
                option VARCHAR(255) not null,
                value  VARCHAR(255) not null
            );
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);
    }
}
