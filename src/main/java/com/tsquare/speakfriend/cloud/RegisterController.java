package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.main.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class RegisterController extends Controller {
    @FXML TextField name;
    @FXML TextField email;
    @FXML PasswordField password;
    @FXML PasswordField confirm_password;
    @FXML Text notice_text;

    @FXML
    protected void registerSubmitAction() {
        if(
            name.getText().isEmpty()
            || email.getText().isEmpty()
            || password.getText().isEmpty()
            || confirm_password.getText().isEmpty()
        ) {
            notice_text.setText("You must fill out all fields.");
            return;
        } else if (!password.getText().equals(confirm_password.getText())) {
            notice_text.setText("The password you entered doesn't match the confirmation.");
            return;
        }

        Api api = new Api();
        ApiResponse response = api.register(name.getText(), email.getText(), password.getText(), confirm_password.getText());

        if (response.getResponseMessage().equals("OK")) {
            notice_text.setFill(Color.rgb(255,255,255));
            notice_text.setText("Successfully created account.");

            // this.transitionScene("sign-in", 2);
        } else {
            notice_text.setFill(Color.rgb(255,50,50));
            notice_text.setText("An unknown error occurred.");
        }
    }

    @FXML
    protected void registerEnterKeyAction(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.registerSubmitAction();
        }
    }

    @FXML
    protected void goBack() throws IOException {
        this.newContainerScene("account-list");
    }
}
