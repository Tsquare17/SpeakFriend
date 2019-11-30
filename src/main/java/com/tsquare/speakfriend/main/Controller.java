package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class Controller {
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Text errorMessage;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) throws IOException {
        Auth auth = new Auth();
        if(auth.check(username.getText(), password.getText())) {
            this.newScene("account-list");
            // send to accountListController
        } else {
            errorMessage.setText("The user or password entered was incorrect.");
        }
    }

    @FXML protected void createNewUser(ActionEvent event) throws IOException {
        this.newScene("register");
    }

    @FXML public void newScene(String newScene) throws IOException {
        String resource = "/" + newScene + ".fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    @FXML public void transitionScene(String newScene, int duration) {
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
}
