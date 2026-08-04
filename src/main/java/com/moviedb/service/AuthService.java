package com.moviedb.service;

import com.moviedb.dao.UserDao;
import com.moviedb.model.User;
import com.moviedb.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final UserDao userDao = new UserDao();

    /** Returns the logged-in user or throws on bad credentials. */
    public User login(String username, String password) throws Exception {
        Optional<String> hash = userDao.getPasswordHash(username);
        if (hash.isEmpty() || !BCrypt.checkpw(password, hash.get())) {
            throw new Exception("Invalid username or password.");
        }
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found."));
        SessionManager.login(user);
        return user;
    }

    /** Registers a new user. Throws if username/email already taken. */
    public void register(String username, String email, String password) throws Exception {
        if (username.isBlank() || email.isBlank() || password.isBlank())
            throw new Exception("All fields are required.");
        if (password.length() < 6)
            throw new Exception("Password must be at least 6 characters.");
        if (userDao.findByUsername(username).isPresent())
            throw new Exception("Username already taken.");
        if (userDao.findByEmail(email).isPresent())
            throw new Exception("Email already registered.");

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            userDao.create(username, email, hash);
        } catch (SQLException e) {
            throw new Exception("Registration failed: " + e.getMessage());
        }
    }

    public void logout() {
        SessionManager.logout();
    }
}
