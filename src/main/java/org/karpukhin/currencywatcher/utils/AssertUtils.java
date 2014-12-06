package org.karpukhin.currencywatcher.utils;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
public class AssertUtils {

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
