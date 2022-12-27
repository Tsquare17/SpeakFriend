package com.tsquare.speakfriend.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserEntity extends Entity {
    public UserEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public int getId() {
        return (int) props.get("id");
    }

    public String getName() {
        return (String) props.get("name");
    }

    public String getPass() {
        return (String) props.get("pass");
    }
}
