package com.trainreserve.util;

import com.trainreserve.db.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Generates unique PNR numbers.
 * Format: PNR + yyyyMMddHHmmss + 4 random digits
 * Example: PNR202407150945381234
 */
public class PNRGenerator {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * Generates a unique PNR not already present in the database.
     */
    public static String generate() throws SQLException {
        DatabaseManager db = DatabaseManager.getInstance();
        String pnr;
        do {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            int suffix = 1000 + RANDOM.nextInt(9000); // 4-digit random number
            pnr = "PNR" + timestamp + suffix;
        } while (db.pnrExists(pnr));
        return pnr;
    }
}
