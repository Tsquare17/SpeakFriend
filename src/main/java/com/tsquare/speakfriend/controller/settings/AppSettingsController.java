package com.tsquare.speakfriend.controller.settings;

import com.tsquare.speakfriend.config.AppConfig;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.controller.main.Nav;
import com.tsquare.speakfriend.controller.update.UpdateController;
import com.tsquare.speakfriend.database.connection.SqliteConnection;
import com.tsquare.speakfriend.database.exception.DatabaseFileNotFoundException;
import com.tsquare.speakfriend.database.schema.Schema;
import com.tsquare.speakfriend.session.ApplicationSession;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AppSettingsController extends Controller {
    @FXML
    Text notice_text;
    @FXML
    TextField db_path_text_field;
    @FXML
    Button back_button;

    @FXML
    public void initialize() throws SQLException {
        AppConfig appConfig = AppConfig.getInstance();

        db_path_text_field.setText(appConfig.getDbFile());
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
            db_path_text_field.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void saveSettingsAction() throws IOException, SQLException, DatabaseFileNotFoundException {
        AppConfig appConfig = AppConfig.getInstance();

        appConfig.setDbFile(db_path_text_field.getText());

        SqliteConnection.resetConnection();

        // may need to update database. run upgrade process
        Schema schema = new Schema();
        schema.up();

        if (!back_button.isVisible()) {
            newScene("sign-in");

            Scene scene = Main.getScene();
            Main.setLoginScene(scene);
        }
    }

    @FXML
    public void backToLoginView() throws IOException {
        newScene("sign-in");
    }

    public void setNoticeText(String text) {
        notice_text.setText(text);
    }

    public void setBackButtonVisible(boolean visible) {
        back_button.setVisible(visible);
    }
}
