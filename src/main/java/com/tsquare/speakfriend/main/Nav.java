package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.state.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class Nav extends Controller {
    @FXML Menu menu_cloud;
    @FXML MenuItem menu_item_logout;
    @FXML MenuItem menu_item_quit;
    @FXML MenuItem menu_item_edit_settings;
    @FXML MenuItem menu_item_delete_user;
    @FXML MenuItem menu_item_cloud_register;
    @FXML MenuItem menu_item_cloud_login;
    @FXML MenuItem menu_item_cloud_backup;
    @FXML MenuItem menu_item_about;

    @FXML
    public void initialize() {
        if (State.isCloudAuthed() == 0) {
            menu_item_cloud_backup.setVisible(false);
        } else {
            menu_item_cloud_login.setVisible(false);
            menu_item_cloud_register.setVisible(false);
        }
    }

    @FXML
    public void quitAction(ActionEvent event) {
        Stage stage = Main.getStage();
        stage.close();
    }

    @FXML
    public void deleteUserView(ActionEvent event) throws IOException {
        this.newContainerScene("delete-user");
    }

    @FXML
    public void logoutAction(ActionEvent event) throws IOException {
        Auth auth = new Auth();
        auth.checkOut();
        this.newScene("sign-in");
    }

    @FXML
    public void editSettingsView() throws IOException {
        this.newContainerScene("settings");
    }

    @FXML
    public void helpAboutView() throws IOException {
        this.newContainerScene("about");
    }

    @FXML
    public void cloudRegisterView() throws IOException {
        this.newContainerScene("cloud-register");
    }

    @FXML
    public void cloudLoginView() throws IOException {
        this.newContainerScene("cloud-login");
    }

    @FXML
    public void cloudBackupView() throws IOException {
        this.transitionContainerScene("backup");
    }
}
