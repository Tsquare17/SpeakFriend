package com.tsquare.speakfriend.session;

import com.tsquare.speakfriend.database.entity.AccountPreviewEntity;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.database.entity.AccountEntity;
import com.tsquare.speakfriend.database.model.TagsModel;
import com.tsquare.speakfriend.utils.AccountPreviewComparator;
import com.tsquare.speakfriend.utils.Crypt;

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
        ApplicationSession applicationSession = ApplicationSession.getInstance();

        if (!applicationSession.isDirtyAccounts()) {
            return accountPreviewList;
        }

        accountPreviewList.clear();

        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();
        Crypt crypt = new Crypt();

        try {
            AccountsModel accountsModel = new AccountsModel();
            ResultSet resultSet = accountsModel.getUserAccounts(userSession.getId());

            while (resultSet.next()) {
                AccountPreviewEntity accountPreview = new AccountPreviewEntity(resultSet);

                String accountName = crypt.decrypt(key, accountPreview.getName());

                accountPreview.setName(accountName);

                accountPreviewList.add(accountPreview);
            }

            resultSet.close();

            accountsModel.close();

            // Sort the list of accounts by name.
            accountPreviewList.sort(new AccountPreviewComparator<String>());
        } catch (Exception e) {
            e.printStackTrace();
        }

        applicationSession.setDirtyAccounts(false);

        return accountPreviewList;
    }

    public ArrayList<List<String>> getDecryptedAccounts(List<Integer> list) throws SQLException {
        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();
        Crypt crypt = new Crypt();

        AccountsModel accountsModel = new AccountsModel();
        ResultSet resultSet;
        if (list.isEmpty()) {
            resultSet = accountsModel.getUserAccounts(userSession.getId());
        } else {
            resultSet = accountsModel.getAccountsByIds(list);
        }

        ArrayList<List<String>> accountList = new ArrayList<>();
        while (resultSet.next()) {
            String accountId = null;
            String accountName = null;
            String accountUser = null;
            String accountPass = null;
            String accountUrl = null;
            String accountNotes = null;

            try {
                accountId = resultSet.getString("id");
                accountName = crypt.decrypt(key, resultSet.getString("name"));
                accountUser = crypt.decrypt(key, resultSet.getString("user"));
                accountPass = crypt.decrypt(key, resultSet.getString("pass"));
                accountUrl = crypt.decrypt(key, resultSet.getString("url"));
                accountNotes = crypt.decrypt(key, resultSet.getString("notes"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<String> addAccount = new ArrayList<>();
            addAccount.add(accountId);
            addAccount.add(accountName);
            addAccount.add(accountUser);
            addAccount.add(accountPass);
            addAccount.add(accountUrl);
            addAccount.add(accountNotes);

            accountList.add(addAccount);
        }

        resultSet.close();
        accountsModel.close();

        return accountList;
    }

    public ArrayList<List<String>> lock(ArrayList<List<String>> accounts, String key) throws SQLException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        ArrayList<List<String>> list = new ArrayList<>();
        Crypt crypt = new Crypt();
        TagsModel tagsModel = new TagsModel();
        for (List<String> account : accounts) {
            List<String> accountFields = new ArrayList<>();

            for (int i = 0; i < account.size(); i++) {
                if (i == 0) {
                    accountFields.add(account.get(i));
                } else {
                    try {
                        accountFields.add(
                            crypt.encrypt(key, account.get(i), 2000)
                        );
                    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                }
            }

            ResultSet resultSet = tagsModel.getAccountTags(Integer.parseInt(account.get(0)));
            StringBuilder accountTags = new StringBuilder();
            while (resultSet.next()) {
                accountTags.append(
                    crypt.encrypt(key, resultSet.getString("user_tag_name"), 2000)
                ).append("$:$");
            }

            resultSet.close();
            tagsModel.reset();

            if (!accountTags.isEmpty()) {
                accountFields.add(accountTags.toString());
            }

            list.add(accountFields);
        }

        tagsModel.close();

        return list;
    }

     public List<AccountEntity> unlockAccountObjects(List<AccountEntity> accounts, String key) {
        ArrayList<AccountEntity> list = new ArrayList<>();
        Crypt crypt = new Crypt();
        for (AccountEntity account : accounts) {

            try {
                String name = crypt.decrypt(key, account.getName(), 2000);
                String user = crypt.decrypt(key, account.getUser(), 2000);
                String pass = crypt.decrypt(key, account.getPass(), 2000);
                String url = crypt.decrypt(key, account.getUrl(), 2000);
                String notes = crypt.decrypt(key, account.getNotes(), 2000);

                AccountEntity addAccount = new AccountEntity(
                    account.getId(),
                    name,
                    user,
                    pass,
                    url,
                    notes
                );

                list.add(addAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
