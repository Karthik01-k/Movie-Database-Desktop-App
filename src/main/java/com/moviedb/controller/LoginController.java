package com.moviedb.controller;

import com.moviedb.service.AuthService;
import com.moviedb.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;

    private final AuthService authService = new AuthService();

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        loginBtn.setDisable(true);
        new Thread(() -> {
            try {
                authService.login(username, password);
                javafx.application.Platform.runLater(() -> SceneManager.switchTo("home.fxml"));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showError(e.getMessage());
                    loginBtn.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onGoRegister() {
        SceneManager.switchTo("register.fxml");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
