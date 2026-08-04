package com.moviedb.controller;

import com.moviedb.model.Review;
import com.moviedb.model.User;
import com.moviedb.model.WatchlistItem;
import com.moviedb.service.UserService;
import com.moviedb.util.SceneManager;
import com.moviedb.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private VBox  watchlistBox;
    @FXML private VBox  reviewsBox;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = SessionManager.getCurrentUser();
        if (user == null) { SceneManager.switchTo("login.fxml"); return; }

        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        roleLabel.setText("Role: " + user.getRole());

        loadWatchlist(user.getId());
        loadReviews(user.getId());
    }

    @FXML
    private void onBack() {
        SceneManager.switchTo("home.fxml");
    }

    // ── Loaders ───────────────────────────────────────────────────────────────

    private void loadWatchlist(int userId) {
        new Thread(() -> {
            try {
                List<WatchlistItem> items = userService.getWatchlist(userId);
                Platform.runLater(() -> {
                    watchlistBox.getChildren().clear();
                    if (items.isEmpty()) {
                        watchlistBox.getChildren().add(new Label("Your watchlist is empty."));
                        return;
                    }
                    for (WatchlistItem item : items) {
                        watchlistBox.getChildren().add(buildWatchlistRow(item, userId));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> watchlistBox.getChildren().add(new Label("Failed to load watchlist.")));
            }
        }).start();
    }

    private void loadReviews(int userId) {
        new Thread(() -> {
            try {
                List<Review> reviews = userService.getReviewsByUser(userId);
                Platform.runLater(() -> {
                    reviewsBox.getChildren().clear();
                    if (reviews.isEmpty()) {
                        reviewsBox.getChildren().add(new Label("You haven't written any reviews yet."));
                        return;
                    }
                    for (Review r : reviews) reviewsBox.getChildren().add(buildReviewRow(r, userId));
                });
            } catch (Exception e) {
                Platform.runLater(() -> reviewsBox.getChildren().add(new Label("Failed to load reviews.")));
            }
        }).start();
    }

    // ── Row builders ──────────────────────────────────────────────────────────

    private VBox buildWatchlistRow(WatchlistItem item, int userId) {
        Label title  = new Label(item.getMovieTitle());
        title.getStyleClass().add("text-bold");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("WANT_TO_WATCH", "WATCHING", "COMPLETED");
        statusBox.setValue(item.getStatus());
        statusBox.setOnAction(e -> {
            new Thread(() -> {
                try { userService.updateWatchlistStatus(userId, item.getTmdbMovieId(), statusBox.getValue()); }
                catch (Exception ignored) {}
            }).start();
        });

        Button remove = new Button("Remove");
        remove.setOnAction(e -> {
            new Thread(() -> {
                try {
                    userService.removeFromWatchlist(userId, item.getTmdbMovieId());
                    Platform.runLater(() -> loadWatchlist(userId));
                } catch (Exception ignored) {}
            }).start();
        });

        Button view = new Button("View");
        view.setOnAction(e -> SceneManager.switchTo("movie_detail.fxml", item.getTmdbMovieId()));

        javafx.scene.layout.HBox actions = new javafx.scene.layout.HBox(8, statusBox, view, remove);
        VBox row = new VBox(4, title, actions);
        row.getStyleClass().add("card");
        row.setStyle("-fx-padding: 12;");
        return row;
    }

    private VBox buildReviewRow(Review r, int userId) {
        Label title  = new Label(r.getMovieTitle());
        title.getStyleClass().add("text-bold");

        Label rating = new Label("⭐ " + r.getRating() + "/10");
        Label text   = new Label(r.getReviewText());
        text.setWrapText(true);

        Button delete = new Button("Delete");
        delete.setOnAction(e -> {
            new Thread(() -> {
                try {
                    userService.deleteReview(r.getId());
                    Platform.runLater(() -> loadReviews(userId));
                } catch (Exception ignored) {}
            }).start();
        });

        Button view = new Button("View Movie");
        view.setOnAction(e -> SceneManager.switchTo("movie_detail.fxml", r.getTmdbMovieId()));

        javafx.scene.layout.HBox actions = new javafx.scene.layout.HBox(8, view, delete);
        VBox row = new VBox(4, title, rating, text, actions);
        row.getStyleClass().add("card");
        row.setStyle("-fx-padding: 12;");
        return row;
    }
}
