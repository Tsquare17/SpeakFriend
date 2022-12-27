package com.tsquare.speakfriend.database.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SystemSettingsModel extends Model {
    public SystemSettingsModel() throws SQLException {}

    protected String getTableName() {
        return "system_settings";
    }

    public void createSystemSetting(
        String option,
        String value
    ) throws SQLException {
        HashMap<String, Object> columnValueMap = new HashMap<>();
        columnValueMap.put("option", option);
        columnValueMap.put("value", value);

        insert(columnValueMap);
    }

    public void updateSystemSetting(
        String option,
        String value
    ) throws SQLException {
        String sql = """
            update system_settings set value = ? where `option` = ?;
            """;

        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("value", value);

        HashMap<String, Object> whereMap = new HashMap<>();
        whereMap.put("option", option);

        update(updateMap, whereMap);
    }

    public ResultSet getSystemSetting(String option) throws SQLException {

        return get("option", option);
    }

    public void deleteSystemSetting(String option) throws SQLException {
        HashMap<String, Object> columnValues = new HashMap<>();
        columnValues.put("option", option);

        delete(columnValues);
    }
}
