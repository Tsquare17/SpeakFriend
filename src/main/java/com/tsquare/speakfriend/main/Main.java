package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.database.schema.Schema;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

// import org.scenicview.ScenicView;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private static Stage stage;

    public static PauseTransition transition = null;

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return stage.getScene();
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
    }

    public static void startTimer(Scene scene) {
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

        transition.play();
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
}
