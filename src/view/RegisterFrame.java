package view;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> roleBox;
    private JButton registerButton, cancelButton;

    public RegisterFrame() {
        setTitle("Đăng ký tài khoản mới");
        setSize(400, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailField = new JTextField(20);

        roleBox = new JComboBox<>();
        roleBox.addItem("Nhân viên");
        roleBox.addItem("Quản lý");
        roleBox.addItem("Admin");

        registerButton = new JButton("Đăng ký");
        cancelButton = new JButton("Hủy");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.add(registerButton);
        btnPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnPanel, gbc);

        add(panel);

        // Sự kiện nút Hủy
        cancelButton.addActionListener(e -> dispose());

        // Sự kiện nút Đăng ký
        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // TODO: Thêm validate email, mật khẩu, và lưu dữ liệu vào DB

            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập lại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
    }
}
