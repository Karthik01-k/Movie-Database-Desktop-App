-- ============================================================
--  Movie Database App - MySQL Schema
--  Run this in MySQL Workbench before starting the app
-- ============================================================

CREATE DATABASE IF NOT EXISTS movie_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE movie_db;

-- ------------------------------------------------------------
-- Users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  UNIQUE NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('USER','ADMIN') DEFAULT 'USER',
    profile_pic   VARCHAR(255) DEFAULT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Favorites
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS favorites (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    tmdb_movie_id  INT NOT NULL,
    movie_title    VARCHAR(255),
    poster_path    VARCHAR(255),
    added_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uq_fav (user_id, tmdb_movie_id)
);

-- ------------------------------------------------------------
-- Watchlist
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS watchlist (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    tmdb_movie_id  INT NOT NULL,
    movie_title    VARCHAR(255),
    poster_path    VARCHAR(255),
    status         ENUM('WANT_TO_WATCH','WATCHING','COMPLETED') DEFAULT 'WANT_TO_WATCH',
    added_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uq_watch (user_id, tmdb_movie_id)
);

-- ------------------------------------------------------------
-- Reviews
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    tmdb_movie_id  INT NOT NULL,
    movie_title    VARCHAR(255),
    rating         TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 10),
    review_text    TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uq_review (user_id, tmdb_movie_id)
);

-- ------------------------------------------------------------
-- Search History
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS search_history (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    query       VARCHAR(255) NOT NULL,
    searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Default Admin User
-- password = "admin123" (BCrypt hashed)
-- ------------------------------------------------------------
INSERT IGNORE INTO users (username, email, password_hash, role)
VALUES (
    'admin',
    'admin@moviedb.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);
