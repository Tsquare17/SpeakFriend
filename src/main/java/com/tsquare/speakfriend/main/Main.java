package com.tsquare.speakfriend.main;

import com.tsquare.speakfriend.database.schema.Schema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;
    private static Scene scene;

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return scene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Schema schema = new Schema();
        schema.up();
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/entry.fxml"));
        stage.setTitle("Speak Friend and Enter");
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
