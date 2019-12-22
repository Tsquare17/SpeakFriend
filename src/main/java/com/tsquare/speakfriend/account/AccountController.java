package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AccountController extends Controller {
    @FXML private TextField account_name;
    @FXML private PasswordField password;
    @FXML private TextField url;
    @FXML private TextArea notes;
    @FXML private Label errorMessage;

    @FXML
    public void createAccountAction(ActionEvent event) {
        Auth auth = new Auth();
        int id = auth.getId();
        String key = auth.getKey();

        String accountName = null;
        String accountPass = null;
        String accountUrl = null;
        String accountNotes = null;

        if(!account_name.getText().isEmpty()) {
            try {
                accountName = Crypt.encrypt(key, account_name.getText());
            } catch (Exception ignored) {}
        }

        if(!password.getText().isEmpty()) {
            try {
                accountPass = Crypt.encrypt(key, password.getText());
            } catch (Exception ignored) {}
        }

        if(!url.getText().isEmpty()) {
            try {
                accountUrl = Crypt.encrypt(key, url.getText());
            } catch (Exception ignored) {}
        }

        if(!notes.getText().isEmpty()) {
            try {
                accountNotes = Crypt.encrypt(key, notes.getText());
            } catch(Exception ignored) {}
        }

        Account account = new Account();
        account.create(id, accountName, accountPass, accountUrl, accountNotes);
        errorMessage.setText("Account Created");
    }

    @FXML
    public void createAccountView(ActionEvent event) throws IOException {
        this.newScene("create-account");
    }

    @FXML
    public void listAccountsView(ActionEvent event) throws IOException {

        Auth auth = new Auth();
        int id = auth.getId();
        String key = auth.getKey();

        String resource = "/account-list.fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        ObservableList<GridPane> gridList = FXCollections.observableArrayList();
        ListView<GridPane> list = new ListView<>();
        StackPane accountListPane = (StackPane) scene.lookup("#accountList");
        List<AccountEntity> accounts = AccountList.get(id);

        for (AccountEntity account: accounts) {
            int accountId = account.getId().getValue();
            try {
                String accountName = Crypt.decrypt(key, account.getName());
                String accountPass = Crypt.decrypt(key, account.getPass());
                String accountUrl = Crypt.decrypt(key, account.getUrl());
                String accountNotes = Crypt.decrypt(key, account.getNotes());
                GridPane gridPane = new GridPane();
                gridPane.setId("account_" + accountId);
                gridPane.getStyleClass().add("account-gridpane");
                gridPane.setOnMouseClicked(mouseEvent -> {
                    try {
                        this.showAccountDetails(accountId, accountName, accountPass, accountUrl, accountNotes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                gridPane.add(new Label(accountName), 0, 0);
                gridList.add(gridPane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        list.getItems().addAll(gridList);
        accountListPane.getChildren().add(list);

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    protected void showAccountDetails(int id, String name, String pass, String url, String notes) throws IOException {

        String resource = "/account-details.fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        TextField accountName      = (TextField) scene.lookup("#account_name");
        PasswordField accountPass  = (PasswordField) scene.lookup("#account_password");
        TextField accountUrl       = (TextField) scene.lookup("#account_url");
        TextArea accountNotes     = (TextArea) scene.lookup("#account_notes");

        accountName.setText(name);
        accountPass.setText(pass);
        accountUrl.setText(url);
        accountNotes.setText(notes);

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    @FXML
    public void updateDetails(ActionEvent event) throws IOException {
        System.out.println("update details");
    }
}
