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

        String duration;
        switch (auto_logout_time.getValue()) {
            case "5 minutes":
                duration = "5";
                break;
            case "10 minutes":
                duration = "10";
                break;
            case "30 minutes":
                duration = "30";
                break;
            case "1 hour":
                duration = "60";
                break;
            case "2 hours":
                duration = "120";
                break;
            case "4 hours":
                duration = "240";
                break;
            case "8 hours":
                duration = "480";
                break;
            default:
                duration = "0";
        }
        setting.update("auto_logout_time", duration);
    }

    @FXML
    public void accountListView() throws IOException {
        AccountController accountController = new AccountController();
        accountController.listAccountsView();
    }
}
