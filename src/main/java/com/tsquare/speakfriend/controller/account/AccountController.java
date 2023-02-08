package com.tsquare.speakfriend.controller.account;

import com.tsquare.speakfriend.utils.Crypt;
import com.tsquare.speakfriend.utils.Password;
import com.tsquare.speakfriend.database.entity.AccountEntity;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;

import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountController extends Controller {
    @FXML private TextField account_notes;
    @FXML private HTMLEditor account_notes_edited;
    @FXML private Button account_notes_update_button;
    @FXML private Slider password_length;
    @FXML private CheckBox specify_digits;
    @FXML private CheckBox specify_symbols;
    @FXML private Slider number_of_digits;
    @FXML private Slider number_of_symbols;

    @FXML
    public void passwordModalView() throws IOException {
        Stage stage = Main.getStage();
        Stage newStage = new Stage();
        newStage.initOwner(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/generate-password.fxml"));
        VBox modal = fxmlLoader.load();
        newStage.setScene(new Scene(modal, 300, 350));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();
    }

    @FXML
    public void accountNotesModalView() throws IOException, SQLException {
        Stage stage = Main.getStage();

        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();

        ApplicationSession applicationSession = ApplicationSession.getInstance();

        AccountsModel accountsModel = new AccountsModel();
        ResultSet resultSet = accountsModel.getAccount(applicationSession.getSelectedAccountId());

        AccountEntity accountEntity = new AccountEntity(resultSet);

        resultSet.close();
        accountsModel.close();

        String accountNotes = "";
        Crypt crypt = new Crypt();
        try {
            accountNotes = crypt.decrypt(key, accountEntity.getNotes());
        } catch (Exception ignore) {}

        Stage newStage = new Stage();
        newStage.initOwner(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/account-notes.fxml"));
        fxmlLoader.setController(this);
        VBox modal = fxmlLoader.load();
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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edit-account-notes.fxml"));
        fxmlLoader.setController(this);

        VBox modal = fxmlLoader.load();
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

        if(specify_digits.isSelected()) {
            password.setNumberOfDigits(digits);
        }

        if(specify_symbols.isSelected()) {
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

    @FXML
    public void viewAccountsView() {
        toAccounts();
    }
}
