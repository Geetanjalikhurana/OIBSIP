package com.trainreserve.ui;

import com.trainreserve.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private User currentUser;
    
    // Panels
    private LoginPanel loginPanel;
    private ReservationPanel reservationPanel;
    private CancellationPanel cancellationPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        setTitle("Railway Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.BACKGROUND);

        // Initialize panels
        loginPanel = new LoginPanel(this);
        
        mainContentPanel.add(loginPanel, "LOGIN");
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        showView("LOGIN");
    }

    public void onLoginSuccess(User user) {
        this.currentUser = user;
        
        // Initialize protected panels now that we have a user
        dashboardPanel = new DashboardPanel(this);
        reservationPanel = new ReservationPanel(this);
        cancellationPanel = new CancellationPanel(this);
        
        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(reservationPanel, "RESERVE");
        mainContentPanel.add(cancellationPanel, "CANCEL");

        // Set up the sidebar Layout
        JPanel appPanel = new JPanel(new BorderLayout());
        appPanel.add(createSidebar(), BorderLayout.WEST);
        appPanel.add(mainContentPanel, BorderLayout.CENTER);
        
        setContentPane(appPanel);
        revalidate();
        
        showView("DASHBOARD");
    }

    public void onLogout() {
        this.currentUser = null;
        setContentPane(mainContentPanel);
        showView("LOGIN");
        revalidate();
    }

    public void showView(String viewName) {
        cardLayout.show(mainContentPanel, viewName);
    }
    
    public User getCurrentUser() {
        return currentUser;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.PRIMARY);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("INDIAN RAILWAYS");
        title.setFont(UITheme.TITLE_FONT.deriveFont(20f));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        sidebar.add(createNavButton("Dashboard", "DASHBOARD"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Book Ticket", "RESERVE"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Cancel Ticket", "CANCEL"));
        sidebar.add(Box.createVerticalGlue());
        
        JPanel logoutBtn = createNavButton("Logout", null);
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onLogout();
            }
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JPanel createNavButton(String text, String viewName) {
        JPanel btn = new JPanel(new BorderLayout());
        btn.setBackground(UITheme.PRIMARY);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.LABEL_FONT.deriveFont(14f));
        lbl.setForeground(Color.WHITE);
        btn.add(lbl, BorderLayout.WEST);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(UITheme.SECONDARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(UITheme.PRIMARY);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (viewName != null) {
                    showView(viewName);
                }
            }
        });
        
        return btn;
    }
}
