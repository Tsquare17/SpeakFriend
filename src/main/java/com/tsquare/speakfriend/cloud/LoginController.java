package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.settings.Options;
import com.tsquare.speakfriend.state.State;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginController extends Controller {
    @FXML TextField email;
    @FXML PasswordField password;
    @FXML Text notice_text;
    @FXML CheckBox remember_email;

    @FXML
    public void initialize() {
        String emailAddress = Options.get("email");
        if (!emailAddress.equals("")) {
            email.setText(emailAddress);
            remember_email.setSelected(true);

            Platform.runLater(() -> password.requestFocus());
        }
    }

    @FXML
    public void submitAction(KeyEvent event) throws ParseException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.loginAction();
        }
    }

    @FXML
    public void loginAction() throws ParseException {
        if (email.getText().equals("")) {
            notice_text.setText("Please enter your email address");
            return;
        }
        if (password.getText().equals("")) {
            notice_text.setText("Please enter your password");
            return;
        }

        Auth auth = new Auth();
        Api api = new Api();

        ApiResponse response = api.login(email.getText(), password.getText());

        if (response.getResponseMessage().equals("OK")) {
            String body = response.getResponseBody();
            JSONParser parser = new JSONParser();

            JSONObject requestObject = (JSONObject) parser.parse(body);

            try {
                if (requestObject.get("message").equals("Invalid Credentials")) {
                    notice_text.setText("Invalid credentials");
                    return;
                }
            } catch (Exception ignored) {}

            String token = (String) requestObject.get("access_token");

            auth.setApiToken(token);
            auth.setApiKey(password.getText());

            notice_text.setText("Login successful");

            if (remember_email.isSelected()) {
                Options.put("email", email.getText());
            }

            State.setCloudAuthed(1);

            transitionToAccounts();

            return;
        }

        notice_text.setText(response.getErrors().toString());
    }

    @FXML
    public void goBackAction() {
        toAccounts();
    }
}
