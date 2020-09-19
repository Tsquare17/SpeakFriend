package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.settings.Options;
import com.tsquare.speakfriend.state.State;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginController extends Controller {
    @FXML VBox login_container;
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
        login_container.setMinHeight(Main.getMinimumHeight());
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
            JSONObject userJson = (JSONObject) requestObject.get("user");
            String backupKey = (String) userJson.get("backup_key");

            auth.setApiToken(token);
            // auth.setApiPass(password.getText());

            // TODO: Instead we need to store key in state salt maybe, if it exists, and then
//            if (backupKey != null && ! backupKey.equals("")) {
//                // go to an enter master key scene.
//                auth.setApiKey(backupKey, password.getText());
//            } else {
//                // go to a create master key scene.
//                auth.createApiKey(password.getText());
//            }

            // notice_text.setText("Login successful");

            if (remember_email.isSelected()) {
                Options.put("email", email.getText());
            } else {
                Options.put("email", "");
            }

            State.setCloudAuthed(1);

            String masterKey = Options.get("master_key");
            // If there is not master_key and no backup_key from API
            if (masterKey.isEmpty() && backupKey.isEmpty()) {
                transitionContainerScene("cloud-create-master-key");
                return;
            }

            // If the API returned a backup_key but it's not yet stored locally
            if (!backupKey.isEmpty() && masterKey.isEmpty()) {
                Options.put("master_key", backupKey);
            }

            transitionContainerScene("cloud-enter-master-key");

            return;
        }

        notice_text.setText(response.getErrors().toString());
    }

    @FXML
    public void goBackAction() {
        toAccounts();
    }
}
