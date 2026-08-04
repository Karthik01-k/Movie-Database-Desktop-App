package com.moviedb.util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageCache {

    private static final Map<String, Image> cache = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final String PLACEHOLDER = "/assets/placeholder.png";

    public static void loadAsync(String url, ImageView imageView) {
        if (url == null || url.isBlank()) {
            setPlaceholder(imageView);
            return;
        }

        if (cache.containsKey(url)) {
            imageView.setImage(cache.get(url));
            return;
        }

        setPlaceholder(imageView);

        executor.submit(() -> {
            try {
                Image image = new Image(url, true);
                cache.put(url, image);
                Platform.runLater(() -> imageView.setImage(image));
            } catch (Exception e) {
                Platform.runLater(() -> setPlaceholder(imageView));
            }
        });
    }

    private static void setPlaceholder(ImageView imageView) {
        try {
            Image placeholder = new Image(
                Objects.requireNonNull(ImageCache.class.getResourceAsStream(PLACEHOLDER))
            );
            imageView.setImage(placeholder);
        } catch (Exception ignored) {}
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
