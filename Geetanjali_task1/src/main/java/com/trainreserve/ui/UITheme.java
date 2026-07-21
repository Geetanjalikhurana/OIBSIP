package com.trainreserve.ui;

import javax.swing.plaf.FontUIResource;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class UITheme {

    public static final Color PRIMARY = new Color(41, 128, 185); // Blue
    public static final Color SECONDARY = new Color(52, 152, 219); // Light Blue
    public static final Color BACKGROUND = new Color(245, 247, 250); // Light Gray
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80); // Dark Blue/Gray
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141); // Gray
    public static final Color DANGER = new Color(231, 76, 60); // Red

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static void initGlobalDefaults() {
        UIManager.put("Label.font", new FontUIResource(LABEL_FONT));
        UIManager.put("Button.font", new FontUIResource(LABEL_FONT));
        UIManager.put("TextField.font", new FontUIResource(LABEL_FONT));
        UIManager.put("Panel.background", BACKGROUND);
    }
}
