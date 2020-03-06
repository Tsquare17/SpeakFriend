package com.tsquare.speakfriend.settings;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.main.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class SettingsController extends Controller {
    @FXML ChoiceBox<String> auto_logout_time;
    @FXML Text notice_text;

    @FXML
    public void saveSettingsAction() {
        
    }

    @FXML
    public void accountListView() throws IOException {
        AccountController accountController = new AccountController();
        accountController.listAccountsView();
    }
}
