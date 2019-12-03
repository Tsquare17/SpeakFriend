package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private ListView accountList;

    @FXML
    public void createAccountAction(ActionEvent event) {
        Auth auth = new Auth();
        int id = auth.getId();
        String pass;

        if(!password.getText().isEmpty()) {
            pass = Crypt.encrypt(id, password.getText());
        } else {
            pass = null;
        }

        Account account = new Account();
        account.create(id, account_name.getText(), pass, url.getText(), notes.getText());
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

        String resource = "/account-list.fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        ListView listView = (ListView) scene.lookup("#accountList");

        int accountCount  = AccountList.generate(id);

        // TODO: Refactor this awful code.
        for (int i = 0; i < accountCount; i++) {
            int accountId   = AccountList.getId(i);
            String accountName = AccountList.getName(i);
            String accountUrl  = AccountList.getUrl(i);
            String accountNotes = AccountList.getNotes(i);
            // TODO: Add items to listview in scene
            listView.getItems().add(new Button(accountName));
        }

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }
}
