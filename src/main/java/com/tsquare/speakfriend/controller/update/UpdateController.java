package com.tsquare.speakfriend.controller.update;

import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.entity.AccountEntity;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.database.model.SystemSettingsModel;
import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.controller.main.Nav;
import com.tsquare.speakfriend.session.UserSession;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateController {
    private final String upToDateDbVersion = "101";
    private final String upToDateSysVersion = "100";

    public void update() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws SQLException {
                UserSession userSession = UserSession.getInstance();

                SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
                ResultSet resultSet = systemSettingsModel.getSystemSetting("first_run");

                String firstSystemRun = resultSet.getString("value");
                if (firstSystemRun.equals("")) {
                    systemSettingsModel.createSystemSetting("version", upToDateSysVersion);
                    systemSettingsModel.createSystemSetting("first_run", "false");
                }

                UserSettingsModel userSettingsModel = new UserSettingsModel();
                resultSet = userSettingsModel.getUserSetting(
                    userSession.getId(),
                    "first_run"
                );

                String firstRun = resultSet.getString("value");
                if (firstRun.equals("")) {
                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "auto_logout_time",
                        "0"
                    );
                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "db_version",
                        upToDateDbVersion
                    );
                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "first_run",
                        "false"
                    );
                }

                resultSet = userSettingsModel.getUserSetting(userSession.getId(), "db_version");
                int dbVersion = resultSet.getInt("value");
                if (dbVersion == 0) {
                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "db_version",
                        "100"
                    );
                    userSettingsModel.createUserSetting(
                        userSession.getId(),
                        "auto_logout_time",
                        "0"
                    );

                    dbVersion = 100;
                }

                if (dbVersion < 101) {
                    UpdateController.changeEncryptionIterations(65536, 2000);
                    userSettingsModel.updateUserSetting(
                        userSession.getId(),
                        "db_version",
                        "101"
                    );
                    dbVersion = 101;
                }

                resultSet = systemSettingsModel.getSystemSetting("auto_logout_time");

                String durationSetting = resultSet.getString("value");
                if (!durationSetting.equals("0") && !durationSetting.isEmpty()) {
                    int duration = Integer.parseInt(durationSetting);
                    Duration delay = Duration.minutes(duration);
                    Main.transition = new PauseTransition(delay);
                }

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
        String dbVersion = resultSet.getString("value");
        if (dbVersion.equals("")) {
            return true;
        }

        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();

        resultSet = systemSettingsModel.getSystemSetting("version");
        String systemVersion = resultSet.getString("value");
        if (systemVersion == null) {
            return true;
        }

        int version = Integer.parseInt(dbVersion);
        int sysVersion = Integer.parseInt(systemVersion);

        return version < 101 || sysVersion < 100;
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
                accountName = Crypt.encrypt(key, accountName, iterationsAfter);
                accountUser = Crypt.encrypt(key, accountUser, iterationsAfter);
                accountPass = Crypt.encrypt(key, accountPass, iterationsAfter);
                accountUrl = Crypt.encrypt(key, accountUrl, iterationsAfter);
                accountNotes = Crypt.encrypt(key, accountNotes, iterationsAfter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            accountsModel.updateAccount(accountId, accountName, accountUser, accountPass, accountUrl, accountNotes);
        }
    }

    private static String decrypt(String key, String string, int iterations) {
        try {
            string = Crypt.decrypt(key, string, iterations);
        } catch (Exception e) {
            string = "";
        }

        return string;
    }
}
