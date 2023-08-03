package com.tsquare.speakfriend.controller.update;

import com.tsquare.speakfriend.database.connection.SqliteConnection;
import com.tsquare.speakfriend.database.entity.AccountEntity;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.database.model.SystemSettingsModel;
import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.controller.main.Nav;
import com.tsquare.speakfriend.database.schema.Builder;
import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.utils.Crypt;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateController {
    private final String upToDateDbVersion = "102";
    private final String upToDateSysVersion = "101";

    public void update() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws SQLException {
                UserSession userSession = UserSession.getInstance();

                SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
                ResultSet resultSet = systemSettingsModel.getSystemSetting("version");

                int systemVersion = 0;
                if (resultSet.next()) {
                    systemVersion = resultSet.getInt("value");
                }

                resultSet.close();
                systemSettingsModel.reset();

                if (systemVersion < 101) {
                    Builder builder = new Builder();
                    builder.renameTable("Accounts", "accounts_tmp");
                    builder.reset();

                    builder.renameTable("accounts_tmp", "accounts");
                    builder.reset();

                    builder.dropTable("system_settings");
                    builder.reset();

                    builder.renameTable("SystemSettings", "system_settings_tmp");
                    builder.reset();

                    builder.renameTable("system_settings_tmp", "system_settings");
                    builder.reset();

                    builder.dropTable("user_settings");
                    builder.reset();

                    builder.renameTable("Settings", "user_settings");
                    builder.reset();

                    builder.renameTable("Users", "users_tmp");
                    builder.reset();

                    builder.renameTable("users_tmp", "users");
                    builder.close();
                }

                resultSet = systemSettingsModel.getSystemSetting("first_run");

                if (!resultSet.next() || resultSet.getString("value").equals("")) {
                    systemSettingsModel.createSystemSetting("version", upToDateSysVersion);
                    systemSettingsModel.createSystemSetting("first_run", "false");
                }

                resultSet.close();
                systemSettingsModel.reset();

                UserSettingsModel userSettingsModel = new UserSettingsModel();
                resultSet = userSettingsModel.getUserSetting(userSession.getId(), "db_version");
                int userDbVersion = 0;
                if (resultSet.next()) {
                    userDbVersion = resultSet.getInt("value");
                }

                resultSet.close();
                userSettingsModel.reset();

                resultSet = userSettingsModel.getUserSetting(
                    userSession.getId(),
                    "first_run"
                );

                if (!resultSet.next() || resultSet.getString("value").equals("")) {
                    resultSet.close();

                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "auto_logout_time",
                        "0"
                    );
                    userSettingsModel.reset();

                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "db_version",
                        upToDateDbVersion
                    );
                    userSettingsModel.reset();

                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "first_run",
                        "false"
                    );
                    userSettingsModel.reset();
                } else {
                    resultSet.close();
                }

                if (userDbVersion == 0) {
                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "db_version",
                        "100"
                    );
                    userSettingsModel.reset();

                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "auto_logout_time",
                        "0"
                    );
                    userSettingsModel.reset();

                    userDbVersion = 100;
                }

                if (userDbVersion < 101) {
                    UpdateController.changeEncryptionIterations(65536, 2000);
                    userSettingsModel.updateUserSetting(
                        userSession.getId(),
                        "db_version",
                        "101"
                    );
                    userSettingsModel.reset();

                    userDbVersion = 101;
                }

                if (userDbVersion < 102) {
                    userSettingsModel.updateUserSetting(
                        userSession.getId(),
                        "db_version",
                        "102"
                    );
                    userSettingsModel.reset();
                }

                resultSet = userSettingsModel.getUserSetting(userSession.getId(), "auto_logout_time");

                String durationSetting = resultSet.getString("value");
                if (!durationSetting.equals("0") && !durationSetting.isEmpty()) {
                    int duration = Integer.parseInt(durationSetting);
                    Duration delay = Duration.minutes(duration);
                    Main.transition = new PauseTransition(delay);
                }

                resultSet.close();
                userSettingsModel.close();

                if (systemVersion < 101) {
                    systemSettingsModel.updateSystemSetting("version", "101");
                }

                systemSettingsModel.close();

                return null;
            }
        };

        task.setOnSucceeded(taskFinishEvent -> {
            Nav nav = new Nav();
            nav.toAccounts();
        });
        new Thread(task).start();
    }

    public static boolean checkUpdate() throws SQLException {
        UserSession userSession = UserSession.getInstance();
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        ResultSet resultSet = userSettingsModel.getUserSetting(userSession.getId(), "db_version");
        if (!resultSet.next()) {
            resultSet.close();
            userSettingsModel.close();

            return true;
        }

        String dbVersion = resultSet.getString("value");

        resultSet.close();
        userSettingsModel.close();

        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();

        resultSet = systemSettingsModel.getSystemSetting("version");
        if (!resultSet.next()) {
            resultSet.close();
            systemSettingsModel.close();

            return true;
        }

        String systemVersion = resultSet.getString("value");

        resultSet.close();
        systemSettingsModel.close();

        int version = Integer.parseInt(dbVersion);
        int sysVersion = Integer.parseInt(systemVersion);

        return version < 102 || sysVersion < 100;
    }

    private static void changeEncryptionIterations(int iterationsBefore, int iterationsAfter) throws SQLException {
        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();

        AccountsModel accountsModel = new AccountsModel();
        ResultSet resultSet = accountsModel.getUserAccounts(userSession.getId());

        while(resultSet.next()) {
            AccountEntity account = new AccountEntity(resultSet);

            int accountId = account.getId();

            String accountName = UpdateController.decrypt(key, account.getName(), iterationsBefore);
            String accountUser = UpdateController.decrypt(key, account.getUser(), iterationsBefore);
            String accountPass = UpdateController.decrypt(key, account.getPass(), iterationsBefore);
            String accountUrl  = UpdateController.decrypt(key, account.getUrl(), iterationsBefore);
            String accountNotes = UpdateController.decrypt(key, account.getNotes(), iterationsBefore);

            try {
                Crypt crypt = new Crypt();
                accountName = crypt.encrypt(key, accountName, iterationsAfter);
                accountUser = crypt.encrypt(key, accountUser, iterationsAfter);
                accountPass = crypt.encrypt(key, accountPass, iterationsAfter);
                accountUrl = crypt.encrypt(key, accountUrl, iterationsAfter);
                accountNotes = crypt.encrypt(key, accountNotes, iterationsAfter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            accountsModel.updateAccount(accountId, accountName, accountUser, accountPass, accountUrl, accountNotes);
        }

        resultSet.close();
        accountsModel.close();
    }

    private static String decrypt(String key, String string, int iterations) {
        try {
            Crypt crypt = new Crypt();
            string = crypt.decrypt(key, string, iterations);
        } catch (Exception e) {
            string = "";
        }

        return string;
    }
}
