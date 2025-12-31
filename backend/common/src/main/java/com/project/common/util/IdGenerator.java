package com.project.common.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility class for ID generation
 */
public class IdGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private IdGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generate a UUID-based ID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a prefixed ID (e.g., user-123abc)
     */
    public static String generatePrefixedId(String prefix) {
        return prefix + "-" + generateShortId(8);
    }

    /**
     * Generate a short alphanumeric ID
     */
    public static String generateShortId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
