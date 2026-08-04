package com.moviedb.model;

import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double voteAverage;
    private int voteCount;
    private int runtime;
    private String originalLanguage;
    private List<String> genres;
    private List<Cast> cast;
    private String trailerKey;   // YouTube video key

    public Movie() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    public String getOverview()                 { return overview; }
    public void setOverview(String overview)    { this.overview = overview; }

    public String getPosterPath()               { return posterPath; }
    public void setPosterPath(String posterPath){ this.posterPath = posterPath; }

    public String getBackdropPath()                     { return backdropPath; }
    public void setBackdropPath(String backdropPath)    { this.backdropPath = backdropPath; }

    public String getReleaseDate()                      { return releaseDate; }
    public void setReleaseDate(String releaseDate)      { this.releaseDate = releaseDate; }

    public double getVoteAverage()                      { return voteAverage; }
    public void setVoteAverage(double voteAverage)      { this.voteAverage = voteAverage; }

    public int getVoteCount()                           { return voteCount; }
    public void setVoteCount(int voteCount)             { this.voteCount = voteCount; }

    public int getRuntime()                             { return runtime; }
    public void setRuntime(int runtime)                 { this.runtime = runtime; }

    public String getOriginalLanguage()                         { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage)    { this.originalLanguage = originalLanguage; }

    public List<String> getGenres()                     { return genres; }
    public void setGenres(List<String> genres)          { this.genres = genres; }

    public List<Cast> getCast()                         { return cast; }
    public void setCast(List<Cast> cast)                { this.cast = cast; }

    public String getTrailerKey()                       { return trailerKey; }
    public void setTrailerKey(String trailerKey)        { this.trailerKey = trailerKey; }

    public String getFormattedRuntime() {
        if (runtime <= 0) return "N/A";
        return (runtime / 60) + "h " + (runtime % 60) + "m";
    }

    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) return "N/A";
        return releaseDate.substring(0, 4);
    }

    public String getRatingFormatted() {
        return String.format("%.1f", voteAverage);
    }
}
