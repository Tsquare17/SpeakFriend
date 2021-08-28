package com.tsquare.speakfriend.user;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.database.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.io.IOException;

public class UserController extends Controller {
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Text notice_text;
    private int clickCount;

    @FXML
    public void deleteUserAction() throws IOException {
        if(!this.password.getText().equals(this.confirm_password.getText())) {
            this.notice_text.setText("Password mismatch. Please confirm.");
            this.clickCount = 0;
            return;
        }

        Auth auth = new Auth();
        String userName = auth.getName();

        if(!auth.checkIn(userName, password.getText())) {
            this.notice_text.setText("The password entered was incorrect.");
            this.clickCount = 0;
            return;
        }

        if(this.clickCount < 1) {
            this.notice_text.setText("Click delete once more to permanently remove this account.");
            this.clickCount++;
            return;
        }

        User user = new User();
        user.delete(auth.getId());

        newScene("sign-in");
    }
}
