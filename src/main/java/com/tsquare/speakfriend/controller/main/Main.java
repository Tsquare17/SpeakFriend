package com.tsquare.speakfriend.controller.main;

import com.tsquare.speakfriend.database.model.SystemSettingsModel;
import com.tsquare.speakfriend.database.schema.Schema;
import com.tsquare.speakfriend.utils.Auth;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

// import org.scenicview.ScenicView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {
    private static Stage stage;

    public static String version = "0.0.0";

    public static PauseTransition transition;

    public static URL previousUrl;

    public static int minimumHeight = 470;

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return stage.getScene();
    }

    public static int getMinimumHeight() {
        return minimumHeight;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.setup();

        stage = primaryStage;
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
        Parent root = FXMLLoader.load(getClass().getResource("/sign-in.fxml"));
        stage.setTitle("Speak Friend");
        Scene scene = new Scene(root, 600, getMinimumHeight());
        stage.setScene(scene);
        stage.show();

        // ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected static void setup() throws SQLException {
        File dir = new File(System.getProperty("user.home") + "/.speakfriend");
        if(!dir.exists()) {
            dir.mkdir();
        }

        Schema schema = new Schema();
        schema.up();

        version = getSemanticVersion();
    }

    public static void setTimer(Scene scene) {
        if (transition != null) {
            transition.setOnFinished(evt -> {
                try {
                    Main.logout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            scene.addEventFilter(InputEvent.ANY, evt -> {
                transition.playFromStart();
            });
        }
    }

    public static void setNewTimer(int duration) {
        if (transition != null) {
            transition.stop();
        }

        Duration delay = Duration.minutes(duration);
        Main.transition = new PauseTransition(delay);

        transition.setOnFinished(evt -> {
            try {
                Main.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Scene scene = Main.getScene();

        scene.addEventFilter(InputEvent.ANY, evt -> {
            transition.playFromStart();
        });
    }

    public static void logout() throws IOException {
        transition.stop();
        Auth auth = new Auth();
        auth.checkOut();

        Parent scene = FXMLLoader.load(Main.class.getResource("/sign-in.fxml"));
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        Scene newScene = new Scene(scene, currentScene.getWidth(), currentScene.getHeight());
        stage.setScene(newScene);
    }

    public static String getSemanticVersion() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
        ResultSet resultSet = systemSettingsModel.getSystemSetting("version");
        String systemVersionString = null;
        if (resultSet.next()) {
            systemVersionString = resultSet.getString("value");
        }

        resultSet.close();
        systemSettingsModel.close();

        if (systemVersionString == null) {
            return "0.1.0";
        }

        char[] chars = systemVersionString.toCharArray();

        return chars[0] + "." + chars[1] + "." + chars[2];
    }

    public static String getVersion() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
        ResultSet resultSet = systemSettingsModel.getSystemSetting("version");
        String systemVersionString = resultSet.getString("value");

        resultSet.close();
        systemSettingsModel.close();

        if (systemVersionString.equals("")) {
            return "0";
        }

        return systemVersionString;
    }
}
