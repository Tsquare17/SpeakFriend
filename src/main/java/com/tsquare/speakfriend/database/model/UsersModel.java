package com.tsquare.speakfriend.database.model;

import java.sql.*;
import java.util.HashMap;

public class UsersModel extends Model {
    public UsersModel() throws SQLException {}

    protected String getTableName() {
        return "users";
    }

    public int createUser(
        String name,
        String pass
    ) throws SQLException {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("pass", pass);

        return insert(hashMap);
    }

    public void updateUser(int userId, String name) throws SQLException {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", name);

        HashMap<String, Object> whereMap = new HashMap<>();
        whereMap.put("id", userId);

        update(updateMap, whereMap);
    }

    public void updateUser(int userId, String name, String pass) throws SQLException {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", name);
        updateMap.put("pass", pass);

        HashMap<String, Object> whereMap = new HashMap<>();
        whereMap.put("id", userId);

        update(updateMap, whereMap);
    }

    public ResultSet getUsers() throws SQLException {
        return get();
    }

    public ResultSet getUser(int userId) throws SQLException {

        return get("id", userId);
    }

    public ResultSet getUser(String name) throws SQLException {
        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("name", name);

        return get(columnValueMap);
    }

    public void deleteUser(int userId) throws SQLException {
        delete(userId);
    }
}
