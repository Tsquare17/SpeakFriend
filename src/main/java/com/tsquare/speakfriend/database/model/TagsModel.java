package com.tsquare.speakfriend.database.model;

import com.tsquare.speakfriend.database.model.relationship.JoinTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagsModel extends Model {
    public TagsModel() throws SQLException {
        setSelect("SELECT account_tags.* from account_tags ");
    }

    String tableName = "account_tags";

    protected String getTableName() {
        return tableName;
    }

    public int createUserTag(
        int userId,
        String tag
    ) throws SQLException {
        tableName = "user_tags";

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("user_tag_name", tag);

        return insert(columnValueMap);
    }

    public int createAccountTag(int accountId, int userTagId) throws SQLException {
        tableName = "account_tags";

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("account_id", accountId);
        columnValueMap.put("user_tag_id", userTagId);

        return insert(columnValueMap);
    }

    public ResultSet getUserTags(int userId) throws SQLException {
        tableName = "user_tags";

        setSelect("SELECT user_tags.* from user_tags ");

        setOrderBy("user_tags.user_tag_name ASC");

        return get("user_id", userId);
    }

    public ResultSet getAccountTagByName(int accountId, String tagName) throws SQLException {
        tableName = "account_tags";

        setSelect("SELECT account_tags.* from account_tags ");

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("account_tags.account_id", accountId);
        columnValueMap.put("user_tags.user_tag_name", tagName);

        List<JoinTable> joinTables = new ArrayList<>();

        JoinTable join = new JoinTable();
        join.setTable("user_tags");
        join.setKeys("user_tags.user_tag_id", "account_tags.user_tag_id");

        joinTables.add(join);

        return getJoin(columnValueMap, joinTables);
    }

    public ResultSet getAccountTags(int accountId) throws SQLException {
        tableName = "account_tags";

        setSelect("SELECT account_tags.*, user_tags.* from account_tags ");

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("account_id", accountId);

        List<JoinTable> joinTables = new ArrayList<>();

        JoinTable join = new JoinTable();
        join.setTable("user_tags");
        join.setKeys("user_tags.user_tag_id", "account_tags.user_tag_id");

        joinTables.add(join);

        return getJoin(columnValueMap, joinTables);
    }

    public ResultSet getTagByName(int userId, String tag) throws SQLException {
        tableName = "user_tags";

        setSelect("SELECT user_tags.* from user_tags ");

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("user_tag_name", tag);

        return get(columnValueMap);
    }

    public void deleteAccountTag(int accountTagId) throws SQLException {
        tableName = "account_tags";

        HashMap<String, Object> columnValues = new HashMap<>();
        columnValues.put("account_tag_id", accountTagId);

        delete(columnValues);
    }

    public void deleteUserTag(int userTagId) throws SQLException {
        tableName = "user_tags";

        HashMap<String, Object> columnValues = new HashMap<>();
        columnValues.put("user_tag_id", userTagId);

        delete(columnValues);
    }
}
