package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.settings.Options;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public abstract class Controller {
    private PauseTransition transition;

    @FXML
    public void newScene(String nextScene) throws IOException {
        String resource = "/" + nextScene + ".fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        Scene newScene = new Scene(scene, currentScene.getWidth(), currentScene.getHeight());

        stage.setScene(newScene);
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

    protected Scene addSceneTimer(Scene scene) {
        this.setTimer();

        return scene;
    }

    protected String getEncryptedText(String key, TextField field) {
        if(!field.getText().isEmpty()) {
            try {
                return Crypt.encrypt(key, field.getText());
            } catch(Exception ignored) {}
        }
        return "";
    }

    public void setTimer() {
        Scene scene = Main.getScene();

        try {
            String autoLogoutTime = Options.get("auto_logout_time");

            if(!autoLogoutTime.equals("Never") && !autoLogoutTime.equals("")) {
                int autoLogoutSeconds = Integer.parseInt(autoLogoutTime) * 60 * 1000;

                System.out.println(autoLogoutSeconds);

                transition = new PauseTransition(new javafx.util.Duration(autoLogoutSeconds));
                transition.setOnFinished(evt -> {
                    try {
                        Main.logout();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // Restart transition on user interaction.
                scene.addEventFilter(InputEvent.ANY, evt -> {
                    System.out.println(transition.getDuration());
                    transition.playFromStart();
                });
                transition.play();
            } else {
                transition = new PauseTransition();
            }
        } catch (Exception e) {
            transition = new PauseTransition();
        }
    }
}
