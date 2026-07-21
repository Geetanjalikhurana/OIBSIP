package com.trainreserve.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Input validation utility methods used across all forms.
 */
public class Validator {

    /**
     * Returns true if the string is non-null and non-blank after trimming.
     */
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Returns true if the string contains only digits (no spaces or signs).
     */
    public static boolean isNumeric(String value) {
        return value != null && value.trim().matches("\\d+");
    }

    /**
     * Returns true if the string is a valid ISO date (yyyy-MM-dd)
     * AND the date is today or in the future.
     */
    public static boolean isValidFutureDate(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            LocalDate date = LocalDate.parse(value.trim());
            return !date.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if source and destination are different (case-insensitive).
     */
    public static boolean isDifferentStation(String source, String dest) {
        if (source == null || dest == null) return false;
        return !source.trim().equalsIgnoreCase(dest.trim());
    }
}
