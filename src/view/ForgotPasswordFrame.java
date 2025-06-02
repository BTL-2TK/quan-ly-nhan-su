package view;

import utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPasswordFrame extends JFrame {
    private JTextField emailField;
    private JButton submitButton, cancelButton;

    public ForgotPasswordFrame() {
        setTitle("Quên mật khẩu");
        setSize(350, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        emailField = new JTextField(20);
        submitButton = new JButton("Lấy lại mật khẩu");
        cancelButton = new JButton("Hủy");

        // Panel chính dùng GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label nhập email
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nhập email đã đăng ký:"), gbc);

        // Textfield email
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Panel chứa 2 nút
        JPanel btnPanel = new JPanel();
        btnPanel.add(submitButton);
        btnPanel.add(cancelButton);

        // Thêm panel nút vào panel chính
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnPanel, gbc);

        // Thêm panel chính vào JFrame
        add(panel);

        // Thêm sự kiện nút
        submitButton.addActionListener(_ -> handleForgotPassword());
        cancelButton.addActionListener(_ -> dispose());
    }

    private void handleForgotPassword() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập email!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT TenDangNhap FROM TaiKhoanNguoiDung WHERE EmailXacThuc = ?")) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mật khẩu không thể lấy lại, thông báo cho người dùng
                    JOptionPane.showMessageDialog(this,
                        "Không thể hiển thị mật khẩu gốc. Vui lòng liên hệ quản trị viên để được hỗ trợ đặt lại mật khẩu.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Email không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
