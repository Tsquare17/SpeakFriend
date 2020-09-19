package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.settings.Options;
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
    @FXML MenuItem menu_item_cloud_import;
    @FXML MenuItem menu_item_about;

    @FXML
    public void initialize() {
        if (State.isCloudAuthed() == 0) {
            menu_item_cloud_backup.setVisible(false);
            menu_item_cloud_import.setVisible(false);
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
        newContainerScene("delete-user");
    }

    @FXML
    public void logoutAction(ActionEvent event) throws IOException {
        Auth auth = new Auth();
        auth.checkOut();
        newScene("sign-in");
    }

    @FXML
    public void editSettingsView() throws IOException {
        newContainerScene("settings");
    }

    @FXML
    public void helpAboutView() throws IOException {
        newContainerScene("about");
    }

    @FXML
    public void cloudRegisterView() throws IOException {
        newContainerScene("cloud-register");
    }

    @FXML
    public void cloudLoginView() throws IOException {
        newContainerScene("cloud-login");
    }

    @FXML
    public void cloudBackupView() throws IOException {
        if (Options.get("master_key").isEmpty()) {
            newContainerScene("cloud-create-master-key");
        } else if (State.isCloudKeySet() == 0) {
            newContainerScene("cloud-enter-master-key");
        } else {
            newContainerScene("backup");
        }
    }

    @FXML
    public void cloudImportView() throws IOException {
        if (Options.get("master_key").isEmpty()) {
            newContainerScene("cloud-create-master-key");
        } else if (State.isCloudKeySet() == 0) {
            newContainerScene("cloud-enter-master-key");
        } else {
            State.setLoadingMessage("Loading backed up accounts...");
            newScene("loading");
            loadingImports();
        }
    }

    @FXML
    public void viewAccountsView() {
        toAccounts();
    }
}
