package com.moviedb.service;

import com.google.gson.*;
import com.moviedb.config.TMDBConfig;
import com.moviedb.model.Cast;
import com.moviedb.model.Movie;

import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

public class TMDBService {

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    // ── Search ────────────────────────────────────────────────────────────────

    public List<Movie> searchMovies(String query) throws Exception {
        String url = TMDBConfig.getBaseUrl() + "/search/movie?api_key=" + TMDBConfig.getApiKey()
                + "&query=" + java.net.URLEncoder.encode(query, "UTF-8") + "&page=1";
        JsonObject json = get(url);
        return parseMovieList(json.getAsJsonArray("results"));
    }

    // ── Discover / lists ──────────────────────────────────────────────────────

    public List<Movie> getTrending() throws Exception {
        JsonObject json = get(TMDBConfig.getBaseUrl() + "/trending/movie/week?api_key=" + TMDBConfig.getApiKey());
        return parseMovieList(json.getAsJsonArray("results"));
    }

    public List<Movie> getNowPlaying() throws Exception {
        JsonObject json = get(TMDBConfig.getBaseUrl() + "/movie/now_playing?api_key=" + TMDBConfig.getApiKey());
        return parseMovieList(json.getAsJsonArray("results"));
    }

    public List<Movie> getTopRated() throws Exception {
        JsonObject json = get(TMDBConfig.getBaseUrl() + "/movie/top_rated?api_key=" + TMDBConfig.getApiKey());
        return parseMovieList(json.getAsJsonArray("results"));
    }

    public List<Movie> getUpcoming() throws Exception {
        JsonObject json = get(TMDBConfig.getBaseUrl() + "/movie/upcoming?api_key=" + TMDBConfig.getApiKey());
        return parseMovieList(json.getAsJsonArray("results"));
    }

    // ── Detail ────────────────────────────────────────────────────────────────

    public Movie getMovieDetail(int movieId) throws Exception {
        String url = TMDBConfig.getBaseUrl() + "/movie/" + movieId
                + "?api_key=" + TMDBConfig.getApiKey()
                + "&append_to_response=credits,videos";
        JsonObject json = get(url);
        return parseDetail(json);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JsonObject get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(res.body(), JsonObject.class);
    }

    private List<Movie> parseMovieList(JsonArray results) {
        List<Movie> movies = new ArrayList<>();
        if (results == null) return movies;
        for (JsonElement el : results) {
            JsonObject o = el.getAsJsonObject();
            Movie m = new Movie();
            m.setId(o.get("id").getAsInt());
            m.setTitle(getString(o, "title"));
            m.setOverview(getString(o, "overview"));
            m.setPosterPath(getString(o, "poster_path"));
            m.setBackdropPath(getString(o, "backdrop_path"));
            m.setReleaseDate(getString(o, "release_date"));
            m.setVoteAverage(getDouble(o, "vote_average"));
            m.setVoteCount(getInt(o, "vote_count"));
            movies.add(m);
        }
        return movies;
    }

    private Movie parseDetail(JsonObject o) {
        Movie m = new Movie();
        m.setId(o.get("id").getAsInt());
        m.setTitle(getString(o, "title"));
        m.setOverview(getString(o, "overview"));
        m.setPosterPath(getString(o, "poster_path"));
        m.setBackdropPath(getString(o, "backdrop_path"));
        m.setReleaseDate(getString(o, "release_date"));
        m.setVoteAverage(getDouble(o, "vote_average"));
        m.setVoteCount(getInt(o, "vote_count"));
        m.setRuntime(getInt(o, "runtime"));
        m.setOriginalLanguage(getString(o, "original_language"));

        // Genres
        List<String> genres = new ArrayList<>();
        if (o.has("genres")) {
            for (JsonElement g : o.getAsJsonArray("genres"))
                genres.add(g.getAsJsonObject().get("name").getAsString());
        }
        m.setGenres(genres);

        // Cast & crew
        List<Cast> castList = new ArrayList<>();
        if (o.has("credits")) {
            JsonObject credits = o.getAsJsonObject("credits");
            if (credits.has("cast")) {
                int count = 0;
                for (JsonElement el : credits.getAsJsonArray("cast")) {
                    if (count++ >= 10) break;
                    JsonObject co = el.getAsJsonObject();
                    Cast cast = new Cast();
                    cast.setId(co.get("id").getAsInt());
                    cast.setName(getString(co, "name"));
                    cast.setCharacter(getString(co, "character"));
                    cast.setProfilePath(getString(co, "profile_path"));
                    castList.add(cast);
                }
            }
            if (credits.has("crew")) {
                for (JsonElement el : credits.getAsJsonArray("crew")) {
                    JsonObject co = el.getAsJsonObject();
                    if ("Director".equals(getString(co, "job"))) {
                        Cast crew = new Cast();
                        crew.setId(co.get("id").getAsInt());
                        crew.setName(getString(co, "name"));
                        crew.setJob("Director");
                        crew.setDepartment("Directing");
                        castList.add(crew);
                        break;
                    }
                }
            }
        }
        m.setCast(castList);

        // Trailer
        if (o.has("videos")) {
            JsonArray videos = o.getAsJsonObject("videos").getAsJsonArray("results");
            for (JsonElement el : videos) {
                JsonObject v = el.getAsJsonObject();
                if ("YouTube".equals(getString(v, "site")) && "Trailer".equals(getString(v, "type"))) {
                    m.setTrailerKey(getString(v, "key"));
                    break;
                }
            }
        }

        return m;
    }

    private String getString(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : "";
    }

    private double getDouble(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsDouble() : 0.0;
    }

    private int getInt(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsInt() : 0;
    }
}
