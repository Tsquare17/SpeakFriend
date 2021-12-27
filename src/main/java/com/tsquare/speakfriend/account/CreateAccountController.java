package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.state.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CreateAccountController extends AccountController {
    @FXML private VBox account_container;
    @FXML private TextField account_name;
    @FXML private TextField account_user;
    @FXML private TextField account_password;
    @FXML private TextField account_url;
    @FXML private TextField account_notes;
    @FXML private Label notice_text;
    @FXML private Button create_account_button;

    @FXML
    public void initialize() {
        account_container.setPrefHeight(Main.getStage().getHeight());
        account_container.setMinHeight(Main.getMinimumHeight());
    }

    @FXML
    public void createAccountAction() throws IOException {
        Auth auth = new Auth();
        int id = auth.getId();
        String key = auth.getKey();

        String accountName = this.getEncryptedText(key, account_name);
        if(accountName.equals("")) {
            notice_text.setText("Account name is required.");
            return;
        }

        String accountUser = this.getEncryptedText(key, account_user);
        String accountPass = this.getEncryptedText(key, account_password);
        String accountUrl = this.getEncryptedText(key, account_url);
        String accountNotes = this.getEncryptedText(key, account_notes);

        Account account = new Account();
        AccountEntity entity = account.create(id, accountName, accountUser, accountPass, accountUrl, accountNotes);

        State.setSelectedAccountId(entity.getId().getValue());

        newContainerScene("account-details");
    }
}
