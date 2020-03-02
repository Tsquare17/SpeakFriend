package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.account.preview.AccountPreview;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.crypt.Password;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.utils.AccountPreviewComparator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
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
    @FXML private TextField account_notes;
    @FXML private HTMLEditor account_notes_edited;
    @FXML private Label notice_text;
    @FXML private Button update_account_button;
    @FXML private Button delete_account_button;
    @FXML private Hyperlink edit_account_link;
    @FXML private Button create_account_button;
    @FXML private Button edit_notes_button;
    @FXML private Button view_notes_button;
    @FXML private Button account_notes_update_button;
    @FXML private ImageView generate_password_icon;
    @FXML private Slider password_length;
    @FXML private CheckBox specify_digits;
    @FXML private CheckBox specify_symbols;
    @FXML private Slider number_of_digits;
    @FXML private Slider number_of_symbols;
    @FXML private TextField account_filter_field;
    private int clickCount;

    @FXML
    public void createAccountAction() {
        Auth auth = new Auth();
        int id = auth.getId();
        String key = auth.getKey();

        String accountName = this.getEncryptedText(key, account_name);
        if(accountName.equals("")) {
            notice_text.setText("Account name is required.");
            return;
        }

        String accountPass = this.getEncryptedText(key, account_password);
        String accountUrl = this.getEncryptedText(key, account_url);
        String accountNotes = this.getEncryptedText(key, account_notes);

        Account account = new Account();
        account.create(id, accountName, accountPass, accountUrl, accountNotes);
        notice_text.setText("Account Created");
        create_account_button.setVisible(false);
    }

    @FXML
    public void createAccountView() throws IOException {
        this.newScene("create-account");
    }

    @FXML
    public void listAccountsView() throws IOException {
        // Get decryption key.
        Auth auth  = new Auth();
        int userId     = auth.getId();
        String key = auth.getKey();

        URL file = Controller.class.getResource("/account-list.fxml");

        Parent scene       = FXMLLoader.load(file);
        Stage stage        = Main.getStage();
        Scene currentScene = stage.getScene();

        // Get the account list container.
        AnchorPane accountListAnchor = (AnchorPane) scene.lookup("#accountAnchor");
        accountListAnchor.setPadding(new Insets(30, 0, 0, 0));

        VBox accountListContainerContainer = (VBox) scene.lookup("#accountListContainer");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        VBox accountsVBox = new VBox();
        accountsVBox.setFillWidth(true);

        // Get all accounts for the current user.
        List<AccountEntity> accounts = AccountList.get(userId);

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

        int count = 0;
        for(AccountPreview item: decryptedList) {
            HBox accountBox = new HBox();
            accountBox.getChildren().add(new Label(item.getName()));
            accountBox.setId(item.getName().replace(" ", "$:$").toLowerCase());
            accountBox.setPadding(new Insets(20, 30, 20, 30));
            accountBox.setCursor(Cursor.HAND);
            if (count % 2 != 0) {
                Color darkGrey = Color.rgb(43, 46, 52);
                accountBox.setBackground(
                        new Background(
                                new BackgroundFill(darkGrey, CornerRadii.EMPTY, Insets.EMPTY)
                        )
                );
            }
            accountBox.setOnMouseClicked(e -> {
                try {
                    this.showAccountDetails(item.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            accountsVBox.getChildren().add(accountBox);
            count++;
        }

        accountsVBox.setId("account_list");
        scrollPane.setContent(accountsVBox);
        accountListAnchor.getChildren().add(scrollPane);

        VBox box = FXMLLoader.load(getClass().getResource("/container.fxml"));
        box.getChildren().add(scene);

        scrollPane.setPrefWidth(stage.getWidth());
        scrollPane.setPrefHeight(stage.getHeight() - 60);
        // Bind the scroll pane's size to the parent anchor pane's size.
        stage.heightProperty().addListener(e -> {
            accountListContainerContainer.setPrefHeight(stage.getHeight() - 30);
            accountListAnchor.setPrefHeight(stage.getHeight() - 60);
        });
        stage.widthProperty().addListener(e -> {
            scrollPane.setPrefWidth(stage.getWidth());
        });

        stage.setScene(new Scene(box, currentScene.getWidth(), currentScene.getHeight()));
    }

    @FXML
    private void setFilteredList(KeyEvent event) {
        String filter = account_filter_field.getText().replace(" ", "").toLowerCase();

        if(event.getCode().equals(KeyCode.DELETE)) {
            if(filter.isEmpty()) {
                return;
            }
        }

        Scene scene = Main.getScene();

        VBox vBox = (VBox) scene.lookup("#account_list");
        ObservableList<Node> listView = vBox.getChildren();
        for (Node item: listView) {
            if (item.getId().replace("$:$", " ").toLowerCase().startsWith(filter)) {
                item.setVisible(true);
                item.setManaged(true);
            } else {
                item.setVisible(false);
                item.setManaged(false);
            }

        }
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

        Label accountIdField       = (Label) scene.lookup("#account_id");
        TextField accountNameField = (TextField) scene.lookup("#account_name");
        TextField accountPassField = (TextField) scene.lookup("#account_password");
        TextField accountUrlField  = (TextField) scene.lookup("#account_url");
        TextField accountNotesField = (TextField) scene.lookup("#account_notes");

        accountIdField.setText(accountId);
        accountNameField.setText(accountName);
        accountPassField.setText(accountPass);
        accountUrlField.setText(accountUrl);
        accountNotesField.setText(accountNotes);

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    @FXML
    public void updateAccountDetails() {
        int accountId = Integer.parseInt(account_id.getText());
        String accountName = account_name.getText();
        if (accountName.equals("")) {
            notice_text.setText("Account name is required.");
            return;
        }

        String accountPass = account_password.getText();
        String accountUrl = account_url.getText();
        String accountNotes = account_notes.getText();

        Auth auth = new Auth();
        String key = auth.getKey();

        Account account = new Account();

        String encryptedName = null;
        String encryptedPass = null;
        String encryptedUrl = null;
        String encryptedNotes = null;

        try {
            encryptedName = Crypt.encrypt(key, accountName);
            encryptedPass = Crypt.encrypt(key, accountPass);
            encryptedUrl = Crypt.encrypt(key, accountUrl);
            encryptedNotes = Crypt.encrypt(key, accountNotes);
        } catch (Exception ignored) {};

        account.update(accountId, encryptedName, encryptedPass, encryptedUrl, encryptedNotes);

        notice_text.setText("Account Updated");
    }

    @FXML
    public void editAccountAction() {
        update_account_button.setVisible(true);
        delete_account_button.setVisible(true);
        generate_password_icon.setVisible(true);
        edit_account_link.setVisible(false);
        account_name.setEditable(true);
        account_password.setEditable(true);
        account_url.setEditable(true);
        view_notes_button.setVisible(false);
        edit_notes_button.setDisable(false);
        edit_notes_button.setVisible(true);
    }

    @FXML
    public void deleteAccountAction() throws IOException {
        if(this.clickCount < 1) {
            this.notice_text.setText("Click delete once more to permanently remove this account.");
            this.clickCount++;
            return;
        }
        int accountId = Integer.parseInt(account_id.getText());
        Account account = new Account();
        account.delete(accountId);
        this.listAccountsView();
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
    public void accountNotesModalView() throws IOException {
        Stage stage = Main.getStage();
        String accountId = account_id.getText();

        Auth auth = new Auth();
        String key = auth.getKey();

        Account account = new Account();
        AccountEntity accountEntity = account.getById(Integer.parseInt(accountId));

        String accountNotes = "";
        try {
            assert accountEntity != null;
            accountNotes = Crypt.decrypt(key, accountEntity.getNotes());
        } catch (Exception ignore) {};

        Stage newStage = new Stage();
        newStage.initOwner(stage);
        VBox modal = FXMLLoader.load(getClass().getResource("/account-notes.fxml"));
        newStage.setScene(new Scene(modal, 600, 400));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();

        Scene newScene = newStage.getScene();

        WebView webView = (WebView) newScene.lookup("#account_notes_view");
        webView.setContextMenuEnabled(false);
        if(accountNotes.contains("contenteditable=\"true\"")){
            accountNotes = accountNotes.replace("contenteditable=\"true\"", "contenteditable=\"false\"");
        }

        webView.getEngine().loadContent(accountNotes);
    }

    @FXML
    public void accountNotesEditModalView() throws IOException {
        String accountNotes = account_notes.getText();

        Stage stage = Main.getStage();
        Stage newStage = new Stage();
        newStage.initOwner(stage);

        VBox modal = FXMLLoader.load(getClass().getResource("/edit-account-notes.fxml"));
        newStage.setScene(new Scene(modal, 600, 400));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();

        Scene newScene = newStage.getScene();
        HTMLEditor htmlEditor = (HTMLEditor) newScene.lookup("#account_notes_edited");
        htmlEditor.setHtmlText(accountNotes);
    }

    @FXML
    public void generatePasswordAction() {
        int passwordLength = (int) password_length.getValue();
        int digits = (int) number_of_digits.getValue();
        int symbols = (int) number_of_symbols.getValue();

        Password password = new Password();
        password.setPasswordLength(passwordLength);

        if(this.specify_digits.isSelected()) {
            password.setNumberOfDigits(digits);
        }

        if(this.specify_symbols.isSelected()) {
            password.setNumberOfSymbols(symbols);
        }

        String newPassword = password.generate();

        TextField accountPassField = (TextField) Main.getScene().lookup("#account_password");
        accountPassField.setText(newPassword);
    }

    @FXML
    public void updateAccountNotesAction() {
        String notes = account_notes_edited.getHtmlText();

        TextField accountNotes = (TextField) Main.getScene().lookup("#account_notes");
        accountNotes.setText(notes);

        Stage stage = (Stage) account_notes_update_button.getScene().getWindow();
        stage.close();
    }
}
