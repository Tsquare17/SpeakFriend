package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.account.Account;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.state.State;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class ImportController extends Controller {
    @FXML VBox account_list_container;
    @FXML AnchorPane account_anchor;
    @FXML ScrollPane account_list_scrollpane;
    @FXML Text notice_text;
    @FXML Button import_button;
    @FXML VBox password_field_container;
    @FXML Label password_label;
    @FXML PasswordField password_field;
    @FXML TextField plain_text_password_field;
    @FXML ImageView show_password;
    @FXML HBox password_tooltip_container;

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

        account_list_container.setPrefHeight(Main.getStage().getHeight());

        Tooltip tooltip = new Tooltip("Enter the password for the account used to export the selected file.");
        tooltip.setShowDelay(Duration.millis(200));
        Tooltip.install(password_tooltip_container, tooltip);
    }

    @FXML
    public void handleKeyAction(KeyEvent event) {
        if (plain_text_password_field.isVisible()) {
            password_field.setText(plain_text_password_field.getText());
        } else {
            plain_text_password_field.setText(password_field.getText());
        }

        import_button.setDisable(password_field.getText().isEmpty());
    }

    @FXML
    public void selectAction() throws IOException {
        if (password_field.getText().isEmpty()) {
            notice_text.setText("You must enter the password for the user that exported the accounts.");

            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as");

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.json"));

        File file = fileChooser.showOpenDialog(Main.getStage());

        if (file != null) {
            String contents = Files.readString(file.toPath());

            List<List<String>> unlocked;
            List<List<String>> newAccounts = new ArrayList<>();
            try {
                JSONParser parser = new JSONParser();
                JSONObject accountsObject = (JSONObject) parser.parse(contents);
                JSONArray accountsArray = (JSONArray) accountsObject.get("accounts");

                for (Object o : accountsArray) {
                    List<String> newAccount = new ArrayList<>();
                    JSONArray newImport = (JSONArray) o;

                    String accountName = (String) newImport.get(1);
                    String accountUser = (String) newImport.get(2);
                    String accountPass = (String) newImport.get(3);
                    String accountUrl = (String) newImport.get(4);
                    String accountNotes = (String) newImport.get(5);

                    newAccount.add(accountName);
                    newAccount.add(accountUser);
                    newAccount.add(accountPass);
                    newAccount.add(accountUrl);
                    newAccount.add(accountNotes);

                    newAccounts.add(newAccount);
                }

                String hash = (String) accountsObject.get("hash");
                assert hash != null;
                String key = Crypt.generateKey(hash, password_field.getText());

                assert key != null;
                unlocked = AccountList.unlock(newAccounts, key);

                AccountList.stageImports(unlocked);
            } catch (Exception e) {
                e.printStackTrace();

                notice_text.setText("There was an error processing the file");

                return;
            }

            VBox accountsVBox = new VBox();
            accountsVBox.setFillWidth(true);

            int count = 0;
            List<HBox> accountBoxes = new ArrayList<>();
            List<String> accountIds = new ArrayList<>();
            for (List<String> newAccount : unlocked) {
                String accountName = newAccount.get(0);

                HBox accountBox = new HBox();
                accountBox.getChildren().add(new Label(accountName));
                accountBox.setPadding(new Insets(20, 30, 20, 30));

                Region spaceFiller = new Region();
                HBox.setHgrow(spaceFiller, Priority.ALWAYS);
                accountBox.getChildren().add(spaceFiller);

                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(true);
                String accountId = "checkbox_" + count;
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
            account_list_scrollpane.setPrefHeight(Main.getStage().getHeight());
            account_list_container.setPrefHeight(Main.getStage().getHeight());

            CheckBox selectAll = new CheckBox("Toggle All");
            selectAll.setPadding(new Insets(0, 0, 0, 30));
            selectAll.setSelected(true);

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

            if (unlocked.size() == 0) {
                notice_text.setText("No accounts to import");
                import_button.setManaged(false);
                import_button.setVisible(false);
                import_button.setText("Select File");

                return;
            }

            notice_text.setText("If the accounts shown appear correct, click import");

            // Hide the password label and input
            password_field_container.setVisible(false);
            password_field_container.setManaged(false);

            import_button.setText("Import");
            import_button.setOnAction(e -> {
                State.setLoadingMessage("Importing accounts...");
                try {
                    newScene("loading");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                importAction();
            });
        }
    }

    public void importAction() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call()
                throws InvalidAlgorithmParameterException, NoSuchPaddingException,
                IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
                InvalidKeyException
            {
                Auth auth = new Auth();
                String key = auth.getKey();

                List<List<String>> accountList = AccountList.getStagedImports();
                int counter = 0;
                for (List<String> account : accountList) {
                    String accountName = account.get(0);
                    String accountUser = account.get(1);
                    String accountPass = account.get(2);
                    String accountUrl = account.get(3);
                    String accountNotes = account.get(4);

                    CheckBox checkBox = (CheckBox) account_list_scrollpane.lookup("#checkbox_" + counter);

                    counter++;

                    if (checkBox == null || !checkBox.isSelected()) {
                        continue;
                    }

                    String encryptedName = Crypt.encrypt(key, accountName);
                    String encryptedUser = Crypt.encrypt(key, accountUser);
                    String encryptedPass = Crypt.encrypt(key, accountPass);
                    String encryptedUrl = Crypt.encrypt(key, accountUrl);
                    String encryptedNotes = Crypt.encrypt(key, accountNotes);

                    Account importAccount = new Account();
                    int existingId = AccountList.getAccountIdByName(accountName);
                    if (existingId == 0) {
                        importAccount.create(
                                auth.getId(),
                                encryptedName,
                                encryptedUser,
                                encryptedPass,
                                encryptedUrl,
                                encryptedNotes
                        );
                    } else {
                        importAccount.update(
                                existingId,
                                encryptedName,
                                encryptedUser,
                                encryptedPass,
                                encryptedUrl,
                                encryptedNotes
                        );
                    }
                }

                return null;
            }
        };

        task.setOnSucceeded(taskFinishEvent -> {
            notice_text.setText("Accounts imported");
            transitionContainerScene("account-list", 2);
        });
        new Thread(task).start();
    }

    @FXML
    public void showPasswordAction() {
        if (plain_text_password_field.isVisible()) {
            plain_text_password_field.setVisible(false);
            password_field.setVisible(true);
        } else {
            plain_text_password_field.setVisible(true);
            password_field.setVisible(false);
        }
    }}
