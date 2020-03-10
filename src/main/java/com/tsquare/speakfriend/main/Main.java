package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.database.schema.Schema;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

    public static void logout() throws IOException {
        Auth auth = new Auth();
        auth.checkOut();
        String resource = "/sign-in.fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }
}
