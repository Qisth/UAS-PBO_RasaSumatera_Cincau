package com.example.util;

public class SessionManager {
    private static String token;
    private static String username;

    public static void setSession(String jwtToken, String user) {
        token = jwtToken;
        username = user;
    }

    public static String getToken() { return token; }
    public static String getUsername() { return username; }

    public static void clearSession() {
        token = null;
        username = null;
    }
}