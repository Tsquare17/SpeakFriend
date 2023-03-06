package com.tsquare.speakfriend.database.schema;

import com.tsquare.speakfriend.database.connection.SqliteConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Builder {
    Connection connection = SqliteConnection.getConnection();

    public Builder() throws SQLException {}

    public void close() throws SQLException {
        connection.close();
    }

    public void reset() throws SQLException {
        connection.close();

        connection = SqliteConnection.getConnection();
    }

    public void createUsersTable() throws SQLException {
        String sql = """
            create table if not exists users
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

        statement.close();
    }

    public void createAccountsTable() throws SQLException {
        String sql = """
            create table if not exists accounts (\n
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

        statement.close();
    }

    public void createAccountTagsTables() throws SQLException {
        String sql = """
            create table if not exists account_tags (\n
                id       INTEGER\n
                primary key autoincrement,\n
                user_id INT not null\n
                constraint fk_account_tags_user_id\n
                references users\n
                on update cascade on delete cascade,\n
                tag_name     VARCHAR(255)\n
            );

            create index account_tags_user_id\n
                on account_tags (user_id);\n
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        sql = """
            create table if not exists account_account_tags (\n
                id INTEGER\n
                primary key autoincrement,\n

                account_id  INT not null\n
                constraint fk_account_tags_account_id\n
                references accounts\n
                on update cascade on delete cascade,\n

                account_tag_id  INT not null\n
                constraint fk_account_tags_account_tag_id\n
                references account_tags\n
                on update cascade on delete cascade\n
            );

            create index account_tags_account_id\n
                on account_account_tags (account_id);\n

            create index account_tags_tag_id\n
                on account_account_tags (account_tag_id);\n
            """;

        statement.close();

        statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
    }

    public void createUserSettingsTable() throws SQLException {
        String sql = """
            create table if not exists user_settings
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

            create unique index user_settings_option
                on user_settings (user_id, option);

            create index user_settings_user_id
                on user_settings (user_id);
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
    }

    public void createSystemSettingsTable() throws SQLException {
        String sql = """
            create table if not exists system_settings
            (
                id     INTEGER
                    primary key autoincrement,
                option VARCHAR(255) not null,
                value  VARCHAR(255) not null
            );

            create unique index system_settings_option
                on system_settings (option);
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
    }

    public void renameTable(String tableName, String newName) throws SQLException {
        String sql = "alter table " + tableName + " rename to " + newName;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
    }

    public void dropTable(String tableName) throws SQLException {
        String sql = "drop table " + tableName;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
    }
}
