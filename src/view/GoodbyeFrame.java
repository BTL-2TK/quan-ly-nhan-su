package view;

import javax.swing.*;
import java.awt.*;

public class GoodbyeFrame extends JFrame {
    public GoodbyeFrame() {
        setTitle("Tạm biệt và cảm ơn!");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel thankLabel = new JLabel("Cảm ơn bạn đã sử dụng hệ thống!");
        thankLabel.setFont(new Font("Arial", Font.BOLD, 18));
        thankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;'>"
                + "Mọi thắc mắc vui lòng liên hệ:<br>"
                + "SĐT: 0123 456 789<br>"
                + "Gmail: nhom5java@gmail.com<br>"
                + "Facebook: fb.com/nhom5java"
                + "</div></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeButton = new JButton("Đóng");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());

        panel.add(Box.createVerticalStrut(30));
        panel.add(thankLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(closeButton);

        add(panel);

        // Hiển thị Frame khi tạo
        setVisible(true);
    }
}
