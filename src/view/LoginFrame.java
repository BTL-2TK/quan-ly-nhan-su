package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DatabaseUtils;  // Đảm bảo package đúng

public class LoginFrame extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton forgotPasswordButton;

    private MainFrame mainFrame; // Tham chiếu MainFrame

    public LoginFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
        cancelButton = new JButton("Thoát");
        forgotPasswordButton = new JButton("Quên mật khẩu?");

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        gbc.gridy = 3;
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.add(forgotPasswordButton);
        add(bottomPanel, gbc);

        loginButton.addActionListener(_ -> attemptLogin());
        cancelButton.addActionListener(_ -> System.exit(0));
        forgotPasswordButton.addActionListener(_ -> {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng liên hệ quản trị viên để lấy lại mật khẩu.",
                    "Quên mật khẩu",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Đặt nút đăng nhập là nút mặc định
        mainFrame.getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT t.*, v.TenVaiTro FROM TaiKhoanNguoiDung t LEFT JOIN VaiTro v ON t.MaVaiTro = v.MaVaiTro WHERE t.TenDangNhap = ? AND t.TrangThaiHoatDong = 1";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("MatKhauHash");
                    // TODO: So sánh mật khẩu đã hash
                    if (hash != null && password.equals(hash)) {
                        JOptionPane.showMessageDialog(this,
                                "Đăng nhập thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Chuyển sang panel HomePanel
                        mainFrame.showPanel("HomePanel");
                    } else {
                        showLoginError();
                    }
                } else {
                    showLoginError();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối database: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoginError() {
        JOptionPane.showMessageDialog(this,
                "Sai tên đăng nhập hoặc mật khẩu!",
                "Lỗi đăng nhập",
                JOptionPane.ERROR_MESSAGE);
        passwordField.setText("");
    }
}
