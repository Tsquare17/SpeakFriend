package com.tsquare.speakfriend.controller.account;

import com.tsquare.speakfriend.database.entity.AccountPreviewEntity;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.session.AccountListSession;
import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.session.UserSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportController extends Controller {
    @FXML VBox account_list_container;
    @FXML AnchorPane account_anchor;
    @FXML ScrollPane account_list_scrollpane;
    @FXML Text notice_text;
    @FXML Button export_button;

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
        AccountListSession accountListSession = AccountListSession.getInstance();
        List<AccountPreviewEntity> decryptedList = accountListSession.getPreviews();

        if (decryptedList.size() == 0) {
            export_button.setManaged(false);
            export_button.setVisible(false);
        }

        int count = 0;
        List<HBox> accountBoxes = new ArrayList<>();
        List<String> accountIds = new ArrayList<>();
        for(AccountPreviewEntity item: decryptedList) {
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
            accountBoxes.add(accountBox);
            count++;
        }

        accountsVBox.getChildren().addAll(accountBoxes);
        account_list_scrollpane.setContent(accountsVBox);
        account_list_scrollpane.setPrefWidth(stage.getWidth());
        account_list_scrollpane.setPrefHeight(stage.getHeight());

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
    }

    public void export() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws SQLException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
                AccountListSession accountListSession = AccountListSession.getInstance();
                List<AccountPreviewEntity> accountPreviews = accountListSession.getPreviews();
                List<Integer> selectedAccounts = new ArrayList<>();
                for (AccountPreviewEntity accountPreview : accountPreviews) {
                    String checkboxId = "#checkbox_" + accountPreview.getId();
                    CheckBox checkBox = (CheckBox) account_list_scrollpane.lookup(checkboxId);

                    if (checkBox != null && checkBox.isSelected()) {
                        int accountId = accountPreview.getId();
                        selectedAccounts.add(accountId);
                    }
                }

                UserSession userSession = UserSession.getInstance();

                ArrayList<List<String>> decryptedList = accountListSession.getDecryptedAccounts(selectedAccounts);

                ArrayList<List<String>> encryptedList = accountListSession.lock(decryptedList, userSession.getKey());

                String hash = userSession.getPassHash();

                String accounts = "{\"accounts\": " + JSONArray.toJSONString(encryptedList) + ", \"hash\": \"" + hash + "\"}";

                ApplicationSession applicationSession = ApplicationSession.getInstance();
                applicationSession.setExportFileString(accounts);

                return null;
            }
        };

        task.setOnSucceeded(taskFinishEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save as");

            String date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());

            fileChooser.setInitialFileName("speakfriend-" + date + ".json");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.json"));

            File file = fileChooser.showSaveDialog(Main.getStage());

            ApplicationSession applicationSession = ApplicationSession.getInstance();

            if (file != null) {
                try {
                    saveTextToFile(applicationSession.getExportFileString(), file);

                    transitionToAccounts();
                } catch (FileNotFoundException e) {
                    // TODO: Figure out a good way to inform the user of export fail.
                   notice_text.setText("There was a problem exporting your data.");
                }
            }

            transitionToAccounts();
        });
        new Thread(task).start();
    }

    public void exportAction() throws IOException {
        ApplicationSession applicationSession = ApplicationSession.getInstance();
        applicationSession.setLoadingMessage("Exporting accounts...");
        newScene("loading");
        export();
    }

    private void saveTextToFile(String content, File file) throws FileNotFoundException {
        PrintWriter writer;
        writer = new PrintWriter(file);
        writer.println(content);
        writer.close();
    }
}
