import view.WelcomeFrame;
import utils.DatabaseUtils;
import javax.swing.*;
import java.sql.Connection;

public class HRM {
    public static void main(String[] args) {
        // Thiết lập giao diện hệ điều hành
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Kiểm tra kết nối database, chỉ cảnh báo, không thoát chương trình
        try (Connection conn = DatabaseUtils.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối tới cơ sở dữ liệu! Một số chức năng sẽ bị hạn chế.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi kết nối database: " + ex.getMessage() + "\nMột số chức năng sẽ bị hạn chế.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }

        // Hiển thị giao diện WelcomeFrame
        SwingUtilities.invokeLater(() -> {
            try {
                new WelcomeFrame().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khởi tạo giao diện: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
