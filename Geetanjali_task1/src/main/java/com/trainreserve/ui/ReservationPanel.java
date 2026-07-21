package com.trainreserve.ui;

import com.trainreserve.db.DatabaseManager;
import com.trainreserve.db.PNRGenerator;
import com.trainreserve.model.Reservation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField pNameField, trainNoField, trainNameField, sourceField, destField, dateField;
    private JComboBox<String> classBox;

    public ReservationPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Book a Ticket");
        title.setFont(UITheme.TITLE_FONT.deriveFont(26f));
        title.setForeground(UITheme.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel formGrid = new JPanel(new GridLayout(0, 2, 20, 15));
        formGrid.setBackground(Color.WHITE);

        pNameField = createTextField();
        trainNoField = createTextField();
        trainNameField = createTextField();
        trainNameField.setEditable(false);
        trainNameField.setBackground(new Color(245, 245, 245));
        sourceField = createTextField();
        destField = createTextField();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField = createTextField();
        dateField.setText(sdf.format(new Date()));

        classBox = new JComboBox<>(new String[]{"1AC", "2AC", "3AC", "Sleeper", "Second Seating"});
        classBox.setFont(UITheme.LABEL_FONT);
        classBox.setBackground(Color.WHITE);

        formGrid.add(createLabel("Passenger Name"));
        formGrid.add(pNameField);
        
        formGrid.add(createLabel("Train Number"));
        formGrid.add(trainNoField);
        
        formGrid.add(createLabel("Train Name (Auto or Manual)"));
        formGrid.add(trainNameField);
        
        formGrid.add(createLabel("Class Type"));
        formGrid.add(classBox);
        
        formGrid.add(createLabel("Date of Journey (YYYY-MM-DD)"));
        formGrid.add(dateField);
        
        formGrid.add(createLabel("Source Station"));
        formGrid.add(sourceField);
        
        formGrid.add(createLabel("Destination Station"));
        formGrid.add(destField);

        content.add(formGrid);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.setBackground(UITheme.PRIMARY);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        bookBtn.setFocusPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.setMaximumSize(new Dimension(200, 45));
        
        bookBtn.addActionListener(e -> bookTicket());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(bookBtn);
        
        content.add(btnPanel);
        
        add(new JScrollPane(content), BorderLayout.CENTER);
        
        setupTrainLookup();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.LABEL_FONT);
        tf.setPreferredSize(new Dimension(200, 35));
        return tf;
    }

    private void setupTrainLookup() {
        trainNoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupTrain(); }
            public void removeUpdate(DocumentEvent e) { lookupTrain(); }
            public void changedUpdate(DocumentEvent e) { lookupTrain(); }
        });
    }

    private void lookupTrain() {
        String tno = trainNoField.getText().trim();
        if (tno.length() >= 4) {
            try {
                String tname = DatabaseManager.getInstance().getTrainName(tno);
                if (tname != null) {
                    trainNameField.setText(tname);
                    trainNameField.setEditable(false);
                    trainNameField.setBackground(new Color(245, 245, 245));
                } else {
                    trainNameField.setText("");
                    trainNameField.setEditable(true);
                    trainNameField.setBackground(new Color(255, 250, 205)); // light yellow indicating manual entry
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            trainNameField.setText("");
            trainNameField.setEditable(false);
            trainNameField.setBackground(new Color(245, 245, 245));
        }
    }

    private void bookTicket() {
        try {
            String pName = pNameField.getText().trim();
            String tNo = trainNoField.getText().trim();
            String tName = trainNameField.getText().trim();
            String cType = (String) classBox.getSelectedItem();
            String doj = dateField.getText().trim();
            String src = sourceField.getText().trim();
            String dest = destField.getText().trim();

            if (pName.isEmpty() || tNo.isEmpty() || tName.isEmpty() || doj.isEmpty() || src.isEmpty() || dest.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String pnr = PNRGenerator.generatePNR();
            String bookedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            Reservation res = new Reservation(pnr, pName, tNo, tName, cType, doj, src, dest, bookedAt);
            DatabaseManager.getInstance().insertReservation(res);

            String message = "Booking Successful!\n\n" +
                    "PNR: " + pnr + "\n" +
                    "Passenger: " + pName + "\n" +
                    "Train: " + tNo + " - " + tName + "\n" +
                    "Class: " + cType + "\n" +
                    "Date: " + doj + "\n" +
                    "From: " + src + " To: " + dest;

            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            
            pNameField.setText("");
            trainNoField.setText("");
            sourceField.setText("");
            destField.setText("");
            
            mainFrame.showView("DASHBOARD");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
