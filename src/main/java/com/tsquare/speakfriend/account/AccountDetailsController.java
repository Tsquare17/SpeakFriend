package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.crypt.Password;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.state.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;

public class AccountDetailsController extends AccountController {
    @FXML private VBox account_container;
    @FXML private TextField account_name;
    @FXML private TextField account_user;
    @FXML private TextField account_password;
    @FXML private PasswordField account_password_masked;
    @FXML private TextField account_url;
    @FXML private TextField account_notes;
    @FXML private Label notice_text;
    @FXML private Button update_account_button;
    @FXML private Button delete_account_button;
    @FXML private Hyperlink edit_account_link;
    @FXML private Button edit_notes_button;
    @FXML private Button view_notes_button;
    @FXML private ImageView generate_password_icon;
    @FXML private ImageView password_clipboard;
    @FXML private ImageView show_password;
    @FXML private ImageView username_clipboard;
    @FXML private ImageView go_to_url;
    private int clickCount;
    private int deleteFromCloudId = 0;

    @FXML
    public void initialize() {
        Account account = new Account();
        AccountEntity accountEntity = account.getById(State.getSelectedAccountId());

        assert accountEntity != null;
        String accountName = this.getDecryptedText(accountEntity.getName());
        String accountUser = this.getDecryptedText(accountEntity.getUser());
        String accountPass = this.getDecryptedText(accountEntity.getPass());
        String accountUrl = this.getDecryptedText(accountEntity.getUrl());
        String accountNotes = this.getDecryptedText(accountEntity.getNotes());

        account_name.setText(accountName);
        account_user.setText(accountUser);
        account_password.setText(accountPass);
        account_password_masked.setText(accountPass);
        account_url.setText(accountUrl);

        if (accountNotes.isEmpty()) {
            view_notes_button.setManaged(false);
            view_notes_button.setVisible(false);
        } else {
            account_notes.setText(accountNotes);
        }

        account_container.setMinHeight(Main.getMinimumHeight());
    }

    @FXML
    public void updateAccountDetails() {
        int accountId = State.getSelectedAccountId();

        String accountName = account_name.getText();
        if (accountName.equals("")) {
            notice_text.setText("Account name is required.");
            return;
        }

        String accountUser = account_user.getText();
        String accountPass = account_password.getText();
        String accountUrl = account_url.getText();
        String accountNotes = account_notes.getText();

        Auth auth = new Auth();
        String key = auth.getKey();

        Account account = new Account();

        String encryptedName = null;
        String encryptedUser = null;
        String encryptedPass = null;
        String encryptedUrl = null;
        String encryptedNotes = null;

        try {
            encryptedName = Crypt.encrypt(key, accountName);
            encryptedUser = Crypt.encrypt(key, accountUser);
            encryptedPass = Crypt.encrypt(key, accountPass);
            encryptedUrl = Crypt.encrypt(key, accountUrl);
            encryptedNotes = Crypt.encrypt(key, accountNotes);
        } catch (Exception ignored) {}

        account.update(accountId, encryptedName, encryptedUser, encryptedPass, encryptedUrl, encryptedNotes);

        notice_text.setText("Account Updated");
    }

    @FXML
    public void editAccountAction() {
        update_account_button.setVisible(true);
        delete_account_button.setVisible(true);
        generate_password_icon.setVisible(true);
        edit_account_link.setVisible(false);
        account_name.setEditable(true);
        account_user.setEditable(true);
        account_password.setVisible(true);
        account_password.setEditable(true);
        account_password_masked.setVisible(false);
        account_url.setEditable(true);
        view_notes_button.setVisible(false);
        edit_notes_button.setDisable(false);
        edit_notes_button.setVisible(true);
        password_clipboard.setVisible(false);
        show_password.setVisible(false);
        username_clipboard.setVisible(false);
        go_to_url.setVisible(false);
    }

    @FXML
    public void deleteAccountAction() throws IOException, ParseException {
        if(this.clickCount < 1) {
            this.notice_text.setText("Click delete once more to permanently remove this account.");
            this.clickCount++;
            return;
        }

        int accountId = State.getSelectedAccountId();
        Auth auth = new Auth();
        if (!auth.getApiToken().equals("") && this.deleteFromCloudId == 0) {
            Api api = new Api();
            Account account = new Account();
            AccountEntity accountEntity = account.getById(accountId);
            assert accountEntity != null;
            int cloudId = 0;
            try {
                cloudId = accountEntity.getCloudId();
            } catch (NullPointerException ignored) {}
            ApiResponse response = api.getAccount(cloudId);
            if (response.getResponseMessage().equals("OK")) {
                JSONObject requestObject = parse(response);
                JSONObject accountArray = (JSONObject) requestObject.get("account");

                if (accountArray != null) {
                    this.deleteFromCloudId = cloudId;
                }
            }
        }

        Account account = new Account();
        account.delete(accountId);

        if (!auth.getApiToken().equals("") && this.deleteFromCloudId != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete cloud backup?");
            alert.setHeaderText("Cloud Backup");
            alert.setContentText("Would you like to delete the cloud backup as well?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                Api api = new Api();
                ApiResponse response = api.deleteAccount(this.deleteFromCloudId);
            }
        }

        newContainerScene("account-list");
    }

    @FXML
    public void passwordModalView() throws IOException {
        Stage stage = Main.getStage();
        Stage newStage = new Stage();
        newStage.initOwner(stage);
        VBox modal = FXMLLoader.load(getClass().getResource("/generate-password.fxml"));
        newStage.setScene(new Scene(modal, 300, 350));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();
    }

    @FXML
    public void copyToClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(account_password.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void copyUsernameToClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(account_user.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void goToUrl() {
        String url = account_url.getText();
        try {
            new ProcessBuilder("x-www-browser", url).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showPassword() {
        if (account_password.isVisible()) {
            account_password.setVisible(false);
            account_password_masked.setVisible(true);
        } else {
            account_password.setVisible(true);
            account_password_masked.setVisible(false);
        }
    }
}
