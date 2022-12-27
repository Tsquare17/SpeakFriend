package com.tsquare.speakfriend.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountEntity extends Entity {
    AccountEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public int getId() {
        return (int) props.get("id");
    }

    public String getName() {
        return (String) props.get("name");
    }

    public String getUser() {
        return (String) props.get("user");
    }

    public String getPass() {
        return (String) props.get("pass");
    }

    public String getUrl() {
        return (String) props.get("url");
    }

    public String getNotes() {
        return (String) props.get("notes");
    }
}
