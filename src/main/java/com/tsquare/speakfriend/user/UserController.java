package com.tsquare.speakfriend.user;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.database.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

public class UserController extends Controller
{
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Text notice_text;
    private int clickCount;

    @FXML
    protected void loginAction() throws IOException {
        Auth auth = new Auth();
        if(auth.checkIn(username.getText().trim(), password.getText())) {
            AccountController accountController = new AccountController();
            accountController.listAccountsView();
        } else {
            notice_text.setText("The user or password entered was incorrect.");
        }
    }

    @FXML
    protected void loginEnterKeyAction(KeyEvent event) throws IOException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.loginAction();
        }
    }

    @FXML
    protected void registerView() throws IOException {
        this.newScene("register");
    }

    @FXML
    protected void registerSubmitAction() {
        if(username.getText().isEmpty() || password.getText().isEmpty() || confirm_password.getText().isEmpty()) {
            notice_text.setText("You must fill out all fields.");
        } else if(!password.getText().equals(confirm_password.getText())) {
            notice_text.setText("The password you entered doesn't match the confirmation.");
        } else {
            User user = new User();
            boolean success = user.create(username.getText().trim(), password.getText());

            if(!success) {
                notice_text.setText("A user with that name already exists.");
                return;
            }

            notice_text.setFill(Color.rgb(255,255,255));
            notice_text.setText("Successfully created account.");

            this.transitionScene("sign-in", 2);
        }
    }

    @FXML
    protected void registerEnterKeyAction(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.registerSubmitAction();
        }
    }

    @FXML
    public void deleteUserAction() throws IOException {
        if(!this.password.getText().equals(this.confirm_password.getText())) {
            this.notice_text.setText("Password mismatch. Please confirm.");
            this.clickCount = 0;
            return;
        }

        Auth auth = new Auth();
        String userName = auth.getName();

        if(!auth.checkIn(userName, password.getText())) {
            this.notice_text.setText("The password entered was incorrect.");
            this.clickCount = 0;
            return;
        }

        if(this.clickCount < 1) {
            this.notice_text.setText("Click delete once more to permanently remove this account.");
            this.clickCount++;
            return;
        }

        User user = new User();
        user.delete(auth.getId());

        this.newScene("sign-in");
    }

    @FXML
    protected void entryView() throws IOException {
        this.newScene("sign-in");
    }

    @FXML
    public void accountListView() throws IOException {
        AccountController accountController = new AccountController();
        accountController.listAccountsView();
    }
}
