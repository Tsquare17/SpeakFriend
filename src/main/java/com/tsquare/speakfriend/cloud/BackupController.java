package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class BackupController {
    @FXML AnchorPane account_anchor;
    @FXML ScrollPane account_list_scrollpane;

    @FXML
    public void initialize() {
        Auth auth = new Auth();
        Api api = new Api();

        if (auth.getApiToken().equals("")) {
            return;
        }

        ApiResponse response = api.getAccounts();
        String test = "";
    }
}
