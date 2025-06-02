package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.text.DecimalFormat;

import utils.DatabaseUtils;

public class ContractPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnSearch, btnRefresh, btnSort, btnExport;

    public ContractPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab "HỢP ĐỒNG" (giữ nguyên)
        JPanel contractTab = createContractListTab();
        tabbedPane.addTab("HỢP ĐỒNG", contractTab);

        // Tab "KÍ HỢP ĐỒNG" (theo giao diện ảnh)
        JPanel signContractTab = createSignContractTab();
        tabbedPane.addTab("KÍ HỢP ĐỒNG", signContractTab);

        // Tab "THỐNG KÊ" (giữ nguyên)
        JPanel statisticTab = createStatisticTab();
        tabbedPane.addTab("THỐNG KÊ", statisticTab);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createContractListTab() {
        JPanel contractTab = new JPanel(null);

        // Thanh tìm kiếm và nút
        tfSearch = new JTextField("Tìm kiếm nhanh...");
        tfSearch.setBounds(220, 15, 260, 32);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contractTab.add(tfSearch);

        btnSearch = new JButton();
        btnSearch.setBounds(485, 15, 36, 32);
        contractTab.add(btnSearch);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setBounds(525, 15, 90, 32);
        contractTab.add(btnRefresh);

        btnSort = new JButton();
        btnSort.setBounds(625, 15, 36, 32);
        contractTab.add(btnSort);

        btnExport = new JButton();
        btnExport.setBounds(665, 15, 36, 32);
        contractTab.add(btnExport);

        // Thêm 2 nút Gia hạn và Hủy hợp đồng
        JButton btnExtend = new JButton("Gia hạn Hợp Đồng");
        btnExtend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExtend.setBounds(760, 15, 150, 32);
        contractTab.add(btnExtend);

        JButton btnCancel = new JButton("Hủy hợp đồng");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBounds(920, 15, 130, 32);
        contractTab.add(btnCancel);

        // Bảng dữ liệu
        String[] columns = {
            "STT", "Mã - Tên nhân viên", "Phòng ban", "Từ ngày", "Đến ngày", "Loại hợp đồng", "Lương cơ bản", "Thời gian còn lại"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model) {
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
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row >= 0) {
                            setRowSelectionInterval(row, row); // Chọn dòng khi click vào bất kỳ ô nào
                        }
                    }
                });
            }
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (row == hoveredRow) {
                    c.setBackground(new Color(234, 243, 255));
                    c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else if (isRowSelected(row)) {
                    c.setBackground(new Color(200, 240, 200));
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
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 60, 0, 0);

        contractTab.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(null);
        topPanel.setPreferredSize(new Dimension(0, 60));
        tfSearch.setBounds(220, 15, 260, 32);
        btnSearch.setBounds(485, 15, 36, 32);
        btnRefresh.setBounds(525, 15, 90, 32);
        btnSort.setBounds(625, 15, 36, 32);
        btnExport.setBounds(665, 15, 36, 32);
        btnExtend.setBounds(760, 15, 150, 32);
        btnCancel.setBounds(920, 15, 130, 32);
        topPanel.add(tfSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnRefresh);
        topPanel.add(btnSort);
        topPanel.add(btnExport);
        topPanel.add(btnExtend);
        topPanel.add(btnCancel);

        contractTab.add(topPanel, BorderLayout.NORTH);
        contractTab.add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu từ database
        loadContractsFromDatabase();

        // Sự kiện Gia hạn hợp đồng
        btnExtend.addActionListener(_ -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để gia hạn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            Object[] info = new Object[model.getColumnCount()];
            for (int i = 0; i < info.length; i++) {
                info[i] = model.getValueAt(modelRow, i);
            }
            showExtendContractDialog(info, modelRow);
        });

        // Sự kiện Hủy hợp đồng (demo)
        btnCancel.addActionListener(_ -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để hủy hợp đồng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String maNV = model.getValueAt(modelRow, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy hợp đồng của " + maNV + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Xóa hợp đồng trong database
                try (Connection conn = DatabaseUtils.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM Contracts WHERE EmployeeCode = ?")) {
                    String code = maNV.split("-")[0].trim();
                    ps.setString(1, code);
                    ps.executeUpdate();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa hợp đồng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                model.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, "Đã hủy hợp đồng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Sự kiện nút Làm mới: cập nhật lại dữ liệu từ database
        btnRefresh.addActionListener(_ -> loadContractsFromDatabase());

        return contractTab;
    }

    private JPanel createSignContractTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Bảng danh sách nhân viên chưa có hợp đồng (bên trái)
        String[] columns = {"STT", "Nhân viên", "Phòng ban", "Thử việc từ"};
        DefaultTableModel probationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable probationTable = new JTable(probationModel);
        probationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        probationTable.setRowHeight(28);

        // Load danh sách nhân viên chưa có hợp đồng từ DB
        loadProbationEmployees(probationModel);

        JScrollPane probationScroll = new JScrollPane(probationTable);
        probationScroll.setPreferredSize(new Dimension(420, 0));

        // Panel thông tin ký hợp đồng (bên phải)
        JPanel infoPanel = new JPanel(null);
        infoPanel.setPreferredSize(new Dimension(600, 0));

        JLabel lblTitle = new JLabel("KÍ HỢP ĐỒNG LAO ĐỘNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBounds(20, 10, 300, 30);
        infoPanel.add(lblTitle);

        // Các label và field thông tin nhân viên
        String[] labels = {
            "Nhân viên:", "Ngày sinh:", "Giới tính:", "Địa chỉ:", "Số điện thoại:", "Email:", "CMND:", "Học vấn:", "Chuyên môn:",
            "Chuyên ngành:", "Phòng ban:", "Chức vụ:", "Mức lương:"
        };
        JLabel[] lbls = new JLabel[labels.length];
        JLabel[] vals = new JLabel[labels.length];
        int y = 50;
        for (int i = 0; i < labels.length; i++) {
            lbls[i] = new JLabel(labels[i]);
            lbls[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbls[i].setBounds(20, y, 120, 24);
            infoPanel.add(lbls[i]);
            vals[i] = new JLabel();
            vals[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vals[i].setBounds(150, y, 400, 24);
            infoPanel.add(vals[i]);
            y += 28;
        }

        // Bổ sung thông tin hợp đồng
        JLabel lblSupplement = new JLabel("BỔ SUNG THÔNG TIN");
        lblSupplement.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSupplement.setBounds(20, y + 10, 300, 24);
        infoPanel.add(lblSupplement);

        JLabel lblStart = new JLabel("Bắt đầu hợp đồng");
        lblStart.setBounds(20, y + 40, 140, 24);
        infoPanel.add(lblStart);
        JTextField tfStart = new JTextField();
        tfStart.setBounds(170, y + 40, 120, 24);
        infoPanel.add(tfStart);

        JLabel lblEnd = new JLabel("Kết thúc hợp đồng");
        lblEnd.setBounds(320, y + 40, 140, 24);
        infoPanel.add(lblEnd);
        JTextField tfEnd = new JTextField();
        tfEnd.setBounds(470, y + 40, 120, 24);
        infoPanel.add(tfEnd);

        JLabel lblTerm = new JLabel("Thời hạn hợp đồng");
        lblTerm.setBounds(20, y + 80, 140, 24);
        infoPanel.add(lblTerm);
        JComboBox<String> cbTerm = new JComboBox<>(new String[]{"1 năm", "2 năm", "Không thời hạn"});
        cbTerm.setBounds(170, y + 80, 120, 24);
        infoPanel.add(cbTerm);

        JButton btnCreate = new JButton("Tạo hợp đồng");
        btnCreate.setBounds(470, y + 80, 120, 32);
        infoPanel.add(btnCreate);

        // Khi chọn nhân viên ở bảng bên trái, tự động điền thông tin
        probationTable.getSelectionModel().addListSelectionListener(_ -> {
            int row = probationTable.getSelectedRow();
            if (row >= 0) {
                String maNV = probationModel.getValueAt(row, 1).toString().split(" - ")[0].trim();
                fillEmployeeInfo(maNV, vals);
                tfStart.setText(java.time.LocalDate.now().toString());
                tfEnd.setText(java.time.LocalDate.now().plusYears(1).toString());
                cbTerm.setSelectedIndex(0);
            }
        });

        // Sự kiện chọn thời hạn hợp đồng tự động cập nhật ngày kết thúc
        cbTerm.addActionListener(_ -> {
            String start = tfStart.getText().trim();
            if (!start.matches("\\d{4}-\\d{2}-\\d{2}")) return;
            java.time.LocalDate startDate = java.time.LocalDate.parse(start);
            int idx = cbTerm.getSelectedIndex();
            if (idx == 0) tfEnd.setText(startDate.plusYears(1).toString());
            else if (idx == 1) tfEnd.setText(startDate.plusYears(2).toString());
            else tfEnd.setText("");
        });

        // Sự kiện đổi ngày bắt đầu tự động cập nhật ngày kết thúc
        tfStart.addActionListener(_ -> cbTerm.getActionListeners()[0].actionPerformed(null));

        // Sự kiện nút "Tạo hợp đồng"
        btnCreate.addActionListener(_ -> {
            int row = probationTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn nhân viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maNV = probationModel.getValueAt(row, 1).toString().split(" - ")[0].trim();
            String start = tfStart.getText().trim();
            String end = tfEnd.getText().trim();
            String term = (String) cbTerm.getSelectedItem();
            if (!start.matches("\\d{4}-\\d{2}-\\d{2}") || (!end.isEmpty() && !end.matches("\\d{4}-\\d{2}-\\d{2}"))) {
                JOptionPane.showMessageDialog(panel, "Ngày không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Lấy EmployeeId
            int employeeId = getEmployeeIdByCode(maNV);
            if (employeeId == -1) {
                JOptionPane.showMessageDialog(panel, "Không tìm thấy nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Lấy mức lương từ info
            String salaryStr = vals[12].getText().replace(",", "").replace(".", "");
            double salary = 0;
            try { salary = Double.parseDouble(salaryStr); } catch (Exception ignore) {}
            // Thêm hợp đồng vào DB
            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Contracts (EmployeeId, EmployeeCode, ContractType, StartDate, EndDate, Salary, Status, Note) VALUES (?, ?, ?, ?, ?, ?, N'Còn hiệu lực', NULL)"
                 )) {
                ps.setInt(1, employeeId);
                ps.setString(2, maNV);
                ps.setString(3, term);
                ps.setDate(4, java.sql.Date.valueOf(start));
                ps.setDate(5, end.isEmpty() ? null : java.sql.Date.valueOf(end));
                ps.setDouble(6, salary);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Tạo hợp đồng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Sau khi tạo hợp đồng, cập nhật lại cả hai tab
                loadProbationEmployees((DefaultTableModel) probationTable.getModel());
                loadContractsFromDatabase();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi tạo hợp đồng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, probationScroll, infoPanel);
        splitPane.setDividerLocation(420);
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // Chỉ lấy nhân viên chưa có hợp đồng (không xuất hiện ở tab HỢP ĐỒNG)
    private void loadProbationEmployees(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT e.EmployeeCode, e.FirstName, e.LastName, d.TenPhongBan, e.HireDate " +
                 "FROM Employees e " +
                 "LEFT JOIN Departments d ON e.DepartmentId = d.Id " +
                 "WHERE e.EmployeeCode NOT IN (SELECT EmployeeCode FROM Contracts)"
             );
             ResultSet rs = ps.executeQuery()) {
            int stt = 1;
            while (rs.next()) {
                String maNV = rs.getString("EmployeeCode");
                String tenNV = rs.getString("FirstName") + " " + rs.getString("LastName");
                String phongBan = rs.getString("TenPhongBan");
                String tuNgay = rs.getString("HireDate");
                model.addRow(new Object[]{
                    String.format("%03d", stt++),
                    maNV + " - " + tenNV,
                    phongBan,
                    tuNgay != null ? tuNgay : ""
                });
            }
        } catch (Exception ex) {
            // Nếu lỗi, bảng sẽ rỗng
        }
    }

    private void fillEmployeeInfo(String employeeCode, JLabel[] vals) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT e.EmployeeCode, e.FirstName, e.LastName, e.BirthDate, e.Gender, e.Address, e.Phone, e.Email, e.IdNumber, " +
                 "d.TenPhongBan, p.PositionName, e.Status " +
                 "FROM Employees e " +
                 "LEFT JOIN Departments d ON e.DepartmentId = d.Id " +
                 "LEFT JOIN Positions p ON e.PositionId = p.Id " +
                 "WHERE e.EmployeeCode = ?"
             )) {
            ps.setString(1, employeeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vals[0].setText(rs.getString("EmployeeCode") + " - " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                    vals[1].setText(rs.getString("BirthDate"));
                    vals[2].setText(rs.getString("Gender"));
                    vals[3].setText(rs.getString("Address"));
                    vals[4].setText(rs.getString("Phone"));
                    vals[5].setText(rs.getString("Email"));
                    vals[6].setText(rs.getString("IdNumber"));
                    vals[7].setText(""); // Học vấn
                    vals[8].setText(""); // Chuyên môn
                    vals[9].setText(""); // Chuyên ngành
                    vals[10].setText(rs.getString("TenPhongBan"));
                    vals[11].setText(rs.getString("PositionName"));
                    vals[12].setText("15,000,000"); // Mức lương mẫu, bạn có thể lấy từ bảng Contracts nếu muốn
                }
            }
        } catch (Exception ex) {
            for (JLabel val : vals) val.setText("");
        }
    }

    private int getEmployeeIdByCode(String employeeCode) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT Id FROM Employees WHERE EmployeeCode = ?")) {
            ps.setString(1, employeeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("Id");
            }
        } catch (Exception ex) {}
        return -1;
    }
    private void loadContractsFromDatabase() {
        model.setRowCount(0);
        try (Connection conn = DatabaseUtils.getConnection()) {
            // Kiểm tra cột EmployeeCode trong Contracts
            try (Statement checkStmt = conn.createStatement()) {
                ResultSet rsCheck = checkStmt.executeQuery(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Contracts' AND COLUMN_NAME = 'EmployeeCode'");
                if (!rsCheck.next()) {
                    JOptionPane.showMessageDialog(this, "Bảng Contracts KHÔNG có cột EmployeeCode!\nHãy kiểm tra lại database.", "Lỗi cấu trúc bảng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rsCheck = checkStmt.executeQuery(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Employees' AND COLUMN_NAME = 'EmployeeCode'");
                if (!rsCheck.next()) {
                    JOptionPane.showMessageDialog(this, "Bảng Employees KHÔNG có cột EmployeeCode!\nHãy kiểm tra lại database.", "Lỗi cấu trúc bảng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT c.Id, c.EmployeeCode, e.FirstName, e.LastName, d.TenPhongBan, c.StartDate, c.EndDate, c.ContractType, c.Salary " +
                     "FROM Contracts c " +
                     "INNER JOIN Employees e ON c.EmployeeCode = e.EmployeeCode " +
                     "LEFT JOIN Departments d ON e.DepartmentId = d.Id")) {
                int stt = 1;
                while (rs.next()) {
                    String maNV = rs.getString("EmployeeCode");
                    String tenNV = rs.getString("FirstName") + " " + rs.getString("LastName");
                    String phongBan = rs.getString("TenPhongBan");
                    String tuNgay = rs.getString("StartDate");
                    String denNgay = rs.getString("EndDate");
                    String loaiHD = rs.getString("ContractType");
                    String luong = rs.getString("Salary");

                    // Tính thời gian còn lại (năm, làm tròn 1 số thập phân)
                    String thoiGianConLai = "";
                    try {
                        LocalDate today = LocalDate.now();
                        LocalDate endDate = LocalDate.parse(denNgay);
                        long days = ChronoUnit.DAYS.between(today, endDate);
                        double years = days / 365.0;
                        if (years > 0) {
                            thoiGianConLai = String.format("%.1f năm", years);
                        } else if (days == 0) {
                            thoiGianConLai = "Hết hạn hôm nay";
                        } else {
                            thoiGianConLai = "Đã hết hạn";
                        }
                    } catch (Exception ex) {
                        thoiGianConLai = "Không xác định";
                    }

                    model.addRow(new Object[]{
                            String.format("%03d", stt++),
                            maNV + " - " + tenNV,
                            phongBan,
                            tuNgay,
                            denNgay,
                            loaiHD,
                            luong,
                            thoiGianConLai
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu hợp đồng: " + ex.getMessage() +
                    "\nKiểm tra lại tên cột 'EmployeeCode' trong bảng Contracts và Employees.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Dialog gia hạn hợp đồng
    private void showExtendContractDialog(Object[] info, int modelRow) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Gia hạn hợp đồng", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(700, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(null);

        // Ảnh đại diện (bên trái)
        JLabel avatarLabel = new JLabel();
        avatarLabel.setBounds(30, 30, 110, 140);
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setIcon(new ImageIcon(new BufferedImage(110, 140, BufferedImage.TYPE_INT_ARGB)));
        panel.add(avatarLabel);

        // Thông tin nhân viên (bên phải)
        String[] labels = {
            "Mã nhân viên", "Họ tên", "Phòng ban", "Mã hợp đồng", "Ngày bắt đầu", "Ngày hết hạn", "Loại hợp đồng", "Mức lương", "Thời gian gia hạn thêm:"
        };

        String maTen = info[1] != null ? info[1].toString() : "";
        String[] maTenArr = maTen.split("-", 2);
        String maNV = maTenArr.length > 0 ? maTenArr[0].trim() : "";
        String tenNV = maTenArr.length > 1 ? maTenArr[1].trim() : "";
        String phongBan = info[2] != null ? info[2].toString() : "";
        String maHD = info[0] != null ? "HD" + info[0].toString() : "";
        String ngayBD = info[3] != null ? info[3].toString() : "";
        String ngayKT = info[4] != null ? info[4].toString() : "";
        String loaiHD = info[5] != null ? info[5].toString() : "";
        String mucLuong = info[6] != null ? info[6].toString() : "";

        String[] values = {
            maNV,
            tenNV,
            phongBan,
            maHD,
            ngayBD,
            ngayKT,
            loaiHD,
            mucLuong
        };

        int labelX = 180, valueX = 320, startY = 30, rowH = 32;
        JLabel[] valueLabels = new JLabel[labels.length - 1];
        for (int i = 0; i < labels.length - 1; i++) {
            JLabel lbl = new JLabel(labels[i] + ":");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lbl.setBounds(labelX, startY + i * rowH, 130, 28);
            panel.add(lbl);

            JLabel val = new JLabel(values[i]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            val.setForeground(new Color(0, 102, 204));
            val.setBounds(valueX, startY + i * rowH, 220, 28);
            panel.add(val);
            valueLabels[i] = val;
        }

        // Trường nhập thời gian gia hạn thêm (năm)
        JLabel lblExtend = new JLabel(labels[labels.length - 1]);
        lblExtend.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblExtend.setBounds(labelX, startY + 8 * rowH, 150, 28);
        panel.add(lblExtend);

        JTextField tfExtend = new JTextField();
        tfExtend.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfExtend.setBounds(valueX, startY + 8 * rowH, 120, 28);
        panel.add(tfExtend);

        // Nút
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBounds(350, startY + 9 * rowH + 10, 90, 36);
        JButton btnSave = new JButton("Gia hạn");
        btnSave.setBounds(460, startY + 9 * rowH + 10, 110, 36);
        btnSave.setBackground(new Color(0, 153, 204));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));
        panel.add(btnCancel);
        panel.add(btnSave);

        btnCancel.addActionListener(_ -> dialog.dispose());
        btnSave.addActionListener(_ -> {
            String extendVal = tfExtend.getText().trim();
            if (extendVal.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập thời gian gia hạn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int extendYears;
            try {
                extendYears = Integer.parseInt(extendVal.replaceAll("\\D", ""));
                if (extendYears <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Thời gian gia hạn phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Cập nhật ngày hết hạn trong database
            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Contracts SET EndDate = DATEADD(year, ?, EndDate) WHERE EmployeeCode = ?")) {
                ps.setInt(1, extendYears);
                ps.setString(2, maNV);
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    // Lấy ngày hết hạn mới từ DB
                    try (PreparedStatement ps2 = conn.prepareStatement(
                            "SELECT EndDate FROM Contracts WHERE EmployeeCode = ?")) {
                        ps2.setString(1, maNV);
                        try (ResultSet rs = ps2.executeQuery()) {
                            if (rs.next()) {
                                String newEndDate = rs.getString("EndDate");
                                // Cập nhật trên giao diện
                                model.setValueAt(newEndDate, modelRow, 4); // Cột "Đến ngày"
                                valueLabels[5].setText(newEndDate);

                                // Tính lại thời gian còn lại (năm)
                                String thoiGianConLai = "";
                                try {
                                    LocalDate today = LocalDate.now();
                                    LocalDate endDate = LocalDate.parse(newEndDate);
                                    long days = ChronoUnit.DAYS.between(today, endDate);
                                    double years = days / 365.0;
                                    if (years > 0) {
                                        thoiGianConLai = String.format("%.1f năm", years);
                                    } else if (days == 0) {
                                        thoiGianConLai = "Hết hạn hôm nay";
                                    } else {
                                        thoiGianConLai = "Đã hết hạn";
                                    }
                                } catch (Exception ex) {
                                    thoiGianConLai = "Không xác định";
                                }
                                model.setValueAt(thoiGianConLai, modelRow, 7); // Cột "Thời gian còn lại"
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(dialog, "Đã gia hạn hợp đồng thêm " + extendYears + " năm!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Không thể gia hạn hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật hợp đồng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JPanel createStatisticTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // --- 1. Lấy dữ liệu thống kê từ DB ---
        java.util.List<Object[]> contractList = new ArrayList<>();
        int countInYear = 0, countExpired = 0;
        int[] typeCounts = new int[5]; // 1-2, 3-5, 6-8, 9-10, >10 năm
        try (Connection conn = DatabaseUtils.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.EmployeeCode, e.FirstName, e.LastName, d.TenPhongBan, c.StartDate, c.EndDate, c.ContractType, c.Salary " +
                    "FROM Contracts c " +
                    "INNER JOIN Employees e ON c.EmployeeCode = e.EmployeeCode " +
                    "LEFT JOIN Departments d ON e.DepartmentId = d.Id")) {
            while (rs.next()) {
                String maNV = rs.getString("EmployeeCode");
                String tenNV = rs.getString("FirstName") + " " + rs.getString("LastName");
                String phongBan = rs.getString("TenPhongBan");
                String tuNgay = rs.getString("StartDate");
                String denNgay = rs.getString("EndDate");
                String loaiHD = rs.getString("ContractType");
                double luong = rs.getDouble("Salary");

                // Tính số năm hợp đồng
                int year = 0;
                try {
                    LocalDate start = LocalDate.parse(tuNgay);
                    LocalDate end = LocalDate.parse(denNgay);
                    year = (int)Math.round(ChronoUnit.DAYS.between(start, end) / 365.0);
                } catch (Exception ignore) {}

                // Phân loại loại hợp đồng
                if (year >= 1 && year <= 2) typeCounts[0]++;
                else if (year >= 3 && year <= 5) typeCounts[1]++;
                else if (year >= 6 && year <= 8) typeCounts[2]++;
                else if (year >= 9 && year <= 10) typeCounts[3]++;
                else if (year > 10) typeCounts[4]++;

                // Hợp đồng hết hạn trong năm
                try {
                    LocalDate end = LocalDate.parse(denNgay);
                    LocalDate now = LocalDate.now();
                    if (end.getYear() == now.getYear()) countExpired++;
                    else countInYear++;
                } catch (Exception ignore) {}

                contractList.add(new Object[]{
                        maNV, tenNV, phongBan, tuNgay, denNgay, year > 0 ? year + " năm" : "", String.format("%,.0f", luong)
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int total = contractList.size();
        int[] pie1 = {countInYear, countExpired};
        int[] pie2 = Arrays.copyOf(typeCounts, 5);

        // --- 2. Biểu đồ tròn ---
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        chartPanel.setBackground(Color.WHITE);

        // Pie 1: Tỉ lệ chênh lệch
        Map<String, Integer> pie1Data = new LinkedHashMap<>();
        pie1Data.put("Hợp đồng đã kí trong năm", countInYear);
        pie1Data.put("Hợp đồng hết hạn trong năm", countExpired);
        Color[] pie1Colors = {new Color(51, 153, 255), new Color(102, 204, 102)};
        chartPanel.add(new PieChartPanel(
                "Tỉ lệ chênh lệch",
                pie1Data,
                pie1Colors,
                new String[]{"Hợp đồng đã kí trong năm", "Hợp đồng hết hạn trong năm"}
        ));

        // Pie 2: Tỉ lệ các loại hợp đồng
        Map<String, Integer> pie2Data = new LinkedHashMap<>();
        pie2Data.put("1 - 2 năm", typeCounts[0]);
        pie2Data.put("3 - 5 năm", typeCounts[1]);
        pie2Data.put("6 - 8 năm", typeCounts[2]);
        pie2Data.put("9 - 10 năm", typeCounts[3]);
        pie2Data.put("trên 10 năm", typeCounts[4]);
        Color[] pie2Colors = {
                new Color(51, 153, 255),
                new Color(102, 204, 102),
                new Color(255, 204, 51),
                new Color(255, 102, 51),
                new Color(255, 51, 51)
        };
        chartPanel.add(new PieChartPanel(
                "Tỉ lệ các loại hợp đồng",
                pie2Data,
                pie2Colors,
                new String[]{"1 - 2 năm", "3 - 5 năm", "6 - 8 năm", "9 - 10 năm", "trên 10 năm"}
        ));

        // --- 3. Bảng thống kê ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        JComboBox<String> cbFilter = new JComboBox<>(new String[]{
                "Tất cả hợp đồng", "Hợp đồng hết hạn trong năm", "Hợp đồng đã kí trong năm"
        });
        JLabel lblCount = new JLabel("Số lượng: " + total);
        filterPanel.add(cbFilter);
        filterPanel.add(lblCount);

        String[] columns = {"STT", "Mã - Tên nhân viên", "Phòng ban", "Từ ngày", "Đến ngày", "Loại hợp đồng", "Lương cơ bản"};
        DefaultTableModel statModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable statTable = new JTable(statModel);
        statTable.setRowHeight(28);
        statTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane statScroll = new JScrollPane(statTable);

        // Hàm fill bảng theo filter
        Runnable fillTable = () -> {
            statModel.setRowCount(0);
            int stt = 1, count = 0;
            for (Object[] row : contractList) {
                String tuNgay = row[3] != null ? row[3].toString() : "";
                String denNgay = row[4] != null ? row[4].toString() : "";
                boolean add = true;
                if (cbFilter.getSelectedIndex() == 1) { // Hết hạn trong năm
                    try {
                        LocalDate end = LocalDate.parse(denNgay);
                        add = end.getYear() == LocalDate.now().getYear();
                    } catch (Exception ex) { add = false; }
                } else if (cbFilter.getSelectedIndex() == 2) { // Đã kí trong năm
                    try {
                        LocalDate start = LocalDate.parse(tuNgay);
                        add = start.getYear() == LocalDate.now().getYear();
                    } catch (Exception ex) { add = false; }
                }
                if (add) {
                    statModel.addRow(new Object[]{
                            stt++,
                            row[0] + " - " + row[1],
                            row[2],
                            row[3],
                            row[4],
                            row[5],
                            row[6]
                    });
                    count++;
                }
            }
            lblCount.setText("Số lượng: " + count);
        };
        fillTable.run();

        cbFilter.addActionListener(_ -> fillTable.run());

        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(statScroll, BorderLayout.CENTER);

        // --- 4. Layout tổng ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(chartPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    // PieChartPanel: Vẽ biểu đồ tròn đơn giản
    static class PieChartPanel extends JPanel {
        private final String title;
        private final Map<String, Integer> data;
        private final Color[] colors;
        private final String[] legends;

        public PieChartPanel(String title, Map<String, Integer> data, Color[] colors, String[] legends) {
            this.title = title;
            this.data = data;
            this.colors = colors;
            this.legends = legends;
            setPreferredSize(new Dimension(400, 260));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            int x = 40, y = 40, w = 140, h = 140;
            int startAngle = 0, i = 0;
            DecimalFormat df = new DecimalFormat("0.#");
            // Draw pie
            for (Integer value : data.values()) {
                int angle = total == 0 ? 0 : (int) Math.round(value * 360.0 / total);
                g.setColor(colors[i % colors.length]);
                g.fillArc(x, y, w, h, startAngle, angle);
                startAngle += angle;
                i++;
            }
            // Draw legend
            int legendY = y + h + 10;
            i = 0;
            for (String _ : data.keySet()) {
                g.setColor(colors[i % colors.length]);
                g.fillRect(x + i * 90, legendY, 16, 16);
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g.drawString(legends[i] + " ", x + 20 + i * 90, legendY + 13);
                i++;
            }
            // Draw percent on pie
            startAngle = 0;
            i = 0;
            for (Integer value : data.values()) {
                int angle = total == 0 ? 0 : (int) Math.round(value * 360.0 / total);
                double percent = total == 0 ? 0 : value * 100.0 / total;
                if (angle > 0) {
                    double theta = Math.toRadians(startAngle + angle / 2.0);
                    int px = x + w / 2 + (int) (w / 2.5 * Math.cos(theta));
                    int py = y + h / 2 - (int) (h / 2.5 * Math.sin(theta));
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    g.drawString(df.format(percent) + "%", px - 15, py + 5);
                }
                startAngle += angle;
                i++;
            }
            // Draw title
            g.setColor(Color.BLACK);
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.drawString(title, x, y - 15);
        }
    }
}
