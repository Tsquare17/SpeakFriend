package com.tsquare.speakfriend.main;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public abstract class Controller {
    @FXML
    public void newScene(String newScene) throws IOException {
        String resource = "/" + newScene + ".fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    @FXML
    public void transitionScene(String newScene, int duration) {
        PauseTransition pause = new PauseTransition(
                Duration.seconds(duration)
        );
        pause.setOnFinished(e -> {
            try {
                this.newScene(newScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    @FXML
    public void buttonHovered() {
        System.out.println("hover");
        Scene scene = Main.getScene();
        scene.setCursor(Cursor.HAND);
    }

    @FXML
    public void buttonNotHovered() {
        System.out.println("unhovered");
        Scene scene = Main.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }
}
