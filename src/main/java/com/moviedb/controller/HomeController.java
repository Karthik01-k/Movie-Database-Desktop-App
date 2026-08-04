package com.moviedb.controller;

import com.moviedb.model.Movie;
import com.moviedb.service.AuthService;
import com.moviedb.service.TMDBService;
import com.moviedb.util.ImageCache;
import com.moviedb.util.SceneManager;
import com.moviedb.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.moviedb.config.TMDBConfig.getPosterUrl;

public class HomeController implements Initializable {

    @FXML private FlowPane movieGrid;
    @FXML private Label    sectionTitle;
    @FXML private TextField searchField;
    @FXML private Button   adminBtn;

    private final TMDBService tmdb = new TMDBService();
    private final AuthService auth = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SessionManager.isAdmin()) {
            adminBtn.setVisible(true);
            adminBtn.setManaged(true);
        }
        loadMovies("Trending This Week", () -> tmdb.getTrending());
    }

    @FXML private void onTrending()   { loadMovies("Trending This Week",  () -> tmdb.getTrending()); }
    @FXML private void onNowPlaying() { loadMovies("Now Playing",          () -> tmdb.getNowPlaying()); }
    @FXML private void onTopRated()   { loadMovies("Top Rated",            () -> tmdb.getTopRated()); }
    @FXML private void onUpcoming()   { loadMovies("Upcoming",             () -> tmdb.getUpcoming()); }

    @FXML
    private void onSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) return;
        loadMovies("Results for: " + q, () -> tmdb.searchMovies(q));
    }

    @FXML private void onWatchlist() { SceneManager.switchTo("profile.fxml"); }
    @FXML private void onProfile()   { SceneManager.switchTo("profile.fxml"); }
    @FXML private void onAdmin()     { SceneManager.switchTo("admin.fxml"); }

    @FXML
    private void onLogout() {
        auth.logout();
        SceneManager.switchTo("login.fxml");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface MovieLoader { List<Movie> load() throws Exception; }

    private void loadMovies(String title, MovieLoader loader) {
        sectionTitle.setText(title);
        movieGrid.getChildren().clear();
        movieGrid.getChildren().add(new Label("Loading…"));

        new Thread(() -> {
            try {
                List<Movie> movies = loader.load();
                Platform.runLater(() -> {
                    movieGrid.getChildren().clear();
                    for (Movie m : movies) movieGrid.getChildren().add(buildCard(m));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    movieGrid.getChildren().clear();
                    movieGrid.getChildren().add(new Label("Error: " + e.getMessage()));
                });
            }
        }).start();
    }

    private VBox buildCard(Movie movie) {
        ImageView poster = new ImageView();
        poster.setFitWidth(150);
        poster.setFitHeight(225);
        poster.setPreserveRatio(true);
        ImageCache.loadAsync(getPosterUrl(movie.getPosterPath()), poster);

        Label title = new Label(movie.getTitle());
        title.setMaxWidth(150);
        title.setWrapText(true);
        title.getStyleClass().add("text-small");

        Label rating = new Label("⭐ " + movie.getRatingFormatted());
        rating.getStyleClass().add("text-subtle");

        VBox card = new VBox(4, poster, title, rating);
        card.getStyleClass().add("movie-card");
        card.setOnMouseClicked(e -> SceneManager.switchTo("movie_detail.fxml", movie.getId()));
        return card;
    }
}
