package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.database.schema.Schema;
import com.tsquare.speakfriend.database.settings.Setting;
import com.tsquare.speakfriend.database.settings.SettingsEntity;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
// import org.scenicview.ScenicView;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static Stage stage;
    private static PauseTransition transition;

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return stage.getScene();
    }

    public static PauseTransition getTransition() {
        return transition;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.setup();

        stage = primaryStage;
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
        Parent root = FXMLLoader.load(getClass().getResource("/sign-in.fxml"));
        stage.setTitle("Speak Friend");
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);

        Setting setting = new Setting();

        try {
            String autoLogoutTime = setting.getOption("auto_logout_time").getValue();

            if(!autoLogoutTime.equals("Never")) {
                transition = new PauseTransition(new javafx.util.Duration(Integer.parseInt(autoLogoutTime)));
                transition.setOnFinished(evt -> {
                    try {
                        this.logout();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // Restart transition on user interaction.
                scene.addEventFilter(InputEvent.ANY, evt -> {
                    transition.playFromStart();
                });
                transition.play();
            } else {
                transition = new PauseTransition();
            }
        } catch (Exception e) {
            transition = new PauseTransition();
        }

        stage.show();

        // ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected static void setup() {
        File dir = new File(System.getProperty("user.home") + "/.speakfriend");
        if(!dir.exists()) {
            dir.mkdir();
        }

        Schema schema = new Schema();
        schema.up();

        Setting setting = new Setting();
        SettingsEntity dbVersion = setting.getOption("db_version");
        if (dbVersion != null && !dbVersion.getValue().equals("0.4.0")) {
            setting.create("auto_logout_timer", "1 hour");
        }
    }

    protected void logout() throws IOException {
        Auth auth = new Auth();
        auth.checkOut();
        this.newScene("sign-in");
    }

    @FXML
    public void newScene(String newScene) throws IOException {
        String resource = "/" + newScene + ".fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }
}
