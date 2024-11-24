package com.tsquare.speakfriend.controller.user;

import com.tsquare.speakfriend.config.AppConfig;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.session.AccountListSession;
import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.controller.update.UpdateController;
import com.tsquare.speakfriend.utils.Auth;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController extends Controller {
    @FXML private VBox login_container;
    @FXML private GridPane login_grid;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Label login_title;
    @FXML private Text notice_text;
    @FXML private ImageView update_loader;
    @FXML private CheckBox remember_checkbox;

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
    protected void loginAction() throws IOException, SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        Auth auth = new Auth();
        if(auth.checkIn(username.getText().trim(), password.getText())) {
            login_grid.setVisible(false);
            login_grid.setManaged(false);

            login_title.setVisible(false);
            login_title.setManaged(false);

            notice_text.setStyle("-fx-text-fill: yellowgreen");

            update_loader.setVisible(true);
            update_loader.setManaged(true);

            ApplicationSession applicationSession = ApplicationSession.getInstance();
            boolean requiresUpdate = UpdateController.checkUpdate();
            if (requiresUpdate) {
                applicationSession.setLoadingMessage("Updating Database");
                newScene("loading");
                UpdateController updateController = new UpdateController();
                updateController.update();

                return;
            }

            UserSession userSession = UserSession.getInstance();
            UserSettingsModel userSettingsModel = new UserSettingsModel();

            ResultSet resultSet = userSettingsModel.getUserSetting(
                userSession.getId(),
                "auto_logout_time"
            );

            String durationSetting = resultSet.getString("value");
            if (!durationSetting.equals("0")) {
                int duration = Integer.parseInt(durationSetting);
                Duration delay = Duration.minutes(duration);
                Main.transition = new PauseTransition(delay);
            }

            resultSet.close();
            userSettingsModel.close();

            AppConfig appConfig = AppConfig.getInstance();

            if (remember_checkbox.isSelected()) {
                appConfig.setProperty("remember_user", String.valueOf(userSession.getId()));
            } else {
                appConfig.setProperty("remember_user", "0");
            }

            // Check if there are any accounts before displaying the loading message.
            // If not, just go right to the empty list.
            AccountsModel accountsModel = new AccountsModel();
            resultSet = accountsModel.getUserAccounts(userSession.getId());
            if (!resultSet.next()) {
                resultSet.close();
                accountsModel.close();

                toAccounts();

                return;
            }

            resultSet.close();
            accountsModel.close();

            applicationSession.setLoadingMessage("Decrypting Accounts...");
            newScene("loading");

            Task<Void> task = new Task<>() {
                @Override
                public Void call() {
                    AccountListSession accountListSession = AccountListSession.getInstance();
                    accountListSession.getPreviews();
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
    protected void loginEnterKeyAction(KeyEvent event) throws IOException, SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        if(event.getCode().equals(KeyCode.ENTER)) {
            this.loginAction();
        }
    }

    @FXML
    public void registerView() throws IOException {
        newScene("register");
    }

    @FXML
    public void settingsAction() throws IOException {
        newScene("app_settings");
    }
}
