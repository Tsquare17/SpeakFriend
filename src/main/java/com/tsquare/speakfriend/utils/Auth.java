package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.database.model.UsersModel;
import com.tsquare.speakfriend.session.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    public boolean checkIn(String name, String pass) throws SQLException {
        UsersModel usersModel = new UsersModel();

        ResultSet resultSet = usersModel.getUser(name);

        if (!resultSet.next()) {
            return false;
        }

        if (!Crypt.match(pass, resultSet.getString("pass"))) {
            return false;
        }

        UserSession userSession = UserSession.getInstance();
        userSession.setUser(resultSet);
        userSession.setKey(Crypt.generateKey(userSession.getPassHash(), pass));

        UserSettingsModel userSettingsModel = new UserSettingsModel();
        ResultSet settingsResult = userSettingsModel.getUserSetting(userSession.getId(), "db_version");

        if (settingsResult.next()) {
            userSession.setVersion(Integer.parseInt(settingsResult.getString("value")));
        }

        return true;
    }
}
