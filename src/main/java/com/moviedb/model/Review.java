package com.moviedb.model;

public class Review {
    private int id;
    private int userId;
    private int tmdbMovieId;
    private String movieTitle;
    private String username;
    private int rating;
    private String reviewText;
    private String createdAt;

    public Review() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }

    public int getTmdbMovieId()                         { return tmdbMovieId; }
    public void setTmdbMovieId(int tmdbMovieId)         { this.tmdbMovieId = tmdbMovieId; }

    public String getMovieTitle()                       { return movieTitle; }
    public void setMovieTitle(String movieTitle)        { this.movieTitle = movieTitle; }

    public String getUsername()                         { return username; }
    public void setUsername(String username)            { this.username = username; }

    public int getRating()                              { return rating; }
    public void setRating(int rating)                   { this.rating = rating; }

    public String getReviewText()                       { return reviewText; }
    public void setReviewText(String reviewText)        { this.reviewText = reviewText; }

    public String getCreatedAt()                        { return createdAt; }
    public void setCreatedAt(String createdAt)          { this.createdAt = createdAt; }
}
