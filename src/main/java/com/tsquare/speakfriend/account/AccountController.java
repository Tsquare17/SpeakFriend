package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
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

        ObservableList<GridPane> gridList = FXCollections.observableArrayList();
        ListView<GridPane> list = new ListView<>();
        StackPane accountList = (StackPane) scene.lookup("#accountList");
        int accountCount  = AccountList.generate(id);

        // TODO: Refactor account retrieval.
        for (int i = 0; i < accountCount; i++) {
            int accountId   = AccountList.getId(i);
            String accountName = AccountList.getName(i);
            String accountPass = AccountList.getPass(i);
            String accountUrl  = AccountList.getUrl(i);
            String accountNotes = AccountList.getNotes(i);

            GridPane gridPane = new GridPane();
            gridPane.setId("account_" + accountId);
            gridPane.setOnMouseClicked(mouseEvent -> {
                try {
                    this.AccountDetails(accountId, accountName, accountPass, accountUrl, accountNotes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            gridPane.add(new Label(accountName), 0, 0);
            gridList.add(gridPane);
        }
        list.getItems().addAll(gridList);
        accountList.getChildren().add(list);

        stage.setScene(new Scene(scene, currentScene.getWidth(), currentScene.getHeight()));
    }

    protected void AccountDetails(int id, String name, String pass, String url, String notes) throws IOException {

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
