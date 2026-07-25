import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class NumberGuessingGame extends JFrame {

    // Sci-Fi Theme Colors
    private final Color BG_COLOR = new Color(5, 10, 15);
    private final Color PANEL_COLOR = new Color(10, 15, 20);
    private final Color TEXT_COLOR = new Color(0, 255, 204); // Neon Cyan
    private final Color PRIMARY_COLOR = new Color(0, 200, 255);
    private final Color PRIMARY_HOVER = new Color(0, 255, 255);
    private final Color SUCCESS_COLOR = new Color(0, 255, 65); // Matrix Green
    private final Color DANGER_COLOR = new Color(255, 0, 85); // Neon Pink/Red
    private final Color HINT_COLOR = new Color(255, 200, 0); // Neon Yellow

    // Monospaced Fonts
    private final Font TITLE_FONT = new Font("Consolas", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Consolas", Font.BOLD, 18);
    private final Font REGULAR_FONT = new Font("Consolas", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Consolas", Font.BOLD, 16);

    // Game variables
    private int targetNumber;
    private int minRange = 1;
    private int maxRange = 100;
    private int maxAttempts = 7;
    private int attemptsLeft;
    private int roundCount = 0;
    private boolean hintUsed;

    // UI Structure
    private SciFiPanel mainPanel;
    private CardLayout cardLayout;

    // Menu Components
    private JTextArea scoreArea;
    private SciFiButton btnEasy, btnMedium, btnHard;
    
    // Game Components
    private JLabel rangeLabel, attemptsLabel, feedbackLabel;
    private SciFiTextField guessField;
    private SciFiButton guessButton, hintButton;
    private Timer typingTimer; // For typing animation

    private ArrayList<String> roundHistory = new ArrayList<>();

    public NumberGuessingGame() {
        setTitle("AI.SYS // NUMBER_GUESSER");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        cardLayout = new CardLayout();
        mainPanel = new SciFiPanel(cardLayout);
        
        mainPanel.add(createMenuPanel(), "MENU");
        mainPanel.add(createGamePanel(), "GAME");

        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
    }

    // Helper for typing effect
    private void animateText(JLabel label, String text, Color color) {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }
        label.setForeground(color);
        label.setText("");
        int[] index = {0};
        typingTimer = new Timer(25, e -> {
            if (index[0] < text.length()) {
                label.setText(label.getText() + text.charAt(index[0]));
                index[0]++;
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        typingTimer.start();
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("INITIALIZE // NUMBER_GUESSER", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_HOVER);
        panel.add(title, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new GridLayout(2, 1, 20, 20));
        centerContainer.setOpaque(false);

        JPanel diffWrapper = new JPanel(new BorderLayout(10, 10));
        diffWrapper.setOpaque(false);
        
        JLabel diffLabel = new JLabel("> SELECT CALIBRATION LEVEL", SwingConstants.CENTER);
        diffLabel.setFont(SUBTITLE_FONT);
        diffLabel.setForeground(TEXT_COLOR);
        diffWrapper.add(diffLabel, BorderLayout.NORTH);

        JPanel diffButtons = new JPanel(new GridLayout(1, 3, 10, 0));
        diffButtons.setOpaque(false);

        btnEasy = new SciFiButton("LVL_1 (1-50)", PANEL_COLOR, PRIMARY_HOVER);
        btnMedium = new SciFiButton("LVL_2 (1-100)", PRIMARY_COLOR, PRIMARY_HOVER); 
        btnHard = new SciFiButton("LVL_3 (1-200)", PANEL_COLOR, PRIMARY_HOVER);

        btnEasy.addActionListener(e -> setDifficulty(1, 50, 10, btnEasy));
        btnMedium.addActionListener(e -> setDifficulty(1, 100, 7, btnMedium));
        btnHard.addActionListener(e -> setDifficulty(1, 200, 5, btnHard));

        diffButtons.add(btnEasy);
        diffButtons.add(btnMedium);
        diffButtons.add(btnHard);
        diffWrapper.add(diffButtons, BorderLayout.CENTER);
        
        SciFiButton startBtn = new SciFiButton("EXECUTE_START()", SUCCESS_COLOR, new Color(0, 255, 100));
        startBtn.setFont(new Font("Consolas", Font.BOLD, 20));
        startBtn.addActionListener(e -> startGame());
        diffWrapper.add(startBtn, BorderLayout.SOUTH);

        centerContainer.add(diffWrapper);

        JPanel scoreWrapper = new JPanel(new BorderLayout(10, 10));
        scoreWrapper.setOpaque(false);

        JLabel scoreTitle = new JLabel("> SYSTEM_LOG // PREVIOUS_ROUNDS", SwingConstants.CENTER);
        scoreTitle.setFont(BOLD_FONT);
        scoreTitle.setForeground(TEXT_COLOR);
        scoreWrapper.add(scoreTitle, BorderLayout.NORTH);

        scoreArea = new JTextArea("AWAITING_INPUT...");
        scoreArea.setFont(REGULAR_FONT);
        scoreArea.setForeground(TEXT_COLOR);
        scoreArea.setBackground(PANEL_COLOR);
        scoreArea.setEditable(false);
        scoreArea.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 150), 1));
        
        JScrollPane scroll = new JScrollPane(scoreArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scoreWrapper.add(scroll, BorderLayout.CENTER);

        centerContainer.add(scoreWrapper);
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    private void setDifficulty(int min, int max, int attempts, SciFiButton activeBtn) {
        this.minRange = min;
        this.maxRange = max;
        this.maxAttempts = attempts;
        btnEasy.setNormalColor(PANEL_COLOR);
        btnMedium.setNormalColor(PANEL_COLOR);
        btnHard.setNormalColor(PANEL_COLOR);
        activeBtn.setNormalColor(PRIMARY_COLOR);
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        rangeLabel = new JLabel("", SwingConstants.CENTER);
        rangeLabel.setFont(TITLE_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(rangeLabel, gbc);

        attemptsLabel = new JLabel("", SwingConstants.CENTER);
        attemptsLabel.setFont(SUBTITLE_FONT);
        gbc.gridy = 1;
        panel.add(attemptsLabel, gbc);

        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 20, 30, 20);
        panel.add(feedbackLabel, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 20, 10, 20);
        guessField = new SciFiTextField();
        guessField.setHorizontalAlignment(JTextField.CENTER);
        
        guessField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { resetFeedback(); }
            public void removeUpdate(DocumentEvent e) { resetFeedback(); }
            public void changedUpdate(DocumentEvent e) { resetFeedback(); }
            
            private void resetFeedback() {
                if(feedbackLabel.getText().startsWith("ERR:")) {
                    feedbackLabel.setText(" ");
                }
            }
        });
        
        guessField.addActionListener(e -> processGuess());
        panel.add(guessField, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(10, 20, 20, 20);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        guessButton = new SciFiButton("SUBMIT_DATA", PRIMARY_COLOR, PRIMARY_HOVER);
        guessButton.setPreferredSize(new Dimension(180, 45));
        guessButton.addActionListener(e -> processGuess());
        buttonPanel.add(guessButton);
        
        hintButton = new SciFiButton("QUERY_HINT", HINT_COLOR, new Color(255, 255, 100));
        hintButton.setPreferredSize(new Dimension(120, 45));
        hintButton.addActionListener(e -> provideHint());
        buttonPanel.add(hintButton);
        
        panel.add(buttonPanel, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(30, 20, 10, 20);
        SciFiButton quitBtn = new SciFiButton("ABORT_OPERATION", PANEL_COLOR, DANGER_COLOR);
        quitBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        panel.add(quitBtn, gbc);

        return panel;
    }

    private void startGame() {
        targetNumber = new Random().nextInt(maxRange - minRange + 1) + minRange;
        attemptsLeft = maxAttempts;
        roundCount++;

        animateText(rangeLabel, "SCANNING RANGE: [" + minRange + " - " + maxRange + "]", PRIMARY_HOVER);
        updateAttemptsLabel();
        animateText(feedbackLabel, "SYSTEM_READY: AWAITING INPUT...", TEXT_COLOR);
        
        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        hintUsed = false;
        hintButton.setEnabled(true);

        cardLayout.show(mainPanel, "GAME");
        SwingUtilities.invokeLater(() -> guessField.requestFocus());
    }

    private void updateAttemptsLabel() {
        attemptsLabel.setText("ATTEMPTS_REMAINING: " + attemptsLeft);
        if (attemptsLeft <= 2) {
            attemptsLabel.setForeground(DANGER_COLOR);
        } else {
            attemptsLabel.setForeground(TEXT_COLOR);
        }
    }

    private void processGuess() {
        if (attemptsLeft <= 0) return;

        String input = guessField.getText().trim();
        if (input.isEmpty()) return;

        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            animateText(feedbackLabel, "ERR: INVALID_DATA_TYPE_EXCEPTION", DANGER_COLOR);
            guessField.setText("");
            return;
        }

        attemptsLeft--;
        updateAttemptsLabel();

        if (guess == targetNumber) {
            animateText(feedbackLabel, "MATCH_FOUND! TARGET IDENTIFIED: " + targetNumber, SUCCESS_COLOR);
            endRound(true);
        } else if (guess < targetNumber) {
            animateText(feedbackLabel, "STATUS: VALUE_TOO_LOW. RECALCULATING...", HINT_COLOR);
        } else {
            animateText(feedbackLabel, "STATUS: VALUE_TOO_HIGH. RECALCULATING...", HINT_COLOR);
        }

        guessField.setText("");
        guessField.requestFocus();

        if (attemptsLeft == 0 && guess != targetNumber) {
            animateText(feedbackLabel, "CRITICAL: OUT_OF_ATTEMPTS! TRUTH: " + targetNumber, DANGER_COLOR);
            endRound(false);
        }
    }

    private void provideHint() {
        if (hintUsed) return;
        hintUsed = true;
        hintButton.setEnabled(false);
        
        String[] hintTypes = {"even_odd", "multiple", "range"};
        String selectedType = hintTypes[new Random().nextInt(hintTypes.length)];
        
        String hintMsg = "";
        if (selectedType.equals("even_odd")) {
            hintMsg = targetNumber % 2 == 0 ? "DECRYPTING... TARGET_IS_EVEN" : "DECRYPTING... TARGET_IS_ODD";
        } else if (selectedType.equals("multiple")) {
            int multiple = 2 + new Random().nextInt(4);
            int originalMultiple = multiple;
            while (targetNumber % multiple != 0 && multiple <= 7) {
                multiple++;
            }
            if (targetNumber % multiple == 0) {
                hintMsg = "DECRYPTING... MULTIPLE_OF_" + multiple;
            } else {
                hintMsg = "DECRYPTING... NOT_MULTIPLE_OF_" + originalMultiple;
            }
        } else {
            int rangeOffset = 5 + new Random().nextInt(10);
            int low = Math.max(minRange, targetNumber - rangeOffset);
            int high = Math.min(maxRange, targetNumber + rangeOffset);
            hintMsg = "DECRYPTING... IN_BOUNDS [" + low + " TO " + high + "]";
        }
        
        animateText(feedbackLabel, hintMsg, HINT_COLOR);
    }

    private void endRound(boolean won) {
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        hintButton.setEnabled(false);

        int attemptsUsed = maxAttempts - attemptsLeft;
        String historyEntry = String.format("LOG_%d: RANGE[%d-%d] | %s | ATTEMPTS:%d",
                roundCount, minRange, maxRange,
                won ? "SUCCESS" : "FAILURE", attemptsUsed);
        
        roundHistory.add(historyEntry);
        updateScoreArea();

        Timer timer = new Timer(2000, e -> {
            UIManager.put("OptionPane.background", BG_COLOR);
            UIManager.put("Panel.background", BG_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            
            String msg = won ? "MISSION ACCOMPLISHED." : "MISSION FAILED. TARGET LOST.";
            int choice = JOptionPane.showConfirmDialog(this,
                    msg + "\nREBOOT SEQUENCE?",
                    won ? "SUCCESS" : "FATAL_ERROR",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                startGame();
            } else {
                cardLayout.show(mainPanel, "MENU");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateScoreArea() {
        StringBuilder sb = new StringBuilder();
        for (int i = roundHistory.size() - 1; i >= 0; i--) {
            sb.append(roundHistory.get(i)).append("\n");
        }
        scoreArea.setText(sb.toString());
    }

    // --- Custom UI Components --- //

    class SciFiPanel extends JPanel {
        public SciFiPanel(LayoutManager layout) { super(layout); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(BG_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw subtle sci-fi grid
            g2.setColor(new Color(0, 50, 40, 100)); // semi-transparent
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i < getWidth(); i += 40) {
                g2.drawLine(i, 0, i, getHeight());
            }
            for (int i = 0; i < getHeight(); i += 40) {
                g2.drawLine(0, i, getWidth(), i);
            }
            g2.dispose();
        }
    }

    class SciFiButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        public SciFiButton(String text, Color normal, Color hover) {
            super(text);
            this.normalColor = normal;
            this.hoverColor = hover;
            
            setFont(BOLD_FONT);
            setBackground(normalColor);
            updateTextColor(normalColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(hoverColor);
                        updateTextColor(hoverColor);
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(normalColor);
                        updateTextColor(normalColor);
                    }
                }
            });
        }
        
        private void updateTextColor(Color bgColor) {
            if (bgColor.equals(PANEL_COLOR)) {
                setForeground(TEXT_COLOR);
            } else {
                setForeground(BG_COLOR);
            }
        }
        
        public void setNormalColor(Color color) {
            this.normalColor = color;
            setBackground(color);
            updateTextColor(color);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Glowing border effect
            for(int i=0; i<3; i++) {
                g2.setColor(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 50 - (i*15)));
                g2.draw(new RoundRectangle2D.Float(i, i, getWidth()-(i*2)-1, getHeight()-(i*2)-1, 5, 5));
            }
            
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(3, 3, getWidth()-7, getHeight()-7, 5, 5));
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    class SciFiTextField extends JTextField {
        public SciFiTextField() {
            setOpaque(false);
            setFont(new Font("Consolas", Font.BOLD, 24));
            setForeground(PRIMARY_HOVER);
            setCaretColor(PRIMARY_HOVER);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(300, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(PANEL_COLOR);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 5, 5));
            
            if (hasFocus()) {
                g2.setColor(PRIMARY_HOVER);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-3, getHeight()-3, 5, 5));
            } else {
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-3, getHeight()-3, 5, 5));
            }
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new NumberGuessingGame().setVisible(true));
    }
}
