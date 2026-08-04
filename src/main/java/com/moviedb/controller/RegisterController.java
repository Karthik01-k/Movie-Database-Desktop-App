package com.moviedb.controller;

import com.moviedb.service.AuthService;
import com.moviedb.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        new Thread(() -> {
            try {
                authService.register(username, email, password);
                javafx.application.Platform.runLater(() -> SceneManager.switchTo("login.fxml"));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onGoLogin() {
        SceneManager.switchTo("login.fxml");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
