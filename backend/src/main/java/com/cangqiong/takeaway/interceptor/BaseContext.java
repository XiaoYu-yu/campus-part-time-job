package com.cangqiong.takeaway.interceptor;

public class BaseContext {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserType = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    public static void setCurrentUserType(String userType) {
        currentUserType.set(userType);
    }

    public static String getCurrentUserType() {
        return currentUserType.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentUserType.remove();
    }
}
