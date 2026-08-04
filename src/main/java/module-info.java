module com.moviedb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires atlantafx.base;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;

    requires java.sql;
    requires java.net.http;
    requires com.zaxxer.hikari;
    requires com.google.gson;
    requires org.mindrot.jbcrypt;

    opens com.moviedb to javafx.fxml;
    opens com.moviedb.controller to javafx.fxml;
    opens com.moviedb.model to com.google.gson;
    opens com.moviedb.service to javafx.fxml;
    opens com.moviedb.dao to javafx.fxml;
}
