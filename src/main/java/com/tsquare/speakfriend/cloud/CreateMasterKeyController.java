package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.main.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class CreateMasterKeyController extends Controller {
    @FXML PasswordField master_key;
    @FXML PasswordField confirm_key;
    @FXML Text notice_text;

    @FXML
    public void createKeySubmitAction() {

    }

    @FXML
    public void enterKeyAction() {
        createKeySubmitAction();
    }
}
