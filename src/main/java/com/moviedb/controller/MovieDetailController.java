package com.moviedb.controller;

import com.moviedb.config.TMDBConfig;
import com.moviedb.model.Cast;
import com.moviedb.model.Movie;
import com.moviedb.model.Review;
import com.moviedb.service.TMDBService;
import com.moviedb.service.UserService;
import com.moviedb.util.ImageCache;
import com.moviedb.util.SceneManager;
import com.moviedb.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MovieDetailController implements Initializable, SceneManager.DataReceiver {

    @FXML private Label     movieTitleLabel;
    @FXML private ImageView posterView;
    @FXML private Label     titleLabel;
    @FXML private Label     metaLabel;
    @FXML private Label     genreLabel;
    @FXML private Label     ratingLabel;
    @FXML private Label     overviewLabel;
    @FXML private Button    favoriteBtn;
    @FXML private Button    watchlistBtn;
    @FXML private Button    trailerBtn;
    @FXML private HBox      castBox;
    @FXML private VBox      reviewsBox;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private TextArea  reviewTextArea;

    private final TMDBService tmdb = new TMDBService();
    private final UserService userService = new UserService();

    private Movie movie;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @Override
    public void receiveData(Object data) {
        int movieId = (Integer) data;
        new Thread(() -> {
            try {
                Movie m = tmdb.getMovieDetail(movieId);
                Platform.runLater(() -> populate(m));
            } catch (Exception e) {
                Platform.runLater(() -> movieTitleLabel.setText("Failed to load movie."));
            }
        }).start();
    }

    private void populate(Movie m) {
        this.movie = m;
        movieTitleLabel.setText(m.getTitle());
        titleLabel.setText(m.getTitle());
        metaLabel.setText(m.getReleaseYear() + "  •  " + m.getFormattedRuntime() + "  •  " + m.getOriginalLanguage().toUpperCase());
        genreLabel.setText(m.getGenres() != null ? String.join(", ", m.getGenres()) : "");
        ratingLabel.setText("⭐ " + m.getRatingFormatted() + " / 10  (" + m.getVoteCount() + " votes)");
        overviewLabel.setText(m.getOverview());

        ImageCache.loadAsync(TMDBConfig.getPosterUrl(m.getPosterPath()), posterView);

        // Cast
        castBox.getChildren().clear();
        if (m.getCast() != null) {
            for (Cast c : m.getCast()) {
                if (c.getJob() != null && !c.getJob().isEmpty()) continue; // skip crew here
                castBox.getChildren().add(buildCastCard(c));
            }
        }

        // Favorite / watchlist state
        if (SessionManager.isLoggedIn()) {
            int uid = SessionManager.getCurrentUser().getId();
            new Thread(() -> {
                try {
                    boolean fav = userService.isFavorite(uid, m.getId());
                    Optional<com.moviedb.model.WatchlistItem> wl = userService.getWatchlistEntry(uid, m.getId());
                    Platform.runLater(() -> {
                        favoriteBtn.setText(fav ? "❤  Unfavorite" : "♡  Favorite");
                        watchlistBtn.setText(wl.isPresent() ? "✓ In Watchlist" : "+ Watchlist");
                    });
                } catch (Exception ignored) {}
            }).start();

            loadReviews();

            // Pre-fill existing review
            new Thread(() -> {
                try {
                    Optional<Review> existing = userService.getUserReviewForMovie(uid, m.getId());
                    existing.ifPresent(r -> Platform.runLater(() -> {
                        ratingSpinner.getValueFactory().setValue(r.getRating());
                        reviewTextArea.setText(r.getReviewText());
                    }));
                } catch (Exception ignored) {}
            }).start();
        }

        trailerBtn.setVisible(m.getTrailerKey() != null && !m.getTrailerKey().isEmpty());
        trailerBtn.setManaged(trailerBtn.isVisible());
    }

    @FXML
    private void onToggleFavorite() {
        if (!SessionManager.isLoggedIn()) return;
        int uid = SessionManager.getCurrentUser().getId();
        new Thread(() -> {
            try {
                userService.toggleFavorite(uid, movie.getId(), movie.getTitle(), movie.getPosterPath());
                boolean fav = userService.isFavorite(uid, movie.getId());
                Platform.runLater(() -> favoriteBtn.setText(fav ? "❤  Unfavorite" : "♡  Favorite"));
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onAddWatchlist() {
        if (!SessionManager.isLoggedIn()) return;
        int uid = SessionManager.getCurrentUser().getId();
        new Thread(() -> {
            try {
                Optional<com.moviedb.model.WatchlistItem> wl = userService.getWatchlistEntry(uid, movie.getId());
                if (wl.isPresent()) {
                    userService.removeFromWatchlist(uid, movie.getId());
                    Platform.runLater(() -> watchlistBtn.setText("+ Watchlist"));
                } else {
                    userService.addToWatchlist(uid, movie.getId(), movie.getTitle(), movie.getPosterPath());
                    Platform.runLater(() -> watchlistBtn.setText("✓ In Watchlist"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onTrailer() {
        if (movie.getTrailerKey() == null || movie.getTrailerKey().isEmpty()) return;
        WebView webView = new WebView();
        webView.getEngine().load("https://www.youtube.com/embed/" + movie.getTrailerKey() + "?autoplay=1");
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Trailer – " + movie.getTitle());
        dialog.setScene(new Scene(webView, 854, 480));
        dialog.setOnCloseRequest(e -> webView.getEngine().load(null));
        dialog.show();
    }

    @FXML
    private void onSubmitReview() {
        if (!SessionManager.isLoggedIn()) return;
        int uid    = SessionManager.getCurrentUser().getId();
        int rating = ratingSpinner.getValue();
        String text = reviewTextArea.getText().trim();

        new Thread(() -> {
            try {
                userService.saveReview(uid, movie.getId(), movie.getTitle(), rating, text);
                Platform.runLater(this::loadReviews);
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onBack() {
        SceneManager.switchTo("home.fxml");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void loadReviews() {
        new Thread(() -> {
            try {
                List<Review> reviews = userService.getReviewsForMovie(movie.getId());
                Platform.runLater(() -> {
                    reviewsBox.getChildren().clear();
                    if (reviews.isEmpty()) {
                        reviewsBox.getChildren().add(new Label("No reviews yet. Be the first!"));
                    } else {
                        for (Review r : reviews) reviewsBox.getChildren().add(buildReviewCard(r));
                    }
                });
            } catch (Exception ignored) {}
        }).start();
    }

    private VBox buildCastCard(Cast c) {
        ImageView img = new ImageView();
        img.setFitWidth(80);
        img.setFitHeight(120);
        img.setPreserveRatio(true);
        ImageCache.loadAsync(TMDBConfig.getProfileUrl(c.getProfilePath()), img);

        Label name = new Label(c.getName());
        name.setMaxWidth(80);
        name.setWrapText(true);
        name.getStyleClass().add("text-small");

        Label role = new Label(c.getCharacter());
        role.setMaxWidth(80);
        role.setWrapText(true);
        role.getStyleClass().add("text-subtle");

        VBox card = new VBox(4, img, name, role);
        card.getStyleClass().add("cast-card");
        return card;
    }

    private VBox buildReviewCard(Review r) {
        Label header = new Label("@" + r.getUsername() + "  ⭐ " + r.getRating() + "/10");
        header.getStyleClass().add("text-bold");

        Label text = new Label(r.getReviewText());
        text.setWrapText(true);

        Label date = new Label(r.getCreatedAt());
        date.getStyleClass().add("text-subtle");

        VBox card = new VBox(4, header, text, date);
        card.getStyleClass().add("card");
        card.setStyle("-fx-padding: 12;");
        return card;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }
}
