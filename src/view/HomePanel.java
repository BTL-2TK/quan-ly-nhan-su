package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.DatabaseUtils;

public class HomePanel extends JPanel {
    public HomePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Panel thông tin công ty (vẫn giữ dữ liệu mẫu hoặc có thể lấy từ DB nếu muốn)
        JPanel companyInfoPanel = new JPanel(new BorderLayout());
        companyInfoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin công ty"));
        companyInfoPanel.setBackground(Color.WHITE);

        String[] companyColumns = {"Thông tin", "Giá trị"};
        Object[][] companyData = {
            {"Tên công ty", "Công ty TNHH ABC"},
            {"Địa chỉ", "123 Đường Lớn, Quận 1, TP.HCM"},
            {"Số điện thoại", "028 1234 5678"},
            {"Email", "contact@abc.com"},
            {"Ngày thành lập", "01/01/2010"},
            {"Mã số thuế", "0312345678"},
            {"Website", "www.abc.com"}
        };
        DefaultTableModel companyModel = new DefaultTableModel(companyData, companyColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable companyTable = new JTable(companyModel) {
            private int hoveredRow = -1;

            {
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) {
                            hoveredRow = row;
                            repaint();
                        }
                    }
                });
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoveredRow = -1;
                        repaint();
                    }
                });
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (row == hoveredRow) {
                    c.setBackground(new Color(234, 243, 255));
                    c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setCursor(Cursor.getDefaultCursor());
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(new Color(33, 37, 41));
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        };

        companyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        companyTable.setRowHeight(28);
        companyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        companyTable.setShowGrid(false);
        companyTable.setIntercellSpacing(new Dimension(0, 0));
        companyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        companyInfoPanel.add(new JScrollPane(companyTable), BorderLayout.CENTER);

        // Panel người có chức vụ đặc biệt
        JPanel specialRolePanel = new JPanel(new BorderLayout());
        specialRolePanel.setBorder(BorderFactory.createTitledBorder("Những người có chức vụ đặc biệt"));
        specialRolePanel.setBackground(Color.WHITE);

        String[] specialColumns = {"Họ tên", "Chức vụ", "Phòng ban", "Email", "Số điện thoại"};
        DefaultTableModel specialModel = new DefaultTableModel(specialColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable specialTable = new JTable(specialModel) {
            private int hoveredRow = -1;

            {
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) {
                            hoveredRow = row;
                            repaint();
                        }
                    }
                });
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoveredRow = -1;
                        repaint();
                    }
                });
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (row == hoveredRow) {
                    c.setBackground(new Color(234, 243, 255));
                    c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setCursor(Cursor.getDefaultCursor());
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(new Color(33, 37, 41));
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        };

        specialTable.setEnabled(false);
        specialTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        specialTable.setRowHeight(24);
        specialTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        specialTable.setShowGrid(false);
        specialTable.setIntercellSpacing(new Dimension(0, 0));

        specialRolePanel.add(new JScrollPane(specialTable), BorderLayout.CENTER);

        // Thêm các panel vào HomePanel
        add(companyInfoPanel);
        add(Box.createVerticalStrut(20));
        add(specialRolePanel);
        add(Box.createVerticalGlue());

        // Load dữ liệu các vị trí đặc biệt từ database
        loadSpecialEmployees(specialModel);
    }

    private void loadSpecialEmployees(DefaultTableModel model) {
        // Danh sách chức vụ đặc biệt, bạn có thể mở rộng thêm
        String[] specialPositions = {
            "Giám đốc", "Trưởng phòng Nhân sự", "Trưởng phòng Kỹ thuật", "Trưởng phòng Kinh doanh"
        };
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < specialPositions.length; i++) {
            placeholders.append("?");
            if (i < specialPositions.length - 1) placeholders.append(",");
        }
        String sql =
            "SELECT e.FirstName + ' ' + e.LastName AS HoTen, p.PositionName, d.TenPhongBan, e.Email, e.Phone " +
            "FROM Employees e " +
            "LEFT JOIN Departments d ON e.DepartmentId = d.Id " +
            "LEFT JOIN Positions p ON e.PositionId = p.Id " +
            "WHERE p.PositionName IN (" + placeholders + ")";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < specialPositions.length; i++) {
                ps.setString(i + 1, specialPositions[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("HoTen"),
                        rs.getString("PositionName"),
                        rs.getString("TenPhongBan"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                    });
                }
            }
        } catch (Exception ex) {
            // Nếu lỗi, có thể hiển thị thông báo hoặc để bảng rỗng
            ex.printStackTrace();
        }
    }
}
