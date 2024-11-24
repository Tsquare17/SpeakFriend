package com.tsquare.speakfriend.controller.settings;

import com.tsquare.speakfriend.config.AppConfig;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.database.connection.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AppSettingsController extends Controller {
    @FXML
    TextField dbPathTextField;

    @FXML
    public void initialize() throws SQLException {
        AppConfig appConfig = AppConfig.getInstance();

        dbPathTextField.setText(appConfig.getDbFile());
    }

    @FXML
    public void openDbFileChooser() {
        AppConfig appConfig = AppConfig.getInstance();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select DB File");

        fileChooser.setInitialDirectory(new File(new File(appConfig.getDbFile()).getParentFile().getAbsolutePath()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.db"));

        File file = fileChooser.showOpenDialog(Main.getStage());

        if (file != null) {
            dbPathTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void saveSettingsAction() throws IOException, SQLException {
        AppConfig appConfig = AppConfig.getInstance();

        appConfig.setDbFile(dbPathTextField.getText());

        SqliteConnection.resetConnection();
    }

    @FXML
    public void backToLoginView() throws IOException {
        newScene("sign-in");
    }
}
