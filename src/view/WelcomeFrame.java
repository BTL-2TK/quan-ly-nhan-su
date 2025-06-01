package view;

import utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WelcomeFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton testButton;
    private JLabel closeLabel;
    private JLabel usernamePlaceholder;
    private JLabel passwordPlaceholder;
    private JLabel registerLabel;
    private JLabel forgotPasswordLabel;
    private JLabel subtitleLabel;
    private Timer breathingTimer;
    private boolean breathingIn = true;
    // Thêm biến cho hiệu ứng đánh máy
    private Timer typingTimer;
    private String typingText = "To the best HR Management System";
    private int typingIndex = 0;
    private boolean typingForward = true; // Thêm biến trạng thái

    public WelcomeFrame() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("HR Management System");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
    }

    private void initializeComponents() {
        // Bỏ breathingTimer, thay bằng typingTimer cho hiệu ứng đánh máy
        typingTimer = new Timer(45, e -> {
            if (typingForward) {
                if (subtitleLabel != null && typingIndex <= typingText.length()) {
                    subtitleLabel.setText(typingText.substring(0, typingIndex));
                    typingIndex++;
                } else if (typingIndex > typingText.length()) {
                    // Đợi 1 chút rồi bắt đầu xóa từng ký tự
                    typingForward = false;
                    ((Timer) e.getSource()).setDelay(30);
                    try { Thread.sleep(700); } catch (InterruptedException ignored) {}
                }
            } else {
                if (typingIndex >= 0) {
                    subtitleLabel.setText(typingText.substring(0, typingIndex));
                    typingIndex--;
                } else {
                    // Đợi 1 chút rồi bắt đầu lại hiệu ứng đánh máy
                    typingForward = true;
                    ((Timer) e.getSource()).setDelay(45);
                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                }
            }
        });
        typingTimer.start();

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        usernameField.setOpaque(false);
        usernameField.setForeground(new Color(60, 60, 60));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        passwordField.setOpaque(false);
        passwordField.setForeground(new Color(60, 60, 60));

        usernamePlaceholder = new JLabel("Enter your username");
        usernamePlaceholder.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernamePlaceholder.setForeground(new Color(150, 150, 150));

        passwordPlaceholder = new JLabel("Enter your password");
        passwordPlaceholder.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordPlaceholder.setForeground(new Color(150, 150, 150));

        loginButton = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(new Color(64, 169, 243));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2d.setColor(new Color(64, 169, 243));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        testButton = new JButton("Test System");
        testButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        testButton.setForeground(Color.WHITE);
        testButton.setBackground(new Color(76, 175, 80));
        testButton.setFocusPainted(false);
        testButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLabel = new JLabel("Register");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLabel.setForeground(new Color(64, 169, 243));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPasswordLabel.setForeground(new Color(64, 169, 243));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeLabel = new JLabel("×");
        closeLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        closeLabel.setForeground(new Color(120, 120, 120));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        setLayout(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBounds(10, 10, 880, 530);

        // Left panel gradient background
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color[] colors = {
                        new Color(20, 136, 204),
                        new Color(43, 154, 235),
                        new Color(64, 169, 243)
                };
                float[] fractions = {0.0f, 0.5f, 1.0f};
                LinearGradientPaint gradient = new LinearGradientPaint(
                        0, 0, getWidth(), getHeight(),
                        fractions, colors
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 100, getHeight() - 150, 150, 150);
            }
        };
        leftPanel.setBounds(0, 0, 440, 530);
        leftPanel.setLayout(null);

        JLabel welcomeLabel = new JLabel("WELCOME") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.setFont(getFont());
                g2d.drawString(getText(), 3, getHeight() - 3);
                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, getHeight() - 6);
            }
        };
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(50, 150, 340, 70);
        leftPanel.add(welcomeLabel);

        subtitleLabel = new JLabel(""); // Bắt đầu là rỗng để đánh máy
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setBounds(50, 220, 340, 25);
        leftPanel.add(subtitleLabel);

        JPanel decorativeLine = new JPanel();
        decorativeLine.setBackground(new Color(255, 255, 255, 100));
        decorativeLine.setBounds(50, 260, 80, 3);
        leftPanel.add(decorativeLine);

        JLabel poweredByLabel = new JLabel("POWERED BY 2TK");
        poweredByLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        poweredByLabel.setForeground(new Color(255, 255, 255, 200));
        poweredByLabel.setBounds(50, 290, 340, 20);
        leftPanel.add(poweredByLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBounds(440, 0, 440, 530);
        rightPanel.setLayout(null);

        closeLabel.setBounds(390, 20, 30, 30);
        rightPanel.add(closeLabel);

        JLabel loginTitle = new JLabel("LOGIN");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginTitle.setForeground(new Color(60, 60, 60));
        loginTitle.setBounds(60, 80, 150, 40);
        rightPanel.add(loginTitle);

        JLabel loginSubtitle = new JLabel("Sign in to continue");
        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginSubtitle.setForeground(new Color(120, 120, 120));
        loginSubtitle.setBounds(60, 120, 200, 20);
        rightPanel.add(loginSubtitle);

        JPanel usernameContainer = createInputContainer();
        usernameContainer.setBounds(60, 180, 320, 60);
        usernameContainer.add(usernameField);
        usernameContainer.add(usernamePlaceholder);
        rightPanel.add(usernameContainer);

        JPanel passwordContainer = createInputContainer();
        passwordContainer.setBounds(60, 260, 320, 60);
        passwordContainer.add(passwordField);
        passwordContainer.add(passwordPlaceholder);
        rightPanel.add(passwordContainer);

        loginButton.setBounds(60, 360, 320, 50);
        rightPanel.add(loginButton);

        testButton.setBounds(60, 420, 320, 35);
        rightPanel.add(testButton);

        registerLabel.setBounds(120, 460, 80, 30);
        rightPanel.add(registerLabel);

        forgotPasswordLabel.setBounds(240, 460, 120, 30);
        rightPanel.add(forgotPasswordLabel);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
    }

    private JPanel createInputContainer() {
        JPanel container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(248, 249, 250));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(new Color(220, 220, 220));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };
        container.setLayout(null);
        container.setOpaque(false);
        return container;
    }

    private void setupEventHandlers() {
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setForeground(new Color(220, 50, 50));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(new Color(120, 120, 120));
            }
        });

        usernameField.setBounds(15, 0, 290, 60);
        usernamePlaceholder.setBounds(15, 0, 290, 60);
        usernamePlaceholder.setVisible(usernameField.getText().isEmpty());

        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                usernamePlaceholder.setVisible(false);
            }
            @Override
            public void focusLost(FocusEvent e) {
                usernamePlaceholder.setVisible(usernameField.getText().isEmpty());
            }
        });

        passwordField.setBounds(15, 0, 290, 60);
        passwordPlaceholder.setBounds(15, 0, 290, 60);
        passwordPlaceholder.setVisible(passwordField.getPassword().length == 0);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordPlaceholder.setVisible(false);
            }
            @Override
            public void focusLost(FocusEvent e) {
                passwordPlaceholder.setVisible(passwordField.getPassword().length == 0);
            }
        });

        loginButton.addActionListener(evt -> handleLogin());
        testButton.addActionListener(event -> {
            try {
                MainFrame mainFrame = new MainFrame("USER");
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
                mainFrame.setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.repaint();
            }
        });

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRegisterDialog();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(new Color(20, 120, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(new Color(64, 169, 243));
            }
        });

        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showForgotPasswordDialog();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(20, 120, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(64, 169, 243));
            }
        });

        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "login");
        getRootPane().getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Bắt đầu hiệu ứng đánh máy khi frame hiển thị
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                typingIndex = 0;
                typingForward = true;
                subtitleLabel.setText("");
                typingTimer.restart();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStyledMessage("Please enter both username and password!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mã hóa mật khẩu trước khi so sánh
        String hashedPassword = hashPassword(password);

        String sql = "SELECT t.*, v.TenVaiTro " +
                "FROM TaiKhoanNguoiDung t " +
                "LEFT JOIN VaiTro v ON t.MaVaiTro = v.MaVaiTro " +
                "WHERE t.TenDangNhap = ? AND t.TrangThaiHoatDong = 1";
        try (Connection conn = DatabaseUtils.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("MatKhauHash");
                    String role = rs.getString("TenVaiTro");
                    if (hash != null && hashedPassword.equals(hash)) {
                        showStyledMessage("Login successful!\nWelcome to HR Management System!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        MainFrame mainFrame = new MainFrame(role != null ? role : "USER");
                        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
                        mainFrame.setVisible(true);
                        this.dispose();
                    } else {
                        showStyledMessage("Invalid username or password!\nPlease try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } else {
                    showStyledMessage("Invalid username or password!\nPlease try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            }
        } catch (Exception ex) {
            showStyledMessage("Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Register New Account", true);
        registerDialog.setSize(400, 380);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(null);
        registerDialog.setResizable(false);

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setBounds(30, 20, 340, 30);
        registerDialog.add(titleLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setBounds(30, 70, 100, 25);
        registerDialog.add(usernameLabel);

        JTextField usernameInput = new JTextField();
        usernameInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameInput.setBounds(30, 95, 340, 35);
        registerDialog.add(usernameInput);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setBounds(30, 140, 100, 25);
        registerDialog.add(emailLabel);

        JTextField emailInput = new JTextField();
        emailInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailInput.setBounds(30, 165, 340, 35);
        registerDialog.add(emailInput);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setBounds(30, 210, 100, 25);
        registerDialog.add(passwordLabel);

        JPasswordField passwordInput = new JPasswordField();
        passwordInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordInput.setBounds(30, 235, 340, 35);
        registerDialog.add(passwordInput);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBounds(30, 290, 150, 40);
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(new Color(64, 169, 243));
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(64, 169, 243), 2));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerDialog.add(registerButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBounds(220, 290, 150, 40);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerDialog.add(cancelButton);

        registerButton.addActionListener(e -> {
            String newUsername = usernameInput.getText().trim();
            String newEmail = emailInput.getText().trim();
            String newPassword = new String(passwordInput.getPassword());

            if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                showStyledMessage("Please enter username, email and password!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra username tồn tại chưa
            try (Connection conn = DatabaseUtils.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM TaiKhoanNguoiDung WHERE TenDangNhap = ?")) {
                checkStmt.setString(1, newUsername);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        showStyledMessage("Username already exists!\nPlease choose a different username.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } catch (Exception ex) {
                showStyledMessage("Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Thêm tài khoản mới, hash mật khẩu trước khi lưu
            try (Connection conn = DatabaseUtils.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO TaiKhoanNguoiDung (TenDangNhap, MatKhauHash, EmailXacThuc, MaVaiTro) " +
                                "VALUES (?, ?, ?, (SELECT TOP 1 MaVaiTro FROM VaiTro WHERE TenVaiTro = N'USER'))")) {
                insertStmt.setString(1, newUsername);
                insertStmt.setString(2, hashPassword(newPassword));
                insertStmt.setString(3, newEmail);
                int result = insertStmt.executeUpdate();
                if (result > 0) {
                    showStyledMessage("Registration successful!\nYou can now login with your new account.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    registerDialog.dispose();
                } else {
                    showStyledMessage("Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                showStyledMessage("Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> registerDialog.dispose());

        registerDialog.setVisible(true);
    }

    private void showForgotPasswordDialog() {
        ForgotPasswordFrame forgotPasswordFrame = new ForgotPasswordFrame();
        forgotPasswordFrame.setVisible(true);
    }

    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
