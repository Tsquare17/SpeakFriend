package com.tsquare.speakfriend.database.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tag {
    protected int accountTagId;
    protected int userTagId;
    protected int userId;
    protected int accountId;
    protected String tagName;
    protected boolean isSelected = false;

    public Tag(ResultSet resultSet) throws SQLException {
        try {
            this.accountTagId = resultSet.getInt("account_tag_id");
        } catch (SQLException ignored) {}

         try {
             this.userTagId = resultSet.getInt("user_tag_id");
         } catch (SQLException ignored) {}

         try {
             this.userId = resultSet.getInt("user_id");
         } catch (SQLException ignored) {}

         try {
             this.accountId = resultSet.getInt("account_id");
         } catch (SQLException ignored) {}

         try {
             this.tagName = resultSet.getString("user_tag_name");
         } catch (SQLException ignored) {}

    }

    public int getAccountTagId() {
        return this.accountTagId;
    }

    public int getUserTagId() {
        return this.userTagId;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setAccountTagId(int accountTagId) {
        this.accountTagId = accountTagId;
    }

    public void setUserTagId(int userTagId) {
        this.userTagId = userTagId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
