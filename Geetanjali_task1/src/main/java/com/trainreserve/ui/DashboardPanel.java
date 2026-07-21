package com.trainreserve.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainFrame mainFrame) {
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BACKGROUND);

        JLabel welcome = new JLabel("Welcome, " + mainFrame.getCurrentUser().getUsername() + "!");
        welcome.setFont(UITheme.TITLE_FONT.deriveFont(32f));
        welcome.setForeground(UITheme.TEXT_PRIMARY);
        
        JLabel subtitle = new JLabel("What would you like to do today?");
        subtitle.setFont(UITheme.LABEL_FONT.deriveFont(18f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        content.add(welcome);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 50)));

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        cardsPanel.setBackground(UITheme.BACKGROUND);
        
        cardsPanel.add(createActionCard("Book a Ticket", "Reserve a seat on an Indian Railway train.", () -> mainFrame.showView("RESERVE")));
        cardsPanel.add(createActionCard("Cancel Ticket", "Cancel an existing booking using your PNR.", () -> mainFrame.showView("CANCEL")));

        content.add(cardsPanel);
        
        add(content, BorderLayout.NORTH);
    }

    private JPanel createActionCard(String title, String desc, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 180));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD, 20f));
        titleLbl.setForeground(UITheme.PRIMARY);

        JTextArea descLbl = new JTextArea(desc);
        descLbl.setFont(UITheme.LABEL_FONT);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);
        descLbl.setWrapStyleWord(true);
        descLbl.setLineWrap(true);
        descLbl.setOpaque(false);
        descLbl.setEditable(false);
        descLbl.setFocusable(false);

        card.add(titleLbl);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(descLbl);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(245, 248, 255));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }
}
