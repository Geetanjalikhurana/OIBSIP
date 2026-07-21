package com.trainreserve;

import com.trainreserve.db.DatabaseManager;
import com.trainreserve.ui.MainFrame;
import com.trainreserve.ui.UITheme;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Apply modern Look and Feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        UITheme.initGlobalDefaults();

        // Initialize Database
        try {
            DatabaseManager.getInstance();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not connect to database.", "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
