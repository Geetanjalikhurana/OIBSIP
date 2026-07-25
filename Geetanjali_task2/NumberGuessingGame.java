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

    // Modern Dark Theme Colors
    private final Color BG_COLOR = new Color(30, 30, 34);
    private final Color PANEL_COLOR = new Color(45, 45, 50);
    private final Color TEXT_COLOR = new Color(240, 240, 240);
    private final Color PRIMARY_COLOR = new Color(0, 122, 204);
    private final Color PRIMARY_HOVER = new Color(0, 150, 255);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color DANGER_COLOR = new Color(220, 53, 69);

    // Modern Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Game variables
    private int targetNumber;
    private int minRange = 1;
    private int maxRange = 100;
    private int maxAttempts = 7;
    private int attemptsLeft;
    private int roundCount = 0;

    // UI Structure
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Menu Components
    private JTextArea scoreArea;
    private StyledButton btnEasy, btnMedium, btnHard;
    
    // Game Components
    private JLabel rangeLabel, attemptsLabel, feedbackLabel;
    private CustomTextField guessField;
    private StyledButton guessButton;
    private StyledButton hintButton;
    private boolean hintUsed;

    private ArrayList<String> roundHistory = new ArrayList<>();

    public NumberGuessingGame() {
        setTitle("Number Guessing Game");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);
        
        mainPanel.add(createMenuPanel(), "MENU");
        mainPanel.add(createGamePanel(), "GAME");

        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Number Guessing Game", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        panel.add(title, BorderLayout.NORTH);

        // Center Container
        JPanel centerContainer = new JPanel(new GridLayout(2, 1, 20, 20));
        centerContainer.setBackground(BG_COLOR);

        // Difficulty Selection
        JPanel diffWrapper = new JPanel(new BorderLayout(10, 10));
        diffWrapper.setBackground(PANEL_COLOR);
        diffWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel diffLabel = new JLabel("Select Difficulty", SwingConstants.CENTER);
        diffLabel.setFont(SUBTITLE_FONT);
        diffLabel.setForeground(TEXT_COLOR);
        diffWrapper.add(diffLabel, BorderLayout.NORTH);

        JPanel diffButtons = new JPanel(new GridLayout(1, 3, 10, 0));
        diffButtons.setBackground(PANEL_COLOR);

        btnEasy = new StyledButton("Easy (1-50)", new Color(60, 60, 65), PRIMARY_HOVER);
        btnMedium = new StyledButton("Medium (1-100)", PRIMARY_COLOR, PRIMARY_HOVER); // Default selected
        btnHard = new StyledButton("Hard (1-200)", new Color(60, 60, 65), PRIMARY_HOVER);

        // Difficulty click handlers
        btnEasy.addActionListener(e -> setDifficulty(1, 50, 10, btnEasy));
        btnMedium.addActionListener(e -> setDifficulty(1, 100, 7, btnMedium));
        btnHard.addActionListener(e -> setDifficulty(1, 200, 5, btnHard));

        diffButtons.add(btnEasy);
        diffButtons.add(btnMedium);
        diffButtons.add(btnHard);
        diffWrapper.add(diffButtons, BorderLayout.CENTER);
        
        StyledButton startBtn = new StyledButton("START GAME", SUCCESS_COLOR, new Color(45, 185, 75));
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        startBtn.addActionListener(e -> startGame());
        diffWrapper.add(startBtn, BorderLayout.SOUTH);

        centerContainer.add(diffWrapper);

        // Score History
        JPanel scoreWrapper = new JPanel(new BorderLayout(10, 10));
        scoreWrapper.setBackground(PANEL_COLOR);
        scoreWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel scoreTitle = new JLabel("Recent Scores", SwingConstants.CENTER);
        scoreTitle.setFont(BOLD_FONT);
        scoreTitle.setForeground(TEXT_COLOR);
        scoreWrapper.add(scoreTitle, BorderLayout.NORTH);

        scoreArea = new JTextArea("No rounds played yet.");
        scoreArea.setFont(REGULAR_FONT);
        scoreArea.setForeground(new Color(200, 200, 200));
        scoreArea.setBackground(PANEL_COLOR);
        scoreArea.setEditable(false);
        scoreArea.setHighlighter(null);
        
        JScrollPane scroll = new JScrollPane(scoreArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setBackground(PANEL_COLOR);
        scoreWrapper.add(scroll, BorderLayout.CENTER);

        centerContainer.add(scoreWrapper);
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    private void setDifficulty(int min, int max, int attempts, StyledButton activeBtn) {
        this.minRange = min;
        this.maxRange = max;
        this.maxAttempts = attempts;

        // Reset button colors
        btnEasy.setNormalColor(new Color(60, 60, 65));
        btnMedium.setNormalColor(new Color(60, 60, 65));
        btnHard.setNormalColor(new Color(60, 60, 65));
        
        // Highlight active
        activeBtn.setNormalColor(PRIMARY_COLOR);
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Range Label
        rangeLabel = new JLabel("Guess the number!", SwingConstants.CENTER);
        rangeLabel.setFont(TITLE_FONT);
        rangeLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(rangeLabel, gbc);

        // Attempts Label
        attemptsLabel = new JLabel("Attempts left: 7", SwingConstants.CENTER);
        attemptsLabel.setFont(SUBTITLE_FONT);
        attemptsLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        panel.add(attemptsLabel, gbc);

        // Feedback Label
        feedbackLabel = new JLabel("Good luck!", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        feedbackLabel.setForeground(new Color(150, 150, 150));
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 20, 30, 20);
        panel.add(feedbackLabel, gbc);

        // Input Field
        gbc.gridy = 3; gbc.insets = new Insets(0, 20, 10, 20);
        guessField = new CustomTextField();
        guessField.setHorizontalAlignment(JTextField.CENTER);
        
        // Clear error dynamically when user types
        guessField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { resetFeedback(); }
            public void removeUpdate(DocumentEvent e) { resetFeedback(); }
            public void changedUpdate(DocumentEvent e) { resetFeedback(); }
            
            private void resetFeedback() {
                if(feedbackLabel.getText().startsWith("Invalid")) {
                    feedbackLabel.setText(" ");
                }
            }
        });
        
        guessField.addActionListener(e -> processGuess());
        panel.add(guessField, gbc);

        // Submit & Hint Buttons
        gbc.gridy = 4; gbc.insets = new Insets(10, 20, 20, 20);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BG_COLOR);
        
        guessButton = new StyledButton("SUBMIT GUESS", PRIMARY_COLOR, PRIMARY_HOVER);
        guessButton.setPreferredSize(new Dimension(160, 45));
        guessButton.addActionListener(e -> processGuess());
        buttonPanel.add(guessButton);
        
        hintButton = new StyledButton("HINT", new Color(220, 120, 0), new Color(255, 150, 0));
        hintButton.setPreferredSize(new Dimension(90, 45));
        hintButton.addActionListener(e -> provideHint());
        buttonPanel.add(hintButton);
        
        panel.add(buttonPanel, gbc);

        // Quit Button
        gbc.gridy = 5; gbc.insets = new Insets(30, 20, 10, 20);
        StyledButton quitBtn = new StyledButton("Quit to Menu", new Color(60, 60, 65), DANGER_COLOR);
        quitBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        panel.add(quitBtn, gbc);

        return panel;
    }

    private void startGame() {
        targetNumber = new Random().nextInt(maxRange - minRange + 1) + minRange;
        attemptsLeft = maxAttempts;
        roundCount++;

        rangeLabel.setText("Number between " + minRange + " & " + maxRange);
        updateAttemptsLabel();
        feedbackLabel.setText("Make your first guess!");
        feedbackLabel.setForeground(new Color(150, 150, 150));
        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        
        hintUsed = false;
        hintButton.setEnabled(true);

        cardLayout.show(mainPanel, "GAME");
        SwingUtilities.invokeLater(() -> guessField.requestFocus());
    }

    private void updateAttemptsLabel() {
        attemptsLabel.setText("Attempts left: " + attemptsLeft);
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
            feedbackLabel.setText("Invalid input! Enter a valid number.");
            feedbackLabel.setForeground(DANGER_COLOR);
            guessField.setText("");
            return;
        }

        attemptsLeft--;
        updateAttemptsLabel();

        if (guess == targetNumber) {
            feedbackLabel.setText("Correct! The number was " + targetNumber);
            feedbackLabel.setForeground(SUCCESS_COLOR);
            endRound(true);
        } else if (guess < targetNumber) {
            feedbackLabel.setText("Too Low!");
            feedbackLabel.setForeground(new Color(255, 165, 0)); // Orange
        } else {
            feedbackLabel.setText("Too High!");
            feedbackLabel.setForeground(new Color(255, 165, 0));
        }

        guessField.setText("");
        guessField.requestFocus();

        if (attemptsLeft == 0 && guess != targetNumber) {
            feedbackLabel.setText("Out of attempts! Number was " + targetNumber);
            feedbackLabel.setForeground(DANGER_COLOR);
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
            hintMsg = targetNumber % 2 == 0 ? "Hint: The number is Even." : "Hint: The number is Odd.";
        } else if (selectedType.equals("multiple")) {
            int multiple = 2 + new Random().nextInt(4); // 2, 3, 4, 5
            int originalMultiple = multiple;
            while (targetNumber % multiple != 0 && multiple <= 7) {
                multiple++;
            }
            if (targetNumber % multiple == 0) {
                hintMsg = "Hint: The number is a multiple of " + multiple + ".";
            } else {
                hintMsg = "Hint: The number is not a multiple of " + originalMultiple + ".";
            }
        } else {
            int rangeOffset = 5 + new Random().nextInt(10);
            int low = Math.max(minRange, targetNumber - rangeOffset);
            int high = Math.min(maxRange, targetNumber + rangeOffset);
            hintMsg = "Hint: The number is between " + low + " and " + high + ".";
        }
        
        feedbackLabel.setText(hintMsg);
        feedbackLabel.setForeground(new Color(255, 180, 0)); // Golden Orange
    }

    private void endRound(boolean won) {
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        hintButton.setEnabled(false);

        int attemptsUsed = maxAttempts - attemptsLeft;
        String historyEntry = String.format("Round %d: %s | %s - %d attempts used",
                roundCount,
                (minRange + "-" + maxRange),
                won ? "WON" : "LOST",
                attemptsUsed);
        
        roundHistory.add(historyEntry);
        updateScoreArea();

        // Custom Dialog for Game Over
        Timer timer = new Timer(1500, e -> {
            UIManager.put("OptionPane.background", PANEL_COLOR);
            UIManager.put("Panel.background", PANEL_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            
            String msg = won ? "Incredible! You guessed it right!" : "Game Over! You'll get it next time.";
            int choice = JOptionPane.showConfirmDialog(this,
                    msg + "\nPlay again?",
                    won ? "Victory" : "Defeat",
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
        for (int i = roundHistory.size() - 1; i >= 0; i--) { // Reverse order
            sb.append(roundHistory.get(i)).append("\n");
        }
        scoreArea.setText(sb.toString());
    }

    // --- Custom UI Components --- //

    class StyledButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        public StyledButton(String text, Color normal, Color hover) {
            super(text);
            this.normalColor = normal;
            this.hoverColor = hover;
            
            setFont(BOLD_FONT);
            setForeground(Color.WHITE);
            setBackground(normalColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) setBackground(hoverColor);
                }
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) setBackground(normalColor);
                }
            });
        }
        
        public void setNormalColor(Color color) {
            this.normalColor = color;
            setBackground(color);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    class CustomTextField extends JTextField {
        public CustomTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 24));
            setForeground(Color.WHITE);
            setCaretColor(PRIMARY_COLOR);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(300, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2.setColor(new Color(60, 60, 65));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            
            // Border outline
            if (hasFocus()) {
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 15, 15));
            }
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        // Force cross-platform to remove OS-specific button styling
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new NumberGuessingGame().setVisible(true));
    }
}
