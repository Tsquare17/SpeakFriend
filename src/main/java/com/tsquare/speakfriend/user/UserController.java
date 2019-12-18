package com.tsquare.speakfriend.user;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.database.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

public class UserController extends Controller
{
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Text errorMessage;

    @FXML
    protected void loginAction(ActionEvent event) throws IOException {
        Auth auth = new Auth();
        if(auth.checkIn(username.getText().trim(), password.getText())) {
            AccountController accountController = new AccountController();
            accountController.listAccountsView(event);
        } else {
            errorMessage.setText("The user or password entered was incorrect.");
        }
    }

    @FXML
    protected void registerView(ActionEvent event) throws IOException {
        this.newScene("register");
    }

    @FXML
    protected void registerSubmitAction(ActionEvent event) throws IOException {
        if(username.getText().isEmpty() || password.getText().isEmpty() || confirm_password.getText().isEmpty()) {
            errorMessage.setText("You must fill out all fields.");
        } else if(!password.getText().equals(confirm_password.getText())) {
            errorMessage.setText("The password you entered doesn't match the confirmation.");
        } else {
            User user = new User();
            user.create(username.getText().trim(), password.getText());
            errorMessage.setFill(Color.rgb(255,255,255));
            errorMessage.setText("Successfully created account.");

            this.transitionScene("entry", 2);
        }
    }

    @FXML
    protected void entryView(ActionEvent event) throws IOException {
        this.newScene("entry");
    }
}
