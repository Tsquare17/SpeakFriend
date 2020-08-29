package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

public class Nav extends Controller {
    @FXML
    public void quitAction(ActionEvent event) {
        Stage stage = Main.getStage();
        stage.close();
    }

    @FXML
    public void deleteUserView(ActionEvent event) throws IOException {
        this.newContainerScene("delete-user");
    }

    @FXML
    public void logoutAction(ActionEvent event) throws IOException {
        Auth auth = new Auth();
        auth.checkOut();
        this.newScene("sign-in");
    }

    @FXML
    public void editSettingsView() throws IOException {
        this.newContainerScene("settings");
    }

    @FXML
    public void helpAboutView() throws IOException {
        this.newContainerScene("about");
    }

    @FXML
    public void helpRegisterView() throws IOException {
        this.newContainerScene("cloud-register");
    }
}
