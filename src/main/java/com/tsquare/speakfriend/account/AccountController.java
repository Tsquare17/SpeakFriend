package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.main.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jetbrains.exposed.sql.SizedIterable;

import java.io.IOException;

public class AccountController extends Controller {
    @FXML private TextField account_name;
    @FXML private PasswordField password;
    @FXML private TextField url;
    @FXML private TextArea notes;
    @FXML private Label errorMessage;

    @FXML
    public void createAccountAction(ActionEvent event) {
        Auth auth = new Auth();
        int id = auth.getId();
        String pass;

        if(!password.getText().isEmpty()) {
            pass = Crypt.encrypt(id, password.getText());
        } else {
            pass = null;
        }

        Account account = new Account();
        account.create(id, account_name.getText(), pass, url.getText(), notes.getText());
        errorMessage.setText("Account Created");
    }

    @FXML
    public void createAccountView(ActionEvent event) throws IOException {
        this.newScene("create-account");
    }

    @FXML
    public void accountsView(ActionEvent event) throws IOException {
        this.newScene("account-list");

        Auth auth = new Auth();
        int id = auth.getId();
        // get accounts by user and insert them into view
        Account account = new Account();
        SizedIterable<AccountEntity> accountList = account.getByUserId(id);
        String test = "test";
    }
}
