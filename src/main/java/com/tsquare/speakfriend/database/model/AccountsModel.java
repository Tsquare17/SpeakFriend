package com.tsquare.speakfriend.database.model;

import java.sql.*;
import java.util.HashMap;

public class AccountsModel extends Model {
    public AccountsModel() throws SQLException {}

    protected String getTableName() {
        return "accounts";
    }

    public void createUserAccount(
        int userId,
        String name,
        String user,
        String pass,
        String url,
        String notes
    ) throws SQLException {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id", userId);
        hashMap.put("name", name);
        hashMap.put("user", user);
        hashMap.put("pass", pass);
        hashMap.put("url", url);
        hashMap.put("notes", notes);

        insert(hashMap);
    }

    public ResultSet getAccounts() throws SQLException {
        return get();
    }

    public ResultSet getAccount(int accountId) throws SQLException {

        return get("id", accountId);
    }

    public ResultSet getUserAccounts(int userId) throws SQLException {
        return get("user_id", userId);
    }

    public ResultSet getUserAccountByName(int userId, String accountName) throws SQLException {
        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("name", accountName);

        return get(columnValueMap);
    }

    public void updateUserAccount(
        int userId,
        String name,
        String user,
        String pass,
        String url,
        String notes
    ) throws SQLException {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", name);
        updateMap.put("user", user);
        updateMap.put("pass", pass);
        updateMap.put("url", url);
        updateMap.put("notes", notes);

        HashMap<String, Object> whereMap = new HashMap<>();
        whereMap.put("user_id", userId);

        update(updateMap, whereMap);
    }

    public void deleteAccount(int accountId) throws SQLException {
        delete(accountId);
    }
}
