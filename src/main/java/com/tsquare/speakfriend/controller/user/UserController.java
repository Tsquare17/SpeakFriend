package com.tsquare.speakfriend.controller.user;

import com.tsquare.speakfriend.database.model.UsersModel;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.utils.Auth;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class UserController extends Controller {
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Text notice_text;
    private int clickCount;

    @FXML
    public void deleteUserAction() throws IOException, SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        if(!this.password.getText().equals(this.confirm_password.getText())) {
            this.notice_text.setText("Password mismatch. Please confirm.");
            this.clickCount = 0;
            return;
        }

        UserSession userSession = UserSession.getInstance();
        String userName = userSession.getName();

        Auth auth = new Auth();

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

        UsersModel usersModel = new UsersModel();
        usersModel.deleteUser(userSession.getId());

        newScene("sign-in");
    }
}
