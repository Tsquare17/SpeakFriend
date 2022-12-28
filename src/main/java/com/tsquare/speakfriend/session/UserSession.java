package com.tsquare.speakfriend.session;

import com.tsquare.speakfriend.database.entity.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserSession {
    private static UserSession instance;
    private UserEntity user;
    private String key;
    private int version;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public void setUser(ResultSet userRow) throws SQLException {
        this.user = new UserEntity(userRow);
    }

    public int getId() {
        return this.user.getId();
    }

    public String getName() {
        return this.user.getName();
    }

    public String getPassHash() {
        return this.user.getPass();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public void clear() {
        this.user = null;
        this.key = null;
        this.version = 0;
        instance = null;
    }
}
