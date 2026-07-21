package com.trainreserve.db;

import java.util.Random;

public class PNRGenerator {
    private static final Random RANDOM = new Random();

    public static String generatePNR() {
        // Generates a 10-digit random string for PNR
        long pnr = (long)(Math.random() * 1_000_000_0000L);
        return String.format("%010d", pnr);
    }
}
