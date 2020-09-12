package com.tsquare.speakfriend.user;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.settings.Options;
import com.tsquare.speakfriend.update.UpdateController;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController extends Controller {
    @FXML private VBox login_container;
    @FXML private GridPane login_grid;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Label login_title;
    @FXML private Text notice_text;
    @FXML private ImageView update_loader;

    @FXML
    public void initialize() {
        if (update_loader != null) {
            update_loader.setManaged(false);
            update_loader.setVisible(false);

            RotateTransition rotate = new RotateTransition(Duration.millis(1600), update_loader);
            rotate.setByAngle(360);
            rotate.setCycleCount(Animation.INDEFINITE);
            rotate.setInterpolator(Interpolator.LINEAR);
            rotate.play();
        }
        login_container.setMinHeight(Main.getMinimumHeight());
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

            Task<Void> task = new Task<>() {
                @Override
                public Void call() {
                    AccountList.getPreviews();
                    return null;
                }
            };

            task.setOnSucceeded(taskFinishEvent -> {
                toAccounts();
            });
            new Thread(task).start();
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
    public void registerView() throws IOException {
        newScene("register");
    }
}
