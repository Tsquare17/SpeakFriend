package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public abstract class Controller {

    public void goBack() {
        Scene newScene = Main.previousScene;
        Main.setTimer(newScene);
        Main.getStage().setScene(newScene);
    }

    public void goBack(Integer delay) {
        PauseTransition pause = new PauseTransition(
                Duration.seconds(delay)
        );
        pause.setOnFinished(e -> {
            goBack();
        });
        pause.play();
    }

    @FXML
    public void newScene(String nextScene) throws IOException {
        Main.previousScene = Main.getScene();

        String resource = "/" + nextScene + ".fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        Scene newScene = new Scene(scene, currentScene.getWidth(), currentScene.getHeight());
        Main.setTimer(newScene);
        stage.setScene(newScene);
    }

    public void newContainerScene(String resource) throws IOException {
        Main.previousScene = Main.getScene();

        URL file = Nav.class.getResource("/" + resource + ".fxml");

        Parent scene       = FXMLLoader.load(file);
        Stage stage        = Main.getStage();
        Scene currentScene = stage.getScene();

        VBox box = FXMLLoader.load(getClass().getResource("/container.fxml"));
        box.getChildren().add(scene);

        Scene newScene = new Scene(box, currentScene.getWidth(), currentScene.getHeight());
        Main.setTimer(newScene);
        stage.setScene(newScene);
    }

    @FXML
    public void transitionScene(String newScene, int duration) {
        Main.previousScene = Main.getScene();

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
    public void transitionContainerScene(String newScene, int duration) {
        Main.previousScene = Main.getScene();

        PauseTransition pause = new PauseTransition(
                Duration.seconds(duration)
        );
        pause.setOnFinished(e -> {
            try {
                this.newContainerScene(newScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    @FXML
    public void transitionContainerScene(String newScene) {
        transitionContainerScene(newScene, 0);
    }

    public void transitionToAccounts() {
        Main.previousScene = Main.getScene();

        PauseTransition pause = new PauseTransition(
                Duration.seconds(2)
        );
        pause.setOnFinished(e -> {
            try {
                AccountController accountController = new AccountController();
                accountController.listAccountsView();
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

    protected String getDecryptedText(String encrypted) {
        Auth auth = new Auth();
        String key = auth.getKey();

        String decrypted = "";
        try {
            decrypted = Crypt.decrypt(key, encrypted);
        } catch (Exception ignore) {};

        return decrypted;
    }
}
