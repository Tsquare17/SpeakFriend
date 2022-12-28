package com.tsquare.speakfriend.database.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPreviewEntity extends Entity {
    public AccountPreviewEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public int getId() {
        return (int) props.get("id");
    }

    public String getName() {
        return (String) props.get("name");
    }

    public void setName(String name) {
        props.replace("name", name);
    }
}
