package com.tsquare.speakfriend.session;

import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.entity.AccountPreviewEntity;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.database.entity.AccountEntity;
import com.tsquare.speakfriend.utils.AccountPreviewComparator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class AccountListSession {
    private static AccountListSession instance;

    private ArrayList<AccountEntity> accounts = new ArrayList<>();

    private ArrayList<AccountPreviewEntity> accountPreviewList = new ArrayList<>();

    private ArrayList<List<String>> stagedImportList = new ArrayList<>();

    private boolean locked = true;

    private AccountListSession() {}

    public static AccountListSession getInstance() {
        if (instance == null) {
            instance = new AccountListSession();
        }

        return instance;
    }

    public ArrayList<AccountEntity> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<AccountEntity> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<AccountEntity> unlock(String key) throws SQLException, InvalidAlgorithmParameterException,
        NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException,
        BadPaddingException, InvalidKeyException {
        if (!locked) {
            return accounts;
        }

        UserSession userSession = UserSession.getInstance();

        AccountsModel accountsModel = new AccountsModel();
        ResultSet resultSet = accountsModel.getUserAccounts(userSession.getId());

        do {
            AccountEntity account = new AccountEntity(resultSet);
            account.decrypt(key);
            accounts.add(account);
        } while (resultSet.next());

        return accounts;
    }

    public List<AccountPreviewEntity> getPreviews() {
        ArrayList<AccountPreviewEntity> previewList = new ArrayList<>();

        ApplicationSession applicationSession = ApplicationSession.getInstance();

        if (!applicationSession.isDirtyAccounts()) {
            return accountPreviewList;
        }

        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();

        try {
            AccountsModel accountsModel = new AccountsModel();
            ResultSet resultSet = accountsModel.getUserAccounts(userSession.getId());

            while(resultSet.next()) {
                AccountPreviewEntity accountPreview = new AccountPreviewEntity(resultSet);

                String accountName = Crypt.decrypt(key, accountPreview.getName());

                accountPreview.setName(accountName);

                previewList.add(accountPreview);
            }

            // Sort the list of accounts by name.
            previewList.sort(new AccountPreviewComparator<String>());
        } catch (
            SQLException e) {
            e.printStackTrace();
        }

        applicationSession.setDirtyAccounts(false);

        return previewList;
    }

    public ArrayList<List<String>> getDecryptedAccounts(List<Integer> list) throws SQLException {
        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();

        AccountsModel accountsModel = new AccountsModel();
        ResultSet resultSet;
        if (list.isEmpty()) {
            resultSet = accountsModel.getUserAccounts(userSession.getId());
        } else {
            resultSet = accountsModel.getAccountsByIds(list);
        }

        ArrayList<List<String>> accountList = new ArrayList<>();
        while(resultSet.next()) {
            String accountId = resultSet.getString("id");
            String accountName = Crypt.decrypt(key, resultSet.getString("name"));
            String accountUser = Crypt.decrypt(key, resultSet.getString("user"));
            String accountPass = Crypt.decrypt(key, resultSet.getString("pass"));
            String accountUrl = Crypt.decrypt(key, resultSet.getString("url"));
            String accountNotes = Crypt.decrypt(key, resultSet.getString("notes"));

            List<String> addAccount = new ArrayList<>();
            addAccount.add(accountId);
            addAccount.add(accountName);
            addAccount.add(accountUser);
            addAccount.add(accountPass);
            addAccount.add(accountUrl);
            addAccount.add(accountNotes);

            accountList.add(addAccount);
        }

        return accountList;
    }

    public ArrayList<List<String>> lock(ArrayList<List<String>> accounts, String key) {
        ArrayList<List<String>> list = new ArrayList<List<String>>();
        for (List<String> account : accounts) {
            List<String> accountFields = new ArrayList<>();

            for (int i = 0; i < accounts.size(); i++) {
                if (i == 0) {
                    accountFields.add(account.get(i));
                } else {
                    try {
                        accountFields.add(
                            Crypt.encrypt(key, account.get(i), 2000)
                        );
                    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                }

                list.add(accountFields);
            }
        }

        return list;
    }

     public List<AccountEntity> unlockAccountObjects(List<AccountEntity> accounts, String key) {
        ArrayList<AccountEntity> list = new ArrayList<>();
        for (AccountEntity account : accounts) {

            String name = Crypt.decrypt(key, account.getName(), 2000);
            String user = Crypt.decrypt(key, account.getUser(), 2000);
            String pass = Crypt.decrypt(key, account.getPass(), 2000);
            String url = Crypt.decrypt(key, account.getUrl(), 2000);
            String notes = Crypt.decrypt(key, account.getNotes(), 2000);

            AccountEntity addAccount = new AccountEntity(
                account.getId(),
                name,
                user,
                pass,
                url,
                notes
            );

            list.add(addAccount);
        }

        return list;
    }

    public void stageImports(ArrayList<List<String>> accounts) {
        stagedImportList = accounts;
    }

    public ArrayList<List<String>> getStagedImports() {
        return stagedImportList;
    }

    public void clear() {
        accounts = null;
        accountPreviewList = null;
        stagedImportList = null;
    }
}
