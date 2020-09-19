package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.settings.Options;
import com.tsquare.speakfriend.state.State;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CreateMasterKeyController extends Controller {
    @FXML PasswordField master_key;
    @FXML PasswordField confirm_key;
    @FXML Text notice_text;

    @FXML
    public void createKeySubmitAction() {
        if (master_key.getText().isEmpty() || confirm_key.getText().isEmpty()) {
            notice_text.setText("You must fill out both fields.");
            return;
        }

        if (!master_key.getText().equals(confirm_key.getText())) {
            notice_text.setText("Master key mismatch");
            return;
        }

        Api api = new Api();
        ApiResponse response = api.createMasterKey(master_key.getText());

        if (response.getResponseMessage().equals("OK")) {
            Auth auth = new Auth();
            auth.createApiKey(master_key.getText());

            transitionToAccounts();
        } else {
            notice_text.setFill(Color.rgb(255,50,50));

            StringBuilder errorText = ApiResponse.errors;

            if (errorText != null) {
                notice_text.setText(errorText.toString());
            } else {
                notice_text.setText("An unknown error occurred");
            }
        }
    }

    @FXML
    public void enterKeyAction(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            createKeySubmitAction();
        }
    }
}
