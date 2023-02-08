package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.database.model.UsersModel;
import com.tsquare.speakfriend.session.AccountListSession;
import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.session.UserSession;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    public boolean checkIn(String name, String pass) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        Crypt crypt = new Crypt();
        UsersModel usersModel = new UsersModel();

        ResultSet resultSet = usersModel.getUser(name);

        if (!resultSet.next()) {
            resultSet.close();
            usersModel.close();

            return false;
        }

        if (!crypt.match(pass, resultSet.getString("pass"))) {
            resultSet.close();
            usersModel.close();

            return false;
        }

        UserSession userSession = UserSession.getInstance();
        userSession.setUser(resultSet);

        resultSet.close();
        usersModel.close();

        try {
            userSession.setKey(crypt.generateKey(userSession.getPassHash(), pass));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        UserSettingsModel userSettingsModel = new UserSettingsModel();
        ResultSet settingsResult = userSettingsModel.getUserSetting(userSession.getId(), "db_version");

        if (settingsResult.next()) {
            userSession.setVersion(Integer.parseInt(settingsResult.getString("value")));
        }

        settingsResult.close();
        userSettingsModel.close();

        return true;
    }

    public void checkOut() {
        UserSession userSession = UserSession.getInstance();
        userSession.clear();

        ApplicationSession applicationSession = ApplicationSession.getInstance();
        applicationSession.clear();

        AccountListSession accountListSession = AccountListSession.getInstance();
        accountListSession.clear();
    }
}
