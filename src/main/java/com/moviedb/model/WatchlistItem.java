package com.moviedb.model;

public class WatchlistItem {
    private int id;
    private int userId;
    private int tmdbMovieId;
    private String movieTitle;
    private String posterPath;
    private String status;  // WANT_TO_WATCH | WATCHING | COMPLETED
    private String addedAt;

    public WatchlistItem() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }

    public int getTmdbMovieId()                         { return tmdbMovieId; }
    public void setTmdbMovieId(int tmdbMovieId)         { this.tmdbMovieId = tmdbMovieId; }

    public String getMovieTitle()                       { return movieTitle; }
    public void setMovieTitle(String movieTitle)        { this.movieTitle = movieTitle; }

    public String getPosterPath()                       { return posterPath; }
    public void setPosterPath(String posterPath)        { this.posterPath = posterPath; }

    public String getStatus()                           { return status; }
    public void setStatus(String status)                { this.status = status; }

    public String getAddedAt()                          { return addedAt; }
    public void setAddedAt(String addedAt)              { this.addedAt = addedAt; }
}
