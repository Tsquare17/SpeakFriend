package com.tsquare.speakfriend.update;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.main.Nav;
import com.tsquare.speakfriend.settings.Options;
import com.tsquare.speakfriend.settings.SystemSettings;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;
import java.sql.SQLException;
import java.util.List;

public class UpdateController {
    private final String upToDateDbVersion = "101";
    private final String upToDateSysVersion = "100";

    public void update() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                Auth auth = new Auth();

                String firstSystemRun = SystemSettings.get("first_run");
                if (firstSystemRun.equals("")) {
                    SystemSettings.put("version", upToDateSysVersion);
                    SystemSettings.put("first_run", "false");
                }

                String firstRun = Options.get("first_run");
                if (firstRun.equals("")) {
                    Options.put("auto_logout_time", "0");
                    Options.put("db_version", upToDateDbVersion);
                    Options.put("first_run", "false");
                }

                int dbVersion = auth.getVersion();
                if (dbVersion == 0) {
                    Options.put("db_version", "100");
                    Options.put("auto_logout_time", "0");
                    dbVersion = 100;
                }

                if (dbVersion < 101) {
                    UpdateController.changeEncryptionIterations(65536, 2000);
                    Options.put("db_version", "101");
                    dbVersion = 101;
                }

                String durationSetting = Options.get("auto_logout_time");
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

    public static boolean checkUpdate() {
        String dbVersion = Options.get("db_version");
        if (dbVersion.equals("")) {
            return true;
        }

        String systemVersion = SystemSettings.get("version");
        if (systemVersion.equals("")) {
            return true;
        }

        int version = Integer.parseInt(dbVersion);
        int sysVersion = Integer.parseInt(systemVersion);

        return version < 101 || sysVersion < 100;
    }

    private static void changeEncryptionIterations(int iterationsBefore, int iterationsAfter) {
        Auth auth  = new Auth();
        int userId     = auth.getId();
        String key = auth.getKey();
        List<AccountEntity> accounts = AccountList.get(userId);

        for (AccountEntity account: accounts) {
            int accountId = account.getId().getValue();

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

            Account updatedAccount = new Account();
            updatedAccount.update(accountId, accountName, accountUser, accountPass, accountUrl, accountNotes);
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
