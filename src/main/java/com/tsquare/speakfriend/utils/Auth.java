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

        String storedPassword = resultSet.getString("pass");
        boolean passwordValid = crypt.match(pass, storedPassword);

        if (!passwordValid) {
            resultSet.close();
            usersModel.close();
            return false;
        }

        int userId = resultSet.getInt("id");
        String userName = resultSet.getString("name");

        UserSession userSession = UserSession.getInstance();
        userSession.setUser(resultSet);

        resultSet.close();
        usersModel.close();

        try {
            // Generate new key
            String newKey = crypt.generateKey(userSession.getPassHash(), pass);
            userSession.setKey(newKey);

            // Also generate legacy key for migration purposes
            String legacyKey = crypt.generateLegacyKeyFromHash(userSession.getPassHash(), pass);
            userSession.setLegacyKey(legacyKey);
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

        // Check if password needs to be upgraded to new format (after migration)
        if (shouldUpgradePassword(storedPassword)) {
            try {
                String newPasswordHash = crypt.generatePassword(pass);
                // Update with new password hash
                UsersModel updateModel = new UsersModel();
                updateModel.updateUser(userId, userName, newPasswordHash);
                updateModel.close();
            } catch (Exception e) {
                // If upgrade fails, user can still log in but will probably not be able to decrypt accounts
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Check if a password hash needs to be upgraded to the new format
     * Old format uses SHA1, new format uses SHA256
     */
    private boolean shouldUpgradePassword(String storedPassword) {
        try {
            String[] parts = storedPassword.split(":");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            // If iterations are low, upgrade is needed
            return iterations < 60000;
        } catch (Exception e) {
            return false;
        }
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
