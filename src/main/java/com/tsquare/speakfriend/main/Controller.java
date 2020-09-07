package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.api.Api;
import com.tsquare.speakfriend.api.ApiResponse;
import com.tsquare.speakfriend.auth.Auth;
import com.tsquare.speakfriend.crypt.Crypt;

import com.tsquare.speakfriend.database.account.AccountList;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class Controller {
    @FXML private URL location;

    public void goBack() throws IOException {
        Parent scene = FXMLLoader.load(Main.previousUrl);

        Main.previousUrl = location;

        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        Scene newScene = new Scene(scene, currentScene.getWidth(), currentScene.getHeight());
        Main.setTimer(newScene);
        stage.setScene(newScene);
    }

    public void goBack(Integer delay) {
        PauseTransition pause = new PauseTransition(
                Duration.seconds(delay)
        );
        pause.setOnFinished(e -> {
            try {
                goBack();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        pause.play();
    }

    @FXML
    public void newScene(String nextScene) throws IOException {
        Main.previousUrl = location;

        String resource = "/" + nextScene + ".fxml";
        URL file = Controller.class.getResource(resource);

        Parent scene = FXMLLoader.load(file);
        Stage stage = Main.getStage();
        Scene currentScene = stage.getScene();

        Scene newScene = new Scene(scene, currentScene.getWidth(), currentScene.getHeight());
        Main.setTimer(newScene);
        stage.setScene(newScene);
    }

    public void newContainerScene(String resource) throws IOException {
        Main.previousUrl = location;

        URL file = Nav.class.getResource("/" + resource + ".fxml");

        Parent scene       = FXMLLoader.load(file);
        Stage stage        = Main.getStage();
        Scene currentScene = stage.getScene();

        VBox box = FXMLLoader.load(getClass().getResource("/container.fxml"));
        box.getChildren().add(scene);

        Scene newScene = new Scene(box, currentScene.getWidth(), currentScene.getHeight());
        Main.setTimer(newScene);
        stage.setScene(newScene);
    }

    @FXML
    public void transitionScene(String newScene, int duration) {
        PauseTransition pause = new PauseTransition(
                Duration.seconds(duration)
        );
        pause.setOnFinished(e -> {
            try {
                this.newScene(newScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    @FXML
    public void transitionContainerScene(String newScene, int duration) {
        PauseTransition pause = new PauseTransition(
                Duration.seconds(duration)
        );
        pause.setOnFinished(e -> {
            try {
                this.newContainerScene(newScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    @FXML
    public void transitionContainerScene(String newScene) {
        transitionContainerScene(newScene, 0);
    }

    public void transitionToAccounts() {
        Main.previousUrl = location;

        PauseTransition pause = new PauseTransition(
                Duration.seconds(2)
        );
        pause.setOnFinished(e -> {
            toAccounts();
        });
        pause.play();
    }

    public void toAccounts() {
        try {
            AccountController accountController = new AccountController();
            accountController.listAccountsView();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected String getEncryptedText(String key, TextField field) {
        if(!field.getText().isEmpty()) {
            try {
                return Crypt.encrypt(key, field.getText());
            } catch(Exception ignored) {}
        }
        return "";
    }

    protected String getDecryptedText(String encrypted) {
        Auth auth = new Auth();
        String key = auth.getKey();

        String decrypted = "";
        try {
            decrypted = Crypt.decrypt(key, encrypted);
        } catch (Exception ignore) {}

        return decrypted;
    }

    protected JSONObject parse(ApiResponse response) throws ParseException {
        String body = response.getResponseBody();
        JSONParser parser = new JSONParser();

        return (JSONObject) parser.parse(body);
    }

    protected void loadingImports() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws ParseException {
                Api api = new Api();
                ApiResponse response = api.getAccounts();

                if (response.getResponseMessage().equals("OK")) {

                    JSONObject requestObject = parse(response);

                    JSONArray accountsArray = (JSONArray) requestObject.get("accounts");

                    List<List<String>> newAccounts = new ArrayList<>();
                    for (Object o : accountsArray) {
                        List<String> newAccount = new ArrayList<>();
                        JSONObject newImport = (JSONObject) o;

                        Long rawCloudId = (Long) newImport.get("id");
                        String cloudId = rawCloudId.toString();
                        String encryptedName = (String) newImport.get("account_name");
                        String encryptedUser = (String) newImport.get("account_user");
                        String encryptedPass = (String) newImport.get("account_pass");
                        String encryptedUrl = (String) newImport.get("account_url");
                        String encryptedNotes = (String) newImport.get("account_notes");

                        Auth auth = new Auth();
                        String key = auth.getApiKey();

                        String accountName = "";
                        String accountUser = "";
                        String accountPass = "";
                        String accountUrl = "";
                        String accountNotes = "";

                        try {
                            accountName = Crypt.decrypt(key, encryptedName, 2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            accountUser = Crypt.decrypt(key, encryptedUser, 2000);
                        } catch (Exception ignored) {
                        }
                        try {
                            accountPass = Crypt.decrypt(key, encryptedPass, 2000);
                        } catch (Exception ignored) {
                        }
                        try {
                            accountUrl = Crypt.decrypt(key, encryptedUrl, 2000);
                        } catch (Exception ignored) {
                        }
                        try {
                            accountNotes = Crypt.decrypt(key, encryptedNotes, 2000);
                        } catch (Exception ignored) {
                        }

                        newAccount.add(cloudId);
                        newAccount.add(accountName);
                        newAccount.add(accountUser);
                        newAccount.add(accountPass);
                        newAccount.add(accountUrl);
                        newAccount.add(accountNotes);

                        newAccounts.add(newAccount);
                    }

                    AccountList.stageImports(newAccounts);
                }

                return null;
            }
        };

        task.setOnSucceeded(taskFinishEvent -> {
            try {
                newContainerScene("import");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        new Thread(task).start();
    }
}
