package com.tsquare.speakfriend.database.model;

import com.tsquare.speakfriend.database.model.relationship.JoinTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagsModel extends Model {
    public TagsModel() throws SQLException {}

    String tableName = "account_tags";

    protected String getTableName() {
        return tableName;
    }

    public int createUserTag(
        int userId,
        String tag
    ) throws SQLException {
        tableName = "account_tags";

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("tag_name", tag);

        return insert(columnValueMap);
    }

    public int createAccountTag(int accountId, int tagId) throws SQLException {
        tableName = "account_account_tags";

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("account_id", accountId);
        columnValueMap.put("account_tag_id", tagId);

        return insert(columnValueMap);
    }

    public ResultSet getUserTags(int userId) throws SQLException {
        tableName = "account_tags";

        return get("user_id", userId);
    }

    public ResultSet getAccountTags(int accountId) throws SQLException {
        tableName = "account_account_tags";

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("account_id", accountId);

        List<JoinTable> joinTables = new ArrayList<>();

        JoinTable join = new JoinTable();
        join.setTable("account_tags");
        join.setKeys("account_tag_id", "id");

        joinTables.add(join);

        return getJoin(columnValueMap, joinTables);
    }

    public ResultSet getTagByName(int userId, String tag) throws SQLException {
        tableName = "account_tags";

        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("tag_name", tag);

        return get(columnValueMap);
    }
}
