package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.database.schema.Schema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
// import org.scenicview.ScenicView;

import java.io.File;

public class Main extends Application {
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return stage.getScene();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.setup();

        stage = primaryStage;
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
        Parent root = FXMLLoader.load(getClass().getResource("/sign-in.fxml"));
        stage.setTitle("Speak Friend");
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();

        // ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected static void setup() {
        File dir = new File(System.getProperty("user.home") + "/.speakfriend");
        if(!dir.exists()) {
            dir.mkdir();
        }

        Schema schema = new Schema();
        schema.up();
    }
}
