package com.tsquare.speakfriend.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class UserSettingsModel extends Model {
    public UserSettingsModel() throws SQLException {}

    protected String getTableName() {
        return "user_settings";
    }

    public void createUserSetting(
        int userId,
        String option,
        String value
    ) throws SQLException {
        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("option", option);
        columnValueMap.put("value", value);

        insert(columnValueMap);
    }

    public void updateUserSetting(
        int userId,
        String option,
        String value
    ) throws SQLException {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("value", value);

        HashMap<String, Object> whereMap = new HashMap<>();
        whereMap.put("option", option);
        whereMap.put("user_id", userId);

        update(updateMap, whereMap);
    }

    public ResultSet getUserSetting(int userId, String option) throws SQLException {
        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("user_id", userId);
        columnValueMap.put("option", option);

        return get(columnValueMap);
    }

    public void deleteUserSetting(int userId, String option) throws SQLException {
        HashMap<String, Object> columnValues = new HashMap<>();
        columnValues.put("user_id", userId);
        columnValues.put("option", option);

        delete(columnValues);
    }
}
