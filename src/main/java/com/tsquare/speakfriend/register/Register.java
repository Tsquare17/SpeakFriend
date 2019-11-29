package com.tsquare.speakfriend.register;

import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class Register
{
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Text errorMessage;

    @FXML
    protected void submitRegistration(ActionEvent event) throws IOException {
        if(username.getText().isEmpty() || password.getText().isEmpty() || confirm_password.getText().isEmpty()) {
            errorMessage.setText("You must fill out all fields.");
        } else if(!password.getText().equals(confirm_password.getText())) {
            errorMessage.setText("The password you entered doesn't match the confirmation.");
        } else {
            User user = new User();
            user.create(username.getText(), password.getText());
            errorMessage.setText("Successfully created account.");
        }
    }

    @FXML
    protected void backToEntry(ActionEvent event) throws IOException {
        Controller controller = new Controller();
        controller.newScene("entry");
    }
}
