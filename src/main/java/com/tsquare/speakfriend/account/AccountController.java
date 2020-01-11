package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.utils.AccountListComparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AccountController extends Controller {
    @FXML private Label account_id;
    @FXML private TextField account_name;
    @FXML private TextField account_password;
    @FXML private TextField account_url;
    @FXML private TextArea account_notes;
    @FXML private Label response_message;
    @FXML private Button update_account_button;
    @FXML private Button delete_account_button;
    @FXML private Button back_button;
    @FXML private Hyperlink edit_account_link;
    @FXML private Button create_account_button;

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

        if(!account_password.getText().isEmpty()) {
            try {
                accountPass = Crypt.encrypt(key, account_password.getText());
            } catch (Exception ignored) {}
        }

        if(!account_url.getText().isEmpty()) {
            try {
                accountUrl = Crypt.encrypt(key, account_url.getText());
            } catch (Exception ignored) {}
        }

        if(!account_notes.getText().isEmpty()) {
            try {
                accountNotes = Crypt.encrypt(key, account_notes.getText());
            } catch(Exception ignored) {}
        }

        Account account = new Account();
        account.create(id, accountName, accountPass, accountUrl, accountNotes);
        response_message.setText("Account Created");
        create_account_button.setVisible(false);
    }

    @FXML
    public void createAccountView(ActionEvent event) throws IOException {
        this.newScene("create-account");
    }

    @FXML
    public void listAccountsView() throws IOException {

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

        List<List<String>> decryptedList = new ArrayList<>();

        for (AccountEntity account: accounts) {
            List<String> decryptedAccount = new ArrayList<>();
            String accountId = account.getId().getValue().toString();
            String accountName = "";
            try {
                accountName = Crypt.decrypt(key, account.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            decryptedAccount.add(accountId);
            decryptedAccount.add(accountName);
            decryptedList.add(decryptedAccount);
        }

        decryptedList.sort(new AccountListComparator<>());

        for (List<String> account: decryptedList) {
            try {
                GridPane gridPane = new GridPane();
                gridPane.setId("account_" + account.get(0));
                gridPane.getStyleClass().add("account-gridpane");
                gridPane.setOnMouseClicked(mouseEvent -> {
                    try {
                        this.showAccountDetails(account.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                gridPane.add(new Label(account.get(1)), 0, 0);
                gridList.add(gridPane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        list.getItems().addAll(gridList);
        accountListPane.getChildren().add(list);

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    protected void showAccountDetails(String accountId) throws IOException {

        Auth auth = new Auth();
        String key = auth.getKey();

        int id = Integer.parseInt(accountId);
        Account account = new Account();
        AccountEntity accountEntity = account.getById(id);

        String accountName = "";
        String accountPass = "";
        String accountUrl = "";
        String accountNotes = "";
        try {
            assert accountEntity != null;
            accountName = Crypt.decrypt(key, accountEntity.getName());
            accountPass = Crypt.decrypt(key, accountEntity.getPass());
            accountUrl = Crypt.decrypt(key, accountEntity.getUrl());
            accountNotes = Crypt.decrypt(key, accountEntity.getNotes());
        } catch (Exception ignore) {};

        String resource = "/account-details.fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = Main.getScene();

        Label accountIdField            = (Label) scene.lookup("#account_id");
        TextField accountNameField      = (TextField) scene.lookup("#account_name");
        TextField accountPassField      = (TextField) scene.lookup("#account_password");
        TextField accountUrlField       = (TextField) scene.lookup("#account_url");
        TextArea accountNotesField      = (TextArea) scene.lookup("#account_notes");

        accountIdField.setText(accountId);
        accountNameField.setText(accountName);
        accountPassField.setText(accountPass);
        accountUrlField.setText(accountUrl);
        accountNotesField.setText(accountNotes);

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    @FXML
    public void updateAccountDetails(ActionEvent event) throws IOException {

        int accountId = Integer.parseInt(account_id.getText());
        String accountName = account_name.getText();
        String accountPass = account_password.getText();
        String accountUrl = account_url.getText();
        String accountNotes = account_notes.getText();

        Auth auth = new Auth();
        String key = auth.getKey();

        Account account = new Account();

        String encName = null;
        String encPass = null;
        String encUrl = null;
        String encNotes = null;

        try {
            encName = Crypt.encrypt(key, accountName);
            encPass = Crypt.encrypt(key, accountPass);
            encUrl = Crypt.encrypt(key, accountUrl);
            encNotes = Crypt.encrypt(key, accountNotes);
        } catch (Exception ignored) {};

        account.update(accountId, encName, encPass, encUrl, encNotes);

        response_message.setText("Account Updated");
    }

    @FXML
    public void editAccountAction() {
        update_account_button.setVisible(true);
        delete_account_button.setVisible(true);
        edit_account_link.setVisible(false);
        account_name.setEditable(true);
        account_password.setEditable(true);
        account_url.setEditable(true);
        account_notes.setEditable(true);
    }

    @FXML
    public void deleteAccountAction() throws IOException {
        int accountId = Integer.parseInt(account_id.getText());
        Account account = new Account();
        account.delete(accountId);
        this.listAccountsView();
    }
}
