package com.moviedb.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TMDBConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = TMDBConfig.class
                .getClassLoader()
                .getResourceAsStream("tmdb.properties")) {
            if (in == null) throw new RuntimeException("tmdb.properties not found in resources");
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TMDB config", e);
        }
    }

    public static String getApiKey()         { return props.getProperty("tmdb.api.key"); }
    public static String getBaseUrl()        { return props.getProperty("tmdb.base.url"); }
    public static String getImageBaseUrl()   { return props.getProperty("tmdb.image.base.url"); }
    public static String getPosterSize()     { return props.getProperty("tmdb.poster.size"); }
    public static String getBackdropSize()   { return props.getProperty("tmdb.backdrop.size"); }
    public static String getProfileSize()    { return props.getProperty("tmdb.profile.size"); }

    public static String getPosterUrl(String posterPath) {
        if (posterPath == null || posterPath.isBlank()) return null;
        return getImageBaseUrl() + getPosterSize() + posterPath;
    }

    public static String getBackdropUrl(String backdropPath) {
        if (backdropPath == null || backdropPath.isBlank()) return null;
        return getImageBaseUrl() + getBackdropSize() + backdropPath;
    }

    public static String getProfileUrl(String profilePath) {
        if (profilePath == null || profilePath.isBlank()) return null;
        return getImageBaseUrl() + getProfileSize() + profilePath;
    }
}
