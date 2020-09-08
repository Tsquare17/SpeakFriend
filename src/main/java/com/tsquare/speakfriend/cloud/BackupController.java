package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.account.preview.AccountPreview;
import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountEntity;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.state.State;
import javafx.concurrent.Task;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupController extends Controller {
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

        // Collect list of decrypted account previews.
        List<AccountPreview> decryptedList = AccountList.getPreviews();

        int count = 0;
        List<HBox> accountBoxes = new ArrayList<>();
        List<String> accountIds = new ArrayList<>();
        for(AccountPreview item: decryptedList) {
            HBox accountBox = new HBox();
            accountBox.getChildren().add(new Label(item.getName()));
            accountBox.setId(item.getName().replace(" ", "$:$").toLowerCase());
            accountBox.setPadding(new Insets(20, 30, 20, 30));
            accountBox.setCursor(Cursor.HAND);

            Region spaceFiller = new Region();
            HBox.setHgrow(spaceFiller, Priority.ALWAYS);
            accountBox.getChildren().add(spaceFiller);

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            String accountId = "checkbox_" + item.getId();
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
            accountBox.setOnMouseClicked(e -> {
                try {
                    this.showAccountDiff(item.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
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

        accountsVBox.getChildren().add(0, selectAll);
    }

    public void showAccountDiff(int id) throws IOException {

    }

    public void sendBackups() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws ParseException {
                Auth auth = new Auth();

                List<AccountPreview> accountPreviews = AccountList.getPreviews();
                List<Integer> selectedAccounts = new ArrayList<>();
                for (AccountPreview accountPreview : accountPreviews) {
                    String checkboxId = "#checkbox_" + accountPreview.getId();
                    CheckBox checkBox = (CheckBox) account_list_scrollpane.lookup(checkboxId);

                    if (checkBox != null && checkBox.isSelected()) {
                        int accountId = accountPreview.getId();
                        selectedAccounts.add(accountId);
                    }
                }

                ArrayList<List<String>> decryptedList = AccountList.getDecryptedAccounts(selectedAccounts);

                Api api = new Api();

                ArrayList<List<String>> encryptedList = AccountList.lock(decryptedList, auth.getApiKey());

                ApiResponse response = api.sendBackups(encryptedList);

                if (response.getResponseMessage().equals("OK")) {
                    notice_text.setText("Accounts successfully backed up");

                    JSONObject requestObject = parse(response);

                    JSONArray accountsArray = (JSONArray) requestObject.get("accounts");

                    for (Object o : accountsArray) {
                        JSONObject newBackup = (JSONObject) o;
                        String id = (String) newBackup.get("account_id");
                        String cloudId = (String) newBackup.get("cloud_id");

                        Account account = new Account();
                        account.update(Integer.parseInt(id), Integer.parseInt(cloudId));
                    }

                    return null;
                }

                notice_text.setText(response.getErrors().toString());

                return null;
            }
        };

        task.setOnSucceeded(taskFinishEvent -> {
            transitionContainerScene("account-list");
        });
        new Thread(task).start();
    }

    public void backupAction() throws IOException {
        State.setLoadingMessage("Backing up accounts...");
        newScene("loading");
        sendBackups();
    }
}
