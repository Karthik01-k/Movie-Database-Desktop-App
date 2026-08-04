package com.moviedb.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFile) {
        switchTo(fxmlFile, null);
    }

    public static void switchTo(String fxmlFile, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/fxml/" + fxmlFile)
            );
            Parent root = loader.load();

            // Pass data to controller if it implements DataReceiver
            if (data != null && loader.getController() instanceof DataReceiver receiver) {
                receiver.receiveData(data);
            }

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root, 1280, 800);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlFile, e);
        }
    }

    public static Stage getStage() { return primaryStage; }

    // Implement this interface in controllers that need to receive data
    public interface DataReceiver {
        void receiveData(Object data);
    }
}
