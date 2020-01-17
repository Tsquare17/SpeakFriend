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
        this.newScene("delete-user");
    }

    @FXML
    public void logoutAction(ActionEvent event) throws IOException {
        Auth auth = new Auth();
        auth.checkout();
        this.newScene("sign-in");
    }
}
