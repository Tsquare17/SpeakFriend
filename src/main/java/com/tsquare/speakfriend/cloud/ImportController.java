package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ImportController extends Controller {
    @FXML VBox account_list_container;
    @FXML AnchorPane account_anchor;
    @FXML ScrollPane account_list_scrollpane;
    @FXML Text notice_text;

    @FXML
    public void initialize() {
        account_list_scrollpane.setFitToWidth(true);
        account_list_scrollpane.setFitToHeight(true);

        Stage stage = Main.getStage();
        // Bind the scroll pane's size to the parent anchor pane's size.
        stage.heightProperty().addListener(e -> {
            account_list_container.setPrefHeight(stage.getHeight() - 30);
            account_anchor.setPrefHeight(stage.getHeight() - 60);
        });
        stage.widthProperty().addListener(e -> account_list_scrollpane.setPrefWidth(stage.getWidth()));

        VBox accountsVBox = new VBox();
        accountsVBox.setFillWidth(true);

        CheckBox selectAll = new CheckBox("Toggle All");
        selectAll.setPadding(new Insets(0, 0, 0, 30));
        selectAll.setSelected(true);

        List<List<String>> accountList = AccountList.getStagedImports();

        int count = 0;
        List<HBox> accountBoxes = new ArrayList<>();
        List<String> accountIds = new ArrayList<>();
        for (List<String> newAccount : accountList) {

            String cloudId = newAccount.get(0);
            String accountName = newAccount.get(1);

            HBox accountBox = new HBox();
            accountBox.getChildren().add(new Label(accountName));
            accountBox.setId(accountName.replace(" ", "$:$").toLowerCase());
            accountBox.setPadding(new Insets(20, 30, 20, 30));
            accountBox.setCursor(Cursor.HAND);

            Region spaceFiller = new Region();
            HBox.setHgrow(spaceFiller, Priority.ALWAYS);
            accountBox.getChildren().add(spaceFiller);

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            String accountId = "checkbox_" + cloudId;
            accountIds.add(accountId);
            checkBox.setId(accountId);
            accountBox.getChildren().add(checkBox);

            Color accountColor = Color.rgb(47, 52, 57);
            if (count % 2 != 0) {
                accountColor = Color.rgb(43, 46, 52);
            }

            accountBox.setBackground(
                    new Background(
                            new BackgroundFill(accountColor, CornerRadii.EMPTY, Insets.EMPTY)
                    )
            );
            accountBoxes.add(accountBox);
            count++;
        }

        accountsVBox.getChildren().addAll(accountBoxes);
        account_list_scrollpane.setContent(accountsVBox);

        selectAll.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Scene scene = Main.getScene();
            for (String id: accountIds) {
                CheckBox checkBox = (CheckBox) scene.lookup("#" + id);
                checkBox.setSelected(newValue);
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().add(selectAll);
        account_list_container.getChildren().add(2, hBox);
        account_list_container.setPrefHeight(Main.getStage().getHeight());

        if (accountList.size() == 0) {
            notice_text.setText("No accounts to import");
        }
    }

    @FXML
    public void importAction() {
        Auth auth = new Auth();
        String key = auth.getKey();

        List<List<String>> accountList = AccountList.getStagedImports();
        for (List<String> account : accountList) {
            int cloudId = Integer.parseInt(account.get(0));

            Account importAccount = new Account();
            AccountEntity existing = importAccount.getByCloudId(cloudId);

            String accountName = account.get(1);
            String accountUser = account.get(2);
            String accountPass = account.get(3);
            String accountUrl = account.get(4);
            String accountNotes = account.get(5);

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

            if (existing == null) {
                importAccount.create(
                        auth.getId(),
                        encryptedName,
                        encryptedUser,
                        encryptedPass,
                        encryptedUrl,
                        encryptedNotes,
                        cloudId
                );
            } else {
                importAccount.update(
                        existing.getId().getValue(),
                        encryptedName,
                        encryptedUser,
                        encryptedPass,
                        encryptedUrl,
                        encryptedNotes,
                        cloudId
                );
            }
        }

        notice_text.setText("Accounts imported");
    }
}
