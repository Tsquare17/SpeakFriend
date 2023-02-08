package com.tsquare.speakfriend.controller.settings;

import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsController extends Controller {
    @FXML ChoiceBox<String> auto_logout_time;

    @FXML
    public void initialize() throws SQLException {
        auto_logout_time.getItems().addAll("5 minutes", "10 minutes", "30 minutes", "1 hour", "2 hours", "4 hours", "8 hours", "Never");

        UserSession userSession = UserSession.getInstance();
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        ResultSet resultSet = userSettingsModel.getUserSetting(
            userSession.getId(),
            "auto_logout_time"
        );

        String durationSetting = resultSet.getString("value");

        resultSet.close();
        userSettingsModel.close();

        String duration = switch (durationSetting) {
            case "5" -> "5 minutes";
            case "10" -> "10 minutes";
            case "30" -> "30 minutes";
            case "60" -> "1 hour";
            case "120" -> "2 hours";
            case "240" -> "4 hours";
            case "480" -> "8 hours";
            default -> "0";
        };

        auto_logout_time.getSelectionModel().select(duration);
    }

    @FXML
    public void saveSettingsAction() throws SQLException {
        String durationSetting = switch (auto_logout_time.getValue()) {
            case "5 minutes" -> "5";
            case "10 minutes" -> "10";
            case "30 minutes" -> "30";
            case "1 hour" -> "60";
            case "2 hours" -> "120";
            case "4 hours" -> "240";
            case "8 hours" -> "480";
            default -> "0";
        };

        UserSession userSession = UserSession.getInstance();
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        userSettingsModel.updateUserSetting(
            userSession.getId(),
            "auto_logout_time",
            durationSetting
        );

        userSettingsModel.close();

        int duration = Integer.parseInt(durationSetting);
        Main.setNewTimer(duration);
    }

    @FXML
    public void accountListView() {
        toAccounts();
    }
}
