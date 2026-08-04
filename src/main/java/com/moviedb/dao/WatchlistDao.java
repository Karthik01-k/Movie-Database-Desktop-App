package com.moviedb.dao;

import com.moviedb.config.DatabaseConfig;
import com.moviedb.model.WatchlistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WatchlistDao {

    public List<WatchlistItem> findByUser(int userId) throws SQLException {
        List<WatchlistItem> list = new ArrayList<>();
        String sql = "SELECT * FROM watchlist WHERE user_id = ? ORDER BY added_at DESC";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Optional<WatchlistItem> findByUserAndMovie(int userId, int tmdbMovieId) throws SQLException {
        String sql = "SELECT * FROM watchlist WHERE user_id = ? AND tmdb_movie_id = ?";
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

    public void add(int userId, int tmdbMovieId, String title, String posterPath) throws SQLException {
        String sql = """
                INSERT IGNORE INTO watchlist (user_id, tmdb_movie_id, movie_title, poster_path)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            ps.setString(3, title);
            ps.setString(4, posterPath);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int userId, int tmdbMovieId, String status) throws SQLException {
        String sql = "UPDATE watchlist SET status = ? WHERE user_id = ? AND tmdb_movie_id = ?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.setInt(3, tmdbMovieId);
            ps.executeUpdate();
        }
    }

    public void remove(int userId, int tmdbMovieId) throws SQLException {
        String sql = "DELETE FROM watchlist WHERE user_id = ? AND tmdb_movie_id = ?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tmdbMovieId);
            ps.executeUpdate();
        }
    }

    private WatchlistItem map(ResultSet rs) throws SQLException {
        WatchlistItem w = new WatchlistItem();
        w.setId(rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));
        w.setTmdbMovieId(rs.getInt("tmdb_movie_id"));
        w.setMovieTitle(rs.getString("movie_title"));
        w.setPosterPath(rs.getString("poster_path"));
        w.setStatus(rs.getString("status"));
        w.setAddedAt(rs.getString("added_at"));
        return w;
    }
}
