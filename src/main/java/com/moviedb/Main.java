package com.moviedb;

import atlantafx.base.theme.NordDark;
import com.moviedb.config.DatabaseConfig;
import com.moviedb.util.ImageCache;
import com.moviedb.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Apply AtlantaFX dark theme globally
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());

        stage.setTitle("MovieExplorer");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaximized(true);

        SceneManager.init(stage);

        // Load custom stylesheet on top of AtlantaFX theme
        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String css = getClass().getResource("/css/app.css").toExternalForm();
                if (!newScene.getStylesheets().contains(css))
                    newScene.getStylesheets().add(css);
            }
        });

        SceneManager.switchTo("login.fxml");
    }

    @Override
    public void stop() {
        DatabaseConfig.close();
        ImageCache.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
