package com.tsquare.speakfriend.user;

import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.user.User;
import com.tsquare.speakfriend.main.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class RegisterController extends Controller {
    @FXML private VBox register_container;
    @FXML private GridPane register_gridpane;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Text notice_text;

    @FXML
    public void initialize() {
        register_gridpane.setPrefHeight(300);
        register_gridpane.setMinHeight(200);
    }

    @FXML
    protected void registerSubmitAction() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(username.getText().isEmpty() || password.getText().isEmpty() || confirm_password.getText().isEmpty()) {
            notice_text.setText("You must fill out all fields.");
        } else if(!password.getText().equals(confirm_password.getText())) {
            notice_text.setText("The password you entered doesn't match the confirmation.");
        } else {
            User user = new User();
            String hashedPass = Crypt.generatePassword(password.getText());
            assert hashedPass != null;
            boolean success = user.create(username.getText().trim(), hashedPass);

            if(!success) {
                notice_text.setText("A user with that name already exists.");
                return;
            }

            notice_text.setFill(Color.rgb(255,255,255));
            notice_text.setText("Successfully created account.");

            this.transitionScene("sign-in", 2);
        }
    }

    @FXML
    protected void registerEnterKeyAction(KeyEvent event) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.registerSubmitAction();
        }
    }

    @FXML
    protected void entryView() throws IOException {
        this.newScene("sign-in");
    }
}
