package com.tsquare.speakfriend.controller.help;

import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AboutController extends Controller {
    @FXML Label system_version;
    @FXML Label db_version;
    @FXML Label java_version;

    @FXML
    public void initialize() throws SQLException {
        system_version.setText(Main.version);

        UserSession userSession = UserSession.getInstance();
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        ResultSet resultSet = userSettingsModel.getUserSetting(userSession.getId(), "db_version");

        String version = resultSet.getString("value");
        String[] rawVersion = version.split("(?!^)");
        String dbVersion = rawVersion[0] + '.' + rawVersion[1] + '.' + rawVersion[2];
        db_version.setText(dbVersion);

        java_version.setText(System.getProperty("java.version"));
    }
}
