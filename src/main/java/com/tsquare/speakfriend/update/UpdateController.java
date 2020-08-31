package com.tsquare.speakfriend.update;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.settings.Options;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class UpdateController {

    public void update() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                Auth auth = new Auth();

                int dbVersion = auth.getVersion();
                if (dbVersion == 0) {
                    Options.put("db_version", "100");
                    Options.put("auto_logout_time", "0");
                    dbVersion = 100;
                }

                if (dbVersion < 100) {
                    UpdateController.changeEncryptionIterations(65536,2000);
                    Options.put("db_version", "101");
                    dbVersion = 101;
                }

                String durationSetting = Options.get("auto_logout_time");
                if (!durationSetting.equals("0")) {
                    int duration = Integer.parseInt(durationSetting);
                    Duration delay = Duration.minutes(duration);
                    Main.transition = new PauseTransition(delay);
                }

                return null;
            }
        };

        task.setOnSucceeded(taskFinishEvent -> {
            AccountController accountController = new AccountController();
            try {
                accountController.listAccountsView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        new Thread(task).start();
    }

    public static boolean checkUpdate() {
        String dbVersion = Options.get("db_version");
        if (dbVersion.equals("")) {
            return true;
        }

        int version = Integer.parseInt(dbVersion);

        return version < 101;
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
