package View;

import DAO.DatabaseConnection;
import DAO.ProductDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel dbStatusLabel;

    public LoginFrame() {
        setTitle("Gestionnaire de Stock - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400); // Slightly taller for status messages
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(new ImageIcon("C:\\Users\\elyes\\Downloads\\4661334.PNG").getImage());
        initComponents();
    }

    private void initComponents() {
        // Main panel with BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 245, 255));

        // Title
        JLabel titleLabel = new JLabel("Gestionnaire de Stock", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Database status
        dbStatusLabel = new JLabel("", SwingConstants.CENTER);
        dbStatusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dbStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateDatabaseStatus();

        // Username panel
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        usernamePanel.setBackground(new Color(240, 245, 255));
        JLabel userLabel = new JLabel("Nom d'utilisateur: ");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameField = new JTextField(20);

        usernamePanel.add(userLabel);
        usernamePanel.add(usernameField);

        // Password panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setBackground(new Color(240, 245, 255));
        JLabel passLabel = new JLabel("Mot de passe:       ");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = new JPasswordField(20);
        passwordPanel.add(passLabel);
        passwordPanel.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(180, 40));
        loginButton.addActionListener(e -> handleLogin());


        // Add components with spacing
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(dbStatusLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));


        add(mainPanel);

        // Set focus and add Enter key support
        usernameField.requestFocus();
        passwordField.addActionListener(e -> handleLogin());
    }

    private void updateDatabaseStatus() {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.testConnection();
            }

            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        dbStatusLabel.setText("Base de données connectée");
                        dbStatusLabel.setForeground(new Color(0, 150, 0));
                    } else {
                        dbStatusLabel.setText("Base de données non connectée");
                        dbStatusLabel.setForeground(Color.RED);
                    }
                } catch (Exception e) {
                    dbStatusLabel.setText("Erreur de connexion à la base");
                    dbStatusLabel.setForeground(Color.RED);
                }
            }
        }.execute();
    }


    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show loading
        JButton loginButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(8);
        loginButton.setText("Connexion...");
        loginButton.setEnabled(false);

        // Test database connection first
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                    "Impossible de se connecter à la base de données!\n\n" +
                            "Vérifiez que:\n" +
                            "1. MySQL est démarré\n" +
                            "2. La base 'gestion_stock' existe\n" +
                            "3. La table 'users' existe",
                    "Erreur de base de données",
                    JOptionPane.ERROR_MESSAGE);
            loginButton.setText("Se connecter");
            loginButton.setEnabled(true);
            return;
        }

        // Authenticate in background
        new SwingWorker<Boolean, Void>() {
            private String userRole;

            @Override
            protected Boolean doInBackground() {
                // Authenticate against database
                boolean authenticated = ProductDAO.authenticateUser(username, password);
                if (authenticated) {
                    userRole = ProductDAO.getUserRole(username);
                }
                return authenticated;
            }

            @Override
            protected void done() {
                try {
                    boolean authenticated = get();

                    if (authenticated) {
                        // Show welcome message with role
                        String welcomeMessage = "Bienvenue, " + username + "!";
                        if (userRole != null && !userRole.isEmpty()) {
                            welcomeMessage += " (Rôle: " + userRole + ")";
                        }

                        JOptionPane.showMessageDialog(LoginFrame.this,
                                welcomeMessage,
                                "Connexion réussie",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Open dashboard
                        SwingUtilities.invokeLater(() -> {
                            DashboardFrame dashboard = new DashboardFrame();
                            dashboard.setVisible(true);
                            dispose(); // Close login window
                        });
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Nom d'utilisateur ou mot de passe incorrect\n\n" +
                                        "Essayez avec:\n" +
                                        "• admin / admin123\n" +
                                        "• user / password123",
                                "Échec d'authentification",
                                JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Erreur d'authentification: " + e.getMessage() +
                                    "\n\nUtilisez les identifiants de démonstration.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    loginButton.setText("Se connecter");
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }
}