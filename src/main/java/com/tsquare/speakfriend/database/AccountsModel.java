package com.tsquare.speakfriend.database;

import java.sql.*;

public class AccountsModel {
    Connection connection = SqliteConnection.getConnection();

    public AccountsModel() throws SQLException {}

    public void createUserAccount(
        int userId,
        String name,
        String user,
        String pass,
        String url,
        String notes
    ) throws SQLException {
        String sql = """
            insert into accounts (user_id, name, user, pass, url, notes) values(?, ?, ?, ?, ?, ?)
            """;

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, userId);
        statement.setString(2, name);
        statement.setString(3, user);
        statement.setString(4, pass);
        statement.setString(5, url);
        statement.setString(6, notes);

        statement.executeUpdate();
    }

    public ResultSet getAccounts() throws SQLException {
        String sql = """
            select * from accounts;
            """;

        Statement statement = connection.createStatement();

        return statement.executeQuery(sql);
    }

    public ResultSet getUserAccounts(int userId) throws SQLException {
        String sql = """
            select * from accounts where user_id = ?;
            """;

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, userId);

        return statement.executeQuery();
    }

    public void updateUserAccount(
        int userId,
        String name,
        String user,
        String pass,
        String url,
        String notes
    ) throws SQLException {
        String sql = """
            update accounts set name = ?, user = ?, pass = ?, url = ?, notes = ? where user_id = ?;
            """;

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, name);
        statement.setString(2, user);
        statement.setString(3, pass);
        statement.setString(4, url);
        statement.setString(5, notes);
        statement.setInt(6, userId);

        statement.executeUpdate();
    }
}
