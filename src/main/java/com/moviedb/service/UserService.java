package com.moviedb.service;

import com.moviedb.dao.FavoriteDao;
import com.moviedb.dao.ReviewDao;
import com.moviedb.dao.WatchlistDao;
import com.moviedb.model.Review;
import com.moviedb.model.WatchlistItem;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final WatchlistDao watchlistDao = new WatchlistDao();
    private final FavoriteDao favoriteDao = new FavoriteDao();
    private final ReviewDao reviewDao = new ReviewDao();

    // ── Watchlist ─────────────────────────────────────────────────────────────

    public List<WatchlistItem> getWatchlist(int userId) throws Exception {
        return watchlistDao.findByUser(userId);
    }

    public void addToWatchlist(int userId, int movieId, String title, String posterPath) throws Exception {
        watchlistDao.add(userId, movieId, title, posterPath);
    }

    public void removeFromWatchlist(int userId, int movieId) throws Exception {
        watchlistDao.remove(userId, movieId);
    }

    public void updateWatchlistStatus(int userId, int movieId, String status) throws Exception {
        watchlistDao.updateStatus(userId, movieId, status);
    }

    public Optional<WatchlistItem> getWatchlistEntry(int userId, int movieId) throws Exception {
        return watchlistDao.findByUserAndMovie(userId, movieId);
    }

    // ── Favorites ─────────────────────────────────────────────────────────────

    public boolean isFavorite(int userId, int movieId) throws Exception {
        return favoriteDao.isFavorite(userId, movieId);
    }

    public void toggleFavorite(int userId, int movieId, String title, String posterPath) throws Exception {
        if (favoriteDao.isFavorite(userId, movieId)) {
            favoriteDao.remove(userId, movieId);
        } else {
            favoriteDao.add(userId, movieId, title, posterPath);
        }
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    public List<Review> getReviewsForMovie(int movieId) throws Exception {
        return reviewDao.findByMovie(movieId);
    }

    public List<Review> getReviewsByUser(int userId) throws Exception {
        return reviewDao.findByUser(userId);
    }

    public Optional<Review> getUserReviewForMovie(int userId, int movieId) throws Exception {
        return reviewDao.findByUserAndMovie(userId, movieId);
    }

    public void saveReview(int userId, int movieId, String title, int rating, String text) throws Exception {
        if (rating < 1 || rating > 10) throw new Exception("Rating must be between 1 and 10.");
        reviewDao.save(userId, movieId, title, rating, text);
    }

    public void deleteReview(int reviewId) throws Exception {
        reviewDao.delete(reviewId);
    }
}
