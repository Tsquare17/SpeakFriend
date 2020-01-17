package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.crypt.Crypt;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

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

    protected String getEncryptedText(String key, TextField field) {
        if(!field.getText().isEmpty()) {
            try {
                return Crypt.encrypt(key, field.getText());
            } catch(Exception ignored) {}
        }
        return "";
    }

    protected String getEncryptedText(String key, TextArea field) {
        if(!field.getText().isEmpty()) {
            try {
                return Crypt.encrypt(key, field.getText());
            } catch(Exception ignored) {}
        }
        return "";
    }
}
