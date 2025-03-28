package com.tsquare.speakfriend.controller.main;

import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.utils.Crypt;
import com.tsquare.speakfriend.utils.Function;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

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
        transitionEvent(e -> {
            try {
                this.newScene(newScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }, duration);
    }

    public void transitionEvent(EventHandler<ActionEvent> event, int duration) {
        PauseTransition pause = new PauseTransition(
            Duration.seconds(duration)
        );
        pause.setOnFinished(event);
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
            newContainerScene("account-list");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected String getEncryptedText(String key, TextField field) {
        if(!field.getText().isEmpty()) {
            try {
                Crypt crypt = new Crypt();
                return crypt.encrypt(key, field.getText());
            } catch(Exception ignored) {}
        }
        return "";
    }

    protected String getDecryptedText(String encrypted) {
        UserSession userSession = UserSession.getInstance();
        String key = userSession.getKey();

        String decrypted = "";
        try {
            Crypt crypt = new Crypt();
            decrypted = crypt.decrypt(key, encrypted);
        } catch (Exception ignore) {}

        return decrypted;
    }

    protected void createModalView(String resource, String title) throws IOException {
        this.createModalView(resource, title, true);
    }

    protected void createModalView(String resource, String title, boolean blocking) throws IOException {
        this.createModalView(resource, title, blocking, () -> {});
    }

    protected void createModalView(String resource, String title, boolean blocking, Function onClose) throws IOException {
        Stage stage;
        if (blocking) {
            stage = Main.getStage();
        } else {
            stage = new Stage();
        }

        Stage newStage = new Stage();
        newStage.initOwner(stage);

        VBox modal = FXMLLoader.load(getClass().getResource(resource));
        newStage.setScene(new Scene(modal, 300, 350));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.setTitle(title);

        try {
            onClose.getClass().getMethod("apply");

            newStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
                try {
                    onClose.apply();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (NoSuchMethodException ignored) {}

        newStage.show();
    }


    protected void createModalConfirmation(String message, Function onConfirm) throws IOException {
        Stage stage;
        stage = Main.getStage();

        Stage newStage = new Stage();
        newStage.initOwner(stage);

        VBox modal = FXMLLoader.load(getClass().getResource("/confirm.fxml"));
        newStage.setScene(new Scene(modal, 300, 350));
        newStage.initModality(Modality.WINDOW_MODAL);

        Label messageLabel = (Label) modal.lookup("#message");
        messageLabel.setText(message);
        messageLabel.setWrapText(true);

        Button cancel = (Button) modal.lookup("#cancel");
        cancel.setOnAction(e -> {
            newStage.close();
        });

        Button confirm = (Button) modal.lookup("#confirm");
        confirm.setOnAction(e -> {
            try {
                onConfirm.apply();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            newStage.close();
        });

        newStage.show();
    }
}
