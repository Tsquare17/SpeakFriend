package com.tsquare.speakfriend.database.model;

import com.tsquare.speakfriend.database.model.relationship.JoinTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountsModel extends Model {
    public AccountsModel() throws SQLException {
    }

    protected String getTableName() {
        return "accounts";
    }

    public int createUserAccount(
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

        return insert(hashMap);
    }

    public ResultSet getAccounts() throws SQLException {
        return get();
    }

    public ResultSet getAccount(int accountId) throws SQLException {
        return get("id", accountId);
    }

    public ResultSet getAccountsByIds(List<Integer> list) throws SQLException {
        return getIn("id", list);
    }

    public ResultSet getUserAccounts(int userId) throws SQLException {
        setSelect(
            "SELECT accounts.*, GROUP_CONCAT(user_tags.user_tag_name, '||') AS tags " +
                "FROM accounts "
        );

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("accounts.user_id", userId);

        List<JoinTable> joinTables = new ArrayList<>();

        JoinTable accountTagsJoin = new JoinTable();
        accountTagsJoin.setTable("account_tags");
        accountTagsJoin.setKeys("accounts.id", "account_tags.account_id");

        joinTables.add(accountTagsJoin);

        JoinTable userTagsJoin = new JoinTable();
        userTagsJoin.setTable("user_tags");
        userTagsJoin.setKeys("account_tags.user_tag_id", "user_tags.user_tag_id");

        joinTables.add(userTagsJoin);

        setGroupBy("accounts.id");

        return getJoin(columnValueMap, joinTables);
    }

    public ResultSet getUserAccountByName(int userId, String accountName) throws SQLException {
        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("name", accountName);

        return get(columnValueMap);
    }

    public void updateAccount(
        int accountId,
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
        whereMap.put("id", accountId);

        update(updateMap, whereMap);
    }

    public void deleteAccount(int accountId) throws SQLException {
        delete(accountId);
    }
}
