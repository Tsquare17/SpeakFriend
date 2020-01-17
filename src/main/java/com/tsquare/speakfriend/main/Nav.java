package com.tsquare.speakfriend.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

public class Nav extends Controller {


    @FXML
    public void quit(ActionEvent event) {
        Stage stage = Main.getStage();
        stage.close();
    }

    @FXML
    public void deleteUserView(ActionEvent event) throws IOException {
        this.newScene("delete-user");
    }
}
