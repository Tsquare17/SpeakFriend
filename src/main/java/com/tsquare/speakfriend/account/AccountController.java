package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.main.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;


public class AccountController {
    @FXML private TextField account_name;
    @FXML private PasswordField password;
    @FXML private TextField url;
    @FXML private TextArea notes;
    @FXML private Label errorMessage;

    @FXML protected void createAccountAction(ActionEvent event) {
        Auth auth = new Auth();
        int id = auth.getId();
        String pass;

        if(!password.getText().isEmpty()) {
            pass = auth.encrypt(id, password.getText());
        } else {
            pass = null;
        }

        Account account = new Account();
        account.create(id, account_name.getText(), pass, url.getText(), notes.getText());
        errorMessage.setText("Account Created");
    }

    @FXML protected void createAccountView(ActionEvent event) throws IOException {
        Controller controller = new Controller();
        controller.newScene("create-account");
    }

    @FXML protected void accountsView(ActionEvent event) throws IOException {
        Controller controller = new Controller();
        controller.newScene("account-list");
    }
}
