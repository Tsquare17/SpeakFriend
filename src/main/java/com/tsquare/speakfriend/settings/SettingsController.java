package com.tsquare.speakfriend.settings;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.database.settings.Setting;
import com.tsquare.speakfriend.main.Controller;

import com.tsquare.speakfriend.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.Objects;

public class SettingsController extends Controller {
    @FXML ChoiceBox<String> auto_logout_time;

    @FXML
    public void initialize() {
        auto_logout_time.getItems().addAll("5 minutes", "10 minutes", "30 minutes", "1 hour", "2 hours", "4 hours", "8 hours", "Never");

        Setting setting = new Setting();
        String durationSetting = Objects.requireNonNull(setting.getOption("auto_logout_time")).getValue();

        String duration;
        switch (durationSetting) {
            case "5":
                duration = "5 minutes";
                break;
            case "10":
                duration = "10 minutes";
                break;
            case "30":
                duration = "30 minutes";
                break;
            case "60":
                duration = "1 hour";
                break;
            case "120":
                duration = "2 hours";
                break;
            case "240":
                duration = "4 hours";
                break;
            case "480":
                duration = "8 hours";
                break;
            default:
                duration = "0";
        }

        auto_logout_time.getSelectionModel().select(duration);
    }

    @FXML
    public void saveSettingsAction() {
        Setting setting = new Setting();

        String durationSetting;
        switch (auto_logout_time.getValue()) {
            case "5 minutes":
                durationSetting = "5";
                break;
            case "10 minutes":
                durationSetting = "10";
                break;
            case "30 minutes":
                durationSetting = "30";
                break;
            case "1 hour":
                durationSetting = "60";
                break;
            case "2 hours":
                durationSetting = "120";
                break;
            case "4 hours":
                durationSetting = "240";
                break;
            case "8 hours":
                durationSetting = "480";
                break;
            default:
                durationSetting = "0";
        }

        setting.update("auto_logout_time", durationSetting);
        int duration = Integer.parseInt(durationSetting);
        Main.setNewTimer(duration);
    }

    @FXML
    public void accountListView() {
        toAccounts();
    }
}
