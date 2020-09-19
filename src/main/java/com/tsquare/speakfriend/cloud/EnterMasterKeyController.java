package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.settings.Options;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class EnterMasterKeyController extends Controller {
    @FXML PasswordField master_key;
    @FXML Text notice_text;

    @FXML
    public void submitAction() {
        if (master_key.getText().isEmpty()) {
            notice_text.setText("You must enter your master key");
            return;
        }

        String match = Options.get("master_key");
        if (Crypt.match(match, master_key.getText())) {
            notice_text.setText("Master key accepted");
            Auth auth = new Auth();
            auth.setApiKey(match, master_key.getText());

            transitionToAccounts();
            return;
        }

        notice_text.setText("Key mismatch");
    }

    @FXML
    public void enterKeyAction(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            submitAction();
        }
    }
}
