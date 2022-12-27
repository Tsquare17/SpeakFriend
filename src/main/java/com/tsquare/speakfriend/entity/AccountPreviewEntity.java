package com.tsquare.speakfriend.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPreviewEntity extends Entity {
    AccountPreviewEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public int getId() {
        return (int) props.get("id");
    }

    public String getName() {
        return (String) props.get("name");
    }
}
