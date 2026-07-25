import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineExaminationSystem extends JFrame {

    // --- Modern Dark Theme Colors ---
    private static final Color COLOR_BG = new Color(18, 18, 18);
    private static final Color COLOR_PANEL = new Color(30, 30, 30);
    private static final Color COLOR_FG = new Color(224, 224, 224);
    private static final Color COLOR_ACCENT = new Color(66, 133, 244);
    private static final Color COLOR_ACCENT_HOVER = new Color(86, 153, 255);
    private static final Color COLOR_SUCCESS = new Color(46, 125, 50);
    private static final Color COLOR_ERROR = new Color(198, 40, 40);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 16);

    // --- State & Session ---
    private String currentUsername = "admin";
    private String currentPassword = "admin";
    private String displayName = "Test Student";

    // Navigation and Layout
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Exam State
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] userAnswers;
    private int timeLeftSeconds = 30 * 60; // 30 minutes
    private Timer timer;

    // UI Components to update
    private JLabel lblTimer;
    private JLabel lblQuestionHeader;
    private JLabel lblQuestionText;
    private JRadioButton[] optButtons;
    private ButtonGroup optGroup;
    private JButton btnPrev, btnNext;
    private JPanel resultDetailsPanel;
    private JLabel lblScore;

    public OnlineExaminationSystem() {
        setupTheme();
        setTitle("Modern Online Examination System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Window close confirmation (Session management)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });

        loadQuestions();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(COLOR_BG);

        mainContainer.add(createLoginPanel(), "LOGIN");
        mainContainer.add(createProfilePanel(), "PROFILE");
        mainContainer.add(createExamPanel(), "EXAM");
        mainContainer.add(createResultPanel(), "RESULT");

        add(mainContainer);
        cardLayout.show(mainContainer, "LOGIN");
    }

    private void setupTheme() {
        UIManager.put("Panel.background", COLOR_BG);
        UIManager.put("OptionPane.background", COLOR_BG);
        UIManager.put("OptionPane.messageForeground", COLOR_FG);
        UIManager.put("Button.background", COLOR_ACCENT);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Label.foreground", COLOR_FG);
        UIManager.put("TextField.background", COLOR_PANEL);
        UIManager.put("TextField.foreground", COLOR_FG);
        UIManager.put("TextField.caretForeground", COLOR_FG);
        UIManager.put("PasswordField.background", COLOR_PANEL);
        UIManager.put("PasswordField.foreground", COLOR_FG);
        UIManager.put("PasswordField.caretForeground", COLOR_FG);
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question("What is the default value of a local variable in Java?", 
                new String[]{"null", "0", "Depends on data type", "Not assigned"}, 3));
        questions.add(new Question("Which of these is not a feature of Java?", 
                new String[]{"Object-oriented", "Use of pointers", "Portable", "Dynamic"}, 1));
        questions.add(new Question("What is the size of int variable in Java?", 
                new String[]{"8 bit", "16 bit", "32 bit", "64 bit"}, 2));
        questions.add(new Question("Which component is used to compile, debug and execute the java programs?", 
                new String[]{"JRE", "JIT", "JDK", "JVM"}, 2));
        questions.add(new Question("Which of the following is a superclass of every class in Java?", 
                new String[]{"ArrayList", "Abstract class", "Object class", "String"}, 2));
        userAnswers = new int[questions.size()];
        for(int i=0; i<userAnswers.length; i++) userAnswers[i] = -1;
    }

    // --- Helper Methods to Style Components ---
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_NORMAL);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tf.setCaretColor(COLOR_FG);
        return tf;
    }

    // --- Panels ---

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        JLabel lblTitle = new JLabel("Exam Portal Login");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_ACCENT);
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(FONT_NORMAL);
        card.add(lblUser, gbc);

        gbc.gridx = 1;
        JTextField txtUser = createStyledTextField();
        txtUser.setPreferredSize(new Dimension(200, 35));
        card.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(FONT_NORMAL);
        card.add(lblPass, gbc);

        gbc.gridx = 1;
        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(FONT_NORMAL);
        txtPass.setPreferredSize(new Dimension(200, 35));
        txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton btnLogin = createStyledButton("Login");
        btnLogin.setPreferredSize(new Dimension(300, 40));
        card.add(btnLogin, gbc);
        
        JLabel lblHint = new JLabel("Hint: use admin / admin");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);
        gbc.gridy = 4;
        card.add(lblHint, gbc);

        btnLogin.addActionListener(e -> {
            if (txtUser.getText().equals(currentUsername) && new String(txtPass.getPassword()).equals(currentPassword)) {
                cardLayout.show(mainContainer, "PROFILE");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(card);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COLOR_PANEL);
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        JLabel lblTitle = new JLabel("Update Profile");
        lblTitle.setFont(FONT_TITLE);
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        card.add(new JLabel("Display Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtName = createStyledTextField();
        txtName.setText(displayName);
        txtName.setPreferredSize(new Dimension(200, 35));
        card.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField txtPass = new JPasswordField();
        txtPass.setText(currentPassword);
        txtPass.setPreferredSize(new Dimension(200, 35));
        card.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton btnStart = createStyledButton("Start Exam");
        btnStart.setBackground(COLOR_SUCCESS);
        card.add(btnStart, gbc);

        btnStart.addActionListener(e -> {
            displayName = txtName.getText();
            currentPassword = new String(txtPass.getPassword());
            startExam();
        });

        panel.add(card);
        return panel;
    }

    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        // Header (Timer and User)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PANEL);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblUser = new JLabel("Candidate: " + displayName);
        lblUser.setFont(FONT_HEADER);
        headerPanel.add(lblUser, BorderLayout.WEST);

        lblTimer = new JLabel("Time: 30:00");
        lblTimer.setFont(new Font("Consolas", Font.BOLD, 22));
        lblTimer.setForeground(COLOR_ERROR);
        headerPanel.add(lblTimer, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center Content (Question & Options)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        lblQuestionHeader = new JLabel("Question 1 of 5");
        lblQuestionHeader.setFont(FONT_HEADER);
        lblQuestionHeader.setForeground(COLOR_ACCENT);
        centerPanel.add(lblQuestionHeader, BorderLayout.NORTH);

        JPanel qPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        qPanel.setBackground(COLOR_BG);
        
        lblQuestionText = new JLabel("Question text goes here?");
        lblQuestionText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        qPanel.add(lblQuestionText);
        
        qPanel.add(new JLabel()); // Spacer

        optButtons = new JRadioButton[4];
        optGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optButtons[i] = new JRadioButton("Option " + (i+1));
            optButtons[i].setBackground(COLOR_BG);
            optButtons[i].setForeground(COLOR_FG);
            optButtons[i].setFont(FONT_NORMAL);
            optButtons[i].setFocusPainted(false);
            optGroup.add(optButtons[i]);
            qPanel.add(optButtons[i]);
            
            final int index = i;
            optButtons[i].addActionListener(e -> userAnswers[currentQuestionIndex] = index);
        }
        
        centerPanel.add(qPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Footer Navigation
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        footerPanel.setBackground(COLOR_PANEL);

        btnPrev = createStyledButton("Previous");
        btnNext = createStyledButton("Next");
        JButton btnSubmit = createStyledButton("Submit Exam");
        btnSubmit.setBackground(COLOR_ERROR);

        btnPrev.addActionListener(e -> navigateQuestion(-1));
        btnNext.addActionListener(e -> navigateQuestion(1));
        btnSubmit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to submit your exam early?", 
                    "Confirm Submission", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                finishExam();
            }
        });

        footerPanel.add(btnPrev);
        footerPanel.add(btnNext);
        footerPanel.add(btnSubmit);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        JPanel header = new JPanel();
        header.setBackground(COLOR_PANEL);
        header.setBorder(new EmptyBorder(20, 10, 20, 10));
        lblScore = new JLabel("Your Score: 0 / 5");
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblScore.setForeground(COLOR_ACCENT);
        header.add(lblScore);
        panel.add(header, BorderLayout.NORTH);

        resultDetailsPanel = new JPanel();
        resultDetailsPanel.setLayout(new BoxLayout(resultDetailsPanel, BoxLayout.Y_AXIS));
        resultDetailsPanel.setBackground(COLOR_BG);
        resultDetailsPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JScrollPane scrollPane = new JScrollPane(resultDetailsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(COLOR_PANEL);
        JButton btnLogout = createStyledButton("Logout");
        btnLogout.addActionListener(e -> {
            resetSystem();
            cardLayout.show(mainContainer, "LOGIN");
        });
        footer.add(btnLogout);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private void startExam() {
        for(int i=0; i<userAnswers.length; i++) userAnswers[i] = -1;
        currentQuestionIndex = 0;
        timeLeftSeconds = 30 * 60;
        
        loadQuestionUI();
        cardLayout.show(mainContainer, "EXAM");

        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            timeLeftSeconds--;
            int min = timeLeftSeconds / 60;
            int sec = timeLeftSeconds % 60;
            lblTimer.setText(String.format("Time: %02d:%02d", min, sec));
            if (timeLeftSeconds <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Time is up! Auto-submitting exam.");
                finishExam();
            }
        });
        timer.start();
    }

    private void loadQuestionUI() {
        Question q = questions.get(currentQuestionIndex);
        lblQuestionHeader.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        lblQuestionText.setText("<html>" + q.text + "</html>");
        
        optGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optButtons[i].setText(q.options[i]);
            if (userAnswers[currentQuestionIndex] == i) {
                optButtons[i].setSelected(true);
            }
        }
        
        btnPrev.setEnabled(currentQuestionIndex > 0);
        btnNext.setEnabled(currentQuestionIndex < questions.size() - 1);
    }

    private void navigateQuestion(int direction) {
        currentQuestionIndex += direction;
        loadQuestionUI();
    }

    private void finishExam() {
        if (timer != null) timer.stop();
        
        int score = 0;
        resultDetailsPanel.removeAll();
        
        JLabel titleLabel = new JLabel("Detailed Breakdown:");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(COLOR_FG);
        resultDetailsPanel.add(titleLabel);
        resultDetailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            boolean correct = (userAnswers[i] == q.correctIndex);
            if (correct) score++;

            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(COLOR_PANEL);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(correct ? COLOR_SUCCESS : COLOR_ERROR, 1),
                    new EmptyBorder(10, 15, 10, 15)
            ));
            p.setMaximumSize(new Dimension(800, 120));

            JLabel qLabel = new JLabel("<html><b>Q" + (i+1) + ": " + q.text + "</b></html>");
            qLabel.setForeground(COLOR_FG);
            p.add(qLabel, BorderLayout.NORTH);

            String ansText = userAnswers[i] == -1 ? "Not Answered" : q.options[userAnswers[i]];
            JLabel aLabel = new JLabel("<html>Your Answer: " + ansText + "<br>Correct: " + q.options[q.correctIndex] + "</html>");
            aLabel.setForeground(COLOR_FG);
            p.add(aLabel, BorderLayout.CENTER);

            resultDetailsPanel.add(p);
            resultDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        int timeTaken = (30 * 60) - timeLeftSeconds;
        int m = timeTaken / 60;
        int s = timeTaken % 60;
        
        lblScore.setText(String.format("Score: %d / %d  |  Time Taken: %02d:%02d", score, questions.size(), m, s));
        resultDetailsPanel.revalidate();
        resultDetailsPanel.repaint();

        cardLayout.show(mainContainer, "RESULT");
    }
    
    private void resetSystem() {
        currentUsername = "admin";
        currentPassword = "admin";
        displayName = "Test Student";
        for(int i=0; i<userAnswers.length; i++) userAnswers[i] = -1;
    }

    private void confirmAndExit() {
        if (mainContainer.getComponents()[2].isVisible() && timeLeftSeconds > 0 && timer.isRunning()) {
            // In exam
            int response = JOptionPane.showConfirmDialog(this, 
                "You are currently taking an exam. Are you sure you want to quit?", 
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new OnlineExaminationSystem().setVisible(true);
        });
    }

    // Inner class for Questions
    class Question {
        String text;
        String[] options;
        int correctIndex;

        public Question(String text, String[] options, int correctIndex) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }
}
