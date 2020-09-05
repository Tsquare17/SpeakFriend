package com.tsquare.speakfriend.user;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.database.user.User;
import com.tsquare.speakfriend.main.Main;

import com.tsquare.speakfriend.settings.Options;
import com.tsquare.speakfriend.update.UpdateController;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class UserController extends Controller
{
    @FXML private GridPane login_grid;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField confirm_password;
    @FXML private Label login_title;
    @FXML private Text notice_text;
    @FXML private ImageView update_loader;
    private int clickCount;

    @FXML
    public void initialize() {
        update_loader.setManaged(false);
        update_loader.setVisible(false);

        RotateTransition rotate = new RotateTransition(Duration.millis(1600), update_loader);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
    }

    @FXML
    protected void loginAction() throws IOException {
        Auth auth = new Auth();
        if(auth.checkIn(username.getText().trim(), password.getText())) {
            login_grid.setVisible(false);
            login_grid.setManaged(false);

            login_title.setVisible(false);
            login_title.setManaged(false);

            notice_text.setStyle("-fx-text-fill: yellowgreen");

            update_loader.setVisible(true);
            update_loader.setManaged(true);

            boolean requiresUpdate = UpdateController.checkUpdate();
            if (requiresUpdate) {
                notice_text.setText("Updating Database");

                UpdateController updateController = new UpdateController();
                updateController.update();

                return;
            }

            String durationSetting = Options.get("auto_logout_time");
            if (!durationSetting.equals("0")) {
                int duration = Integer.parseInt(durationSetting);
                Duration delay = Duration.minutes(duration);
                Main.transition = new PauseTransition(delay);
            }

            notice_text.setText("Decrypting Accounts...");

            AccountController accountController = new AccountController();
            accountController.decryptAccounts();
        } else {
            notice_text.setText("The user or password entered was incorrect.");
        }
    }

    @FXML
    protected void loginEnterKeyAction(KeyEvent event) throws IOException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.loginAction();
        }
    }

    @FXML
    protected void registerView() throws IOException {
        this.newScene("register");
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

        this.newScene("sign-in");
    }

    @FXML
    protected void entryView() throws IOException {
        this.newScene("sign-in");
    }
}
