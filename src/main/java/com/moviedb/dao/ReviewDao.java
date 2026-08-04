package com.moviedb.dao;

import com.moviedb.config.DatabaseConfig;
import com.moviedb.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewDao {

    public List<Review> findByMovie(int tmdbMovieId) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = """
                SELECT r.id, r.user_id, r.tmdb_movie_id, r.movie_title,
                       u.username, r.rating, r.review_text, r.created_at
                FROM reviews r JOIN users u ON r.user_id = u.id
                WHERE r.tmdb_movie_id = ?
                ORDER BY r.created_at DESC
                """;
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tmdbMovieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Review> findByUser(int userId) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = """
                SELECT r.id, r.user_id, r.tmdb_movie_id, r.movie_title,
                       u.username, r.rating, r.review_text, r.created_at
                FROM reviews r JOIN users u ON r.user_id = u.id
                WHERE r.user_id = ?
                ORDER BY r.created_at DESC
                """;
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Optional<Review> findByUserAndMovie(int userId, int tmdbMovieId) throws SQLException {
        String sql = """
                SELECT r.id, r.user_id, r.tmdb_movie_id, r.movie_title,
                       u.username, r.rating, r.review_text, r.created_at
                FROM reviews r JOIN users u ON r.user_id = u.id
                WHERE r.user_id = ? AND r.tmdb_movie_id = ?
                """;
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    public void save(int userId, int tmdbMovieId, String movieTitle, int rating, String reviewText) throws SQLException {
        String sql = """
                INSERT INTO reviews (user_id, tmdb_movie_id, movie_title, rating, review_text)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE rating = VALUES(rating), review_text = VALUES(review_text)
                """;
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            ps.setString(3, movieTitle);
            ps.setInt(4, rating);
            ps.setString(5, reviewText);
            ps.executeUpdate();
        }
    }

    public void delete(int reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.executeUpdate();
        }
    }

    private Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setTmdbMovieId(rs.getInt("tmdb_movie_id"));
        r.setMovieTitle(rs.getString("movie_title"));
        r.setUsername(rs.getString("username"));
        r.setRating(rs.getInt("rating"));
        r.setReviewText(rs.getString("review_text"));
        r.setCreatedAt(rs.getString("created_at"));
        return r;
    }
}
