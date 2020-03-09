package com.tsquare.speakfriend.settings;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.database.settings.Setting;
import com.tsquare.speakfriend.main.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class SettingsController extends Controller {
    @FXML ChoiceBox<String> auto_logout_time;
    @FXML Text notice_text;


    @FXML
    public void initialize() {
        auto_logout_time.getItems().addAll("5 minutes", "10 minutes", "30 minutes", "1 hour", "2 hours", "4 hours", "8 hours", "Never");
    }

    @FXML
    public void saveSettingsAction() {
        Setting setting = new Setting();

        if(auto_logout_time.getValue().equals("Never")) {
            setting.delete("auto_logout_time");
        }
        // setting.update("auto_logout_time", "5");
    }

    @FXML
    public void accountListView() throws IOException {
        AccountController accountController = new AccountController();
        accountController.listAccountsView();
    }
}
