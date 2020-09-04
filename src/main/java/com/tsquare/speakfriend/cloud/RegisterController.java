package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.main.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.Set;

public class RegisterController extends Controller {
    @FXML TextField name;
    @FXML TextField email;
    @FXML PasswordField password;
    @FXML PasswordField confirm_password;
    @FXML Text notice_text;
    @FXML Button submit_button;

    @FXML
    protected void registerSubmitAction() {
        submit_button.setDisable(true);

        Api api = new Api();
        ApiResponse response = api.register(name.getText(), email.getText(), password.getText(), confirm_password.getText());

        if (response.getResponseMessage().equals("OK")) {
            notice_text.setFill(Color.rgb(255,255,255));
            notice_text.setText("Successfully created account.");

            this.transitionToAccounts();
        } else {
            notice_text.setFill(Color.rgb(255,50,50));
            notice_text.setText("An unknown error occurred.");
            submit_button.setDisable(false);

            StringBuilder errorText = ApiResponse.errors;

            notice_text.setText(errorText.toString());
        }
    }

    @FXML
    protected void registerEnterKeyAction(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.registerSubmitAction();
        }
    }

    @FXML
    protected void backToAccounts() throws IOException {
        AccountController accountController = new AccountController();
        accountController.listAccountsView();
    }
}
