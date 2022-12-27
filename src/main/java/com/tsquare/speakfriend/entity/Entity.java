package com.tsquare.speakfriend.entity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

abstract public class Entity {
    Map<String, Object> props = new HashMap<>();

    Entity(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int columns = meta.getColumnCount();

        for (int i = 1; i <= columns; i++) {
            props.put(meta.getColumnName(i), resultSet.getObject(i));
        }
    }
}
