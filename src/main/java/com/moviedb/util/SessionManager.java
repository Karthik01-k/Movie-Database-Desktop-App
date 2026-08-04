package com.moviedb.util;

import com.moviedb.model.User;

public class SessionManager {

    private static User currentUser;

    private SessionManager() {}

    public static void login(User user)     { currentUser = user; }
    public static void logout()             { currentUser = null; }
    public static User getCurrentUser()     { return currentUser; }
    public static boolean isLoggedIn()      { return currentUser != null; }
    public static boolean isAdmin()         { return isLoggedIn() && currentUser.isAdmin(); }
}
