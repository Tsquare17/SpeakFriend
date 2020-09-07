package com.tsquare.speakfriend.cloud;

import com.tsquare.speakfriend.account.preview.AccountPreview;
import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
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
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupController extends Controller {
    @FXML AnchorPane account_anchor;
    @FXML ScrollPane account_list_scrollpane;
    @FXML Text notice_text;

    @FXML
    public void initialize() throws ParseException {
        Auth auth = new Auth();
        Api api = new Api();
        String key = auth.getKey();

        account_list_scrollpane.setFitToWidth(true);
        account_list_scrollpane.setFitToHeight(true);

        VBox accountsVBox = new VBox();
        accountsVBox.setFillWidth(true);

        CheckBox selectAll = new CheckBox("Toggle All");
        selectAll.setPadding(new Insets(0, 0, 0, 60));
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


//        ApiResponse response = api.getAccounts();
//
//        if (response.getResponseMessage().equals("OK")) {
//
//            JSONObject requestObject = parse(response);
//
//            JSONArray accountsArray = (JSONArray) requestObject.get("accounts");
//
//            AccountController accountController = new AccountController();
//            List<Account> accountsList = accountController.getAccountsFromJson(accountsArray);
//
//            String test = "";
//            return;
//        }
//
//        notice_text.setText(response.getErrors().toString());
    }

    public void showAccountDiff(int id) throws IOException {

    }

    public void backupAction() {
        Auth auth = new Auth();
        ArrayList<List<String>> decryptedList = AccountList.getDecryptedAccounts();

        Api api = new Api();

        ArrayList<List<String>> encryptedList = AccountList.lock(decryptedList, auth.getApiKey());

        // go through and map

        ApiResponse response = api.sendBackups(encryptedList);

        if (response.getResponseMessage().equals("OK")) {
            notice_text.setText("Accounts successfully backed up");
        } else {
            notice_text.setText(response.getErrors().toString());
        }
    }
}
