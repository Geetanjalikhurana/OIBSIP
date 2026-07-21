package com.trainreserve.ui;

import com.trainreserve.db.DatabaseManager;
import com.trainreserve.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField userField;
    private JPasswordField passField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BACKGROUND);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login to Railway Reservation System");
        subtitle.setFont(UITheme.LABEL_FONT);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        userField = new JTextField();
        userField.setMaximumSize(new Dimension(300, 40));
        userField.setFont(UITheme.LABEL_FONT);

        passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(300, 40));
        passField.setFont(UITheme.LABEL_FONT);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(UITheme.PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        loginBtn.setMaximumSize(new Dimension(300, 45));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginBtn.addActionListener(e -> attemptLogin());

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(UITheme.LABEL_FONT);
        card.add(uLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(userField);
        
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(UITheme.LABEL_FONT);
        card.add(pLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(passField);

        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(loginBtn);

        add(card);
    }

    private void attemptLogin() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        try {
            User user = DatabaseManager.getInstance().authenticate(u, p);
            if (user != null) {
                userField.setText("");
                passField.setText("");
                mainFrame.onLoginSuccess(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
