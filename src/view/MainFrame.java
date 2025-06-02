package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame(String role) {
        setTitle("Quản lý nhân sự - Vai trò: " + role);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // CardLayout để chuyển đổi giữa các màn hình
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Sử dụng panel chức năng thực tế
        HomePanel homePanel = new HomePanel();
        EmployeePanel employeePanel = new EmployeePanel();
        RecruitmentPanel recruitmentPanel = new RecruitmentPanel();
        ContractPanel contractPanel = new ContractPanel(); // Thêm panel hợp đồng
        AttendancePanel attendancePanel = new AttendancePanel();

        mainPanel.add(homePanel, "HomePanel");
        mainPanel.add(employeePanel, "EmployeePanel");
        mainPanel.add(recruitmentPanel, "RecruitmentPanel");
        mainPanel.add(contractPanel, "ContractPanel"); // Thêm vào CardLayout
        mainPanel.add(attendancePanel, "AttendancePanel");

        // Sidebar chức năng
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 247, 250));
        sidebar.setPreferredSize(new Dimension(140, 0));

        JButton btnHome = new JButton("Trang chủ");
        JButton btnEmployee = new JButton("Nhân viên");
        JButton btnRecruitment = new JButton("Tuyển dụng");
        JButton btnContract = new JButton("Hợp đồng"); // Thêm nút Hợp đồng
        JButton btnAttendance = new JButton("Chấm công");

        btnHome.setFocusPainted(false);
        btnEmployee.setFocusPainted(false);
        btnRecruitment.setFocusPainted(false);
        btnContract.setFocusPainted(false);
        btnAttendance.setFocusPainted(false);

        btnHome.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnEmployee.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRecruitment.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnContract.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAttendance.setFont(new Font("Segoe UI", Font.BOLD, 15));

        btnHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEmployee.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRecruitment.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContract.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAttendance.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnHome.setMaximumSize(new Dimension(120, 40));
        btnEmployee.setMaximumSize(new Dimension(120, 40));
        btnRecruitment.setMaximumSize(new Dimension(120, 40));
        btnContract.setMaximumSize(new Dimension(120, 40));
        btnAttendance.setMaximumSize(new Dimension(120, 40));

        sidebar.add(Box.createVerticalStrut(40));
        sidebar.add(btnHome);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnEmployee);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnRecruitment);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnContract); // Thêm vào sidebar
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnAttendance);
        sidebar.add(Box.createVerticalGlue());

        // Sự kiện chuyển panel
        btnHome.addActionListener(_ -> cardLayout.show(mainPanel, "HomePanel"));
        btnEmployee.addActionListener(_ -> cardLayout.show(mainPanel, "EmployeePanel"));
        btnRecruitment.addActionListener(_ -> cardLayout.show(mainPanel, "RecruitmentPanel"));
        btnContract.addActionListener(_ -> cardLayout.show(mainPanel, "ContractPanel")); // Sự kiện nút Hợp đồng
        btnAttendance.addActionListener(_ -> cardLayout.show(mainPanel, "AttendancePanel"));

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Mặc định hiển thị HomePanel
        cardLayout.show(mainPanel, "HomePanel");

        // Ví dụ phân quyền đơn giản, bạn thay đổi logic theo nhu cầu
        if ("ADMIN".equalsIgnoreCase(role)) {
            // Cho ADMIN truy cập tất cả
            System.out.println("Role ADMIN: truy cập toàn bộ chức năng.");
        } else if ("HR_MANAGER".equalsIgnoreCase(role)) {
            // HR_MANAGER chỉ truy cập tuyển dụng và nhân viên
            System.out.println("Role HR_MANAGER: truy cập Tuyển dụng và Nhân viên.");
            // Bạn có thể ẩn hoặc disable các panel không cần thiết ở đây
        } else {
            // USER hoặc role khác
            System.out.println("Role USER hoặc khác: truy cập giới hạn.");
            // Ví dụ chỉ hiện trang Home
            cardLayout.show(mainPanel, "HomePanel");
        }
    }

    // Hàm để chuyển đổi panel
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame("ADMIN");
            frame.setVisible(true);
        });
    }
}
