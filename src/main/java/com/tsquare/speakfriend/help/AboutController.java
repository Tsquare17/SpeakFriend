package com.tsquare.speakfriend.help;

import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.settings.Options;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutController extends Controller {
    @FXML Label system_version;
    @FXML Label db_version;
    @FXML Label java_version;

    @FXML
    public void initialize() {
        system_version.setText(Main.version);

        String[] rawVersion = Options.get("db_version").split("(?!^)");
        String dbVersion = rawVersion[0] + '.' + rawVersion[1] + '.' + rawVersion[2];
        db_version.setText(dbVersion);

        java_version.setText(System.getProperty("java.version"));
    }
}
