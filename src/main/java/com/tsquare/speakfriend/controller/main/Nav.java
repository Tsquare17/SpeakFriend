package com.tsquare.speakfriend.controller.main;

import com.tsquare.speakfriend.utils.Auth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class Nav extends Controller {
    @FXML MenuItem menu_item_logout;
    @FXML MenuItem menu_item_quit;
    @FXML MenuItem menu_item_edit_settings;
    @FXML MenuItem menu_item_accounts;
    @FXML MenuItem menu_item_delete_user;
    @FXML MenuItem menu_item_export;
    @FXML MenuItem menu_item_import;
    @FXML MenuItem menu_item_about;

    @FXML
    public void initialize() {
        // Hide or show items here.
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
    public void exportView() throws IOException {
        newContainerScene("account-export");
    }

    @FXML
    public void importView() throws IOException {
        newContainerScene("account-import");
    }

    @FXML
    public void viewAccountsView() {
        toAccounts();
    }
}
