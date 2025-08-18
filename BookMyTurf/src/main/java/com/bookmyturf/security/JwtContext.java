package com.bookmyturf.security;

public class JwtContext {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>();

    public static void setUserContext(Long userId, String role) {
        userIdHolder.set(userId);
        roleHolder.set(role);
    }

    public static Long getUserId() {
        return userIdHolder.get();
    }

    public static String getUserRole() {
        return roleHolder.get();
    }

    public static void clear() {
        userIdHolder.remove();
        roleHolder.remove();
    }
}
