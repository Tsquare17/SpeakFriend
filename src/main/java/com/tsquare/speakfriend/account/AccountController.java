package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.account.preview.AccountPreview;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.nodes.accountListCell;
import com.tsquare.speakfriend.utils.AccountPreviewComparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private Hyperlink edit_account_link;
    @FXML private Button create_account_button;
    private int clickCount;

    @FXML
    public void createAccountAction(ActionEvent event) {
        Auth auth = new Auth();
        int id = auth.getId();
        String key = auth.getKey();

        String accountName = this.getEncryptedText(key, account_name);
        String accountPass = this.getEncryptedText(key, account_password);
        String accountUrl = this.getEncryptedText(key, account_url);
        String accountNotes = this.getEncryptedText(key, account_notes);

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
        // Get decryption key.
        Auth auth  = new Auth();
        int id     = auth.getId();
        String key = auth.getKey();

        String resource = "/account-list.fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene       = FXMLLoader.load(file);
        Stage stage        = Main.getStage();
        Scene currentScene = stage.getScene();

        // Get the account list container.
        VBox accountListContainer = (VBox) scene.lookup("#accountList");

        // Get all accounts for the current user.
        List<AccountEntity> accounts = AccountList.get(id);

        // Collect list of decrypted account previews.
        List<AccountPreview> decryptedList = new ArrayList<>();
        for (AccountEntity account: accounts) {
            int accountId = account.getId().getValue();
            String accountName = "";
            try {
                accountName = Crypt.decrypt(key, account.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            decryptedList.add(new AccountPreview(accountId, accountName));
        }

        // Sort the list of accounts by name.
        decryptedList.sort(new AccountPreviewComparator<>());

        ListView<AccountPreview> listView        = new ListView<>();
        ObservableList<AccountPreview> listItems = FXCollections.observableArrayList();

        // Add the accounts to the list, set them in the list view, and add it to the container.
        listItems.addAll(decryptedList);
        listView.setItems(listItems);
        listView.setCellFactory(
                accountListView -> new accountListCell()
        );
        accountListContainer.getChildren().add(listView);

        VBox box = FXMLLoader.load(getClass().getResource("/container.fxml"));
        box.getChildren().add(scene);

        stage.setScene(new Scene(box, currentScene.getWidth(), currentScene.getHeight()));
    }

    public void showAccountDetails(int id) throws IOException {

        Auth auth = new Auth();
        String key = auth.getKey();

        Account account = new Account();
        AccountEntity accountEntity = account.getById(id);

        String accountId = String.valueOf(id);
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
