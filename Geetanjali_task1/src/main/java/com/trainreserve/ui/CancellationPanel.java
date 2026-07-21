package com.trainreserve.ui;

import com.trainreserve.db.DatabaseManager;
import com.trainreserve.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CancellationPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField pnrField;
    private JTextArea detailsArea;
    private JButton cancelBtn;

    public CancellationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("Cancel Ticket");
        title.setFont(UITheme.TITLE_FONT.deriveFont(26f));
        title.setForeground(UITheme.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel pnrLabel = new JLabel("Enter PNR Number:");
        pnrLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        
        pnrField = new JTextField(15);
        pnrField.setFont(UITheme.LABEL_FONT);
        
        JButton fetchBtn = new JButton("Fetch Details");
        fetchBtn.setBackground(UITheme.SECONDARY);
        fetchBtn.setForeground(Color.WHITE);
        fetchBtn.setFocusPainted(false);
        fetchBtn.setFont(UITheme.LABEL_FONT);
        fetchBtn.addActionListener(e -> fetchDetails());
        
        searchPanel.add(pnrLabel);
        searchPanel.add(pnrField);
        searchPanel.add(fetchBtn);
        
        content.add(searchPanel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        detailsArea = new JTextArea(10, 40);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setEditable(false);
        detailsArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        content.add(new JScrollPane(detailsArea));
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        cancelBtn = new JButton("Confirm Cancellation");
        cancelBtn.setBackground(UITheme.DANGER);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setEnabled(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelBtn.addActionListener(e -> confirmCancel());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(cancelBtn);
        
        content.add(btnPanel);

        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    private void fetchDetails() {
        String pnr = pnrField.getText().trim();
        if (pnr.isEmpty()) return;
        
        try {
            Reservation res = DatabaseManager.getInstance().getReservationByPNR(pnr);
            if (res != null) {
                String details = 
                    "PNR: " + res.getPnr() + "\n" +
                    "Passenger: " + res.getPassengerName() + "\n" +
                    "Train: " + res.getTrainNumber() + " - " + res.getTrainName() + "\n" +
                    "Class: " + res.getClassType() + "\n" +
                    "Date of Journey: " + res.getDateOfJourney() + "\n" +
                    "Source: " + res.getSourceStation() + "\n" +
                    "Destination: " + res.getDestStation() + "\n" +
                    "Booked At: " + res.getBookedAt();
                detailsArea.setText(details);
                cancelBtn.setEnabled(true);
            } else {
                detailsArea.setText("No reservation found for PNR: " + pnr);
                cancelBtn.setEnabled(false);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmCancel() {
        String pnr = pnrField.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel booking for PNR " + pnr + "?",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = DatabaseManager.getInstance().cancelReservation(pnr);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Ticket Cancelled Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    pnrField.setText("");
                    detailsArea.setText("");
                    cancelBtn.setEnabled(false);
                    mainFrame.showView("DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel ticket", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
