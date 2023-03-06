package com.tsquare.speakfriend.controller.user;

import com.tsquare.speakfriend.database.model.UsersModel;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.utils.Crypt;
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
import java.sql.SQLException;

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
    protected void registerSubmitAction() throws InvalidKeySpecException, NoSuchAlgorithmException, SQLException {
        if(username.getText().isEmpty() || password.getText().isEmpty() || confirm_password.getText().isEmpty()) {
            notice_text.setText("You must fill out all fields.");
        } else if(!password.getText().equals(confirm_password.getText())) {
            notice_text.setText("The password you entered doesn't match the confirmation.");
        } else {
            Crypt crypt = new Crypt();
            String hashedPass = crypt.generatePassword(password.getText());

            UsersModel usersModel = new UsersModel();

            boolean success = true;
            try {
                usersModel.createUser(username.getText().trim(), hashedPass);
            } catch (SQLException exception) {
                success = false;
            }

            usersModel.close();

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
    protected void registerEnterKeyAction(KeyEvent event) throws InvalidKeySpecException, NoSuchAlgorithmException, SQLException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.registerSubmitAction();
        }
    }

    @FXML
    protected void entryView() throws IOException {
        this.newScene("sign-in");
    }
}
