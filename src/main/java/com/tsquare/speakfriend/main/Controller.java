package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Controller {
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Text errorMessage;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) throws IOException {
        Auth auth = new Auth(username.getText(), password.getText());
        if(auth.check()) {
            this.newScene("accounts-list");
            // send to accountListController
        } else {
            errorMessage.setText("The user or password entered was incorrect.");
        }
    }

    @FXML protected void createNewAccount(ActionEvent event) throws IOException {
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
}
