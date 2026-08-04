package com.moviedb.dao;

import com.moviedb.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDao {

    public boolean isFavorite(int userId, int tmdbMovieId) throws SQLException {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND tmdb_movie_id = ?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void add(int userId, int tmdbMovieId, String title, String posterPath) throws SQLException {
        String sql = "INSERT IGNORE INTO favorites (user_id, tmdb_movie_id, movie_title, poster_path) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            ps.setString(3, title);
            ps.setString(4, posterPath);
            ps.executeUpdate();
        }
    }

    public void remove(int userId, int tmdbMovieId) throws SQLException {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND tmdb_movie_id = ?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            ps.executeUpdate();
        }
    }

    /** Returns list of tmdb_movie_id values for a user's favorites. */
    public List<Integer> findMovieIdsByUser(int userId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT tmdb_movie_id FROM favorites WHERE user_id = ? ORDER BY added_at DESC";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("tmdb_movie_id"));
            }
        }
        return ids;
    }
}
