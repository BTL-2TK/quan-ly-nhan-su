package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import utils.DatabaseUtils;

public class RecruitmentPanel extends JPanel {
    private JComboBox<String> cbSearchType;
    private JTextField tfSearch;
    private JButton btnSearch, btnAdd, btnDelete, btnRecruit;
    private JTable tableApplicants;
    private int hoverRow = -1, hoverCol = -1;
    private JComboBox<String> cbMaTuyenDung;

    private DefaultTableModel model;

    public RecruitmentPanel() {
        setLayout(new BorderLayout());

        // Tạo tabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel Ứng viên
        JPanel applicantPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbMaTuyenDung = new JComboBox<>();
        cbMaTuyenDung.addItem("Tất cả");
        topPanel.add(new JLabel("Mã tuyển dụng:"));
        topPanel.add(cbMaTuyenDung);

        cbSearchType = new JComboBox<>(new String[] {
            "Họ và tên"
        });
        cbSearchType.setVisible(false);
        tfSearch = new JTextField(20);
        tfSearch.setVisible(false);
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setVisible(false);
        topPanel.add(cbSearchType);
        topPanel.add(tfSearch);
        topPanel.add(btnSearch);

        btnAdd = new JButton("Thêm");
        // btnAdd.setIcon(new ImageIcon(...)); // Để trống icon nếu không có ảnh
        topPanel.add(btnAdd);

        btnDelete = new JButton("Xóa");
        // btnDelete.setIcon(new ImageIcon(...)); // Để trống icon nếu không có ảnh
        topPanel.add(btnDelete);

        btnRecruit = new JButton("Tuyển");
        // btnRecruit.setIcon(new ImageIcon(...)); // Để trống icon nếu không có ảnh
        topPanel.add(btnRecruit);

        applicantPanel.add(topPanel, BorderLayout.NORTH);

        // Tạo model và load dữ liệu từ database
        String[] columnNames = {
            "Mã tuyển dụng", "Họ và tên", "Số điện thoại", "Email", "Chức vụ", "Trình độ", "Mức lương Deal", "Trạng thái"
        };
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableApplicants = new JTable(model);
        tableApplicants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set preferred width for "Mã tuyển dụng" column to avoid text cutoff
        TableColumnModel columnModel = tableApplicants.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(140);

        // Custom cell renderer cho hiệu ứng hover và màu trạng thái
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Màu xanh nhạt khi chọn dòng
                if (isSelected) {
                    c.setBackground(new Color(200, 240, 200));
                } else if (row == hoverRow && column == hoverCol) {
                    c.setBackground(new Color(220, 240, 255));
                } else {
                    c.setBackground(Color.WHITE);
                }

                // Màu chữ cho cột "Trạng thái"
                if (column == 7) {
                    String status = value != null ? value.toString() : "";
                    if ("Đã tuyển".equalsIgnoreCase(status.trim())) {
                        c.setForeground(new Color(0, 153, 0)); // xanh lá
                    } else if ("Chưa tuyển".equalsIgnoreCase(status.trim())) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        // Áp dụng renderer cho tất cả cột
        for (int i = 0; i < tableApplicants.getColumnCount(); i++) {
            tableApplicants.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Mouse motion để xác định vị trí hover
        tableApplicants.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tableApplicants.rowAtPoint(e.getPoint());
                int col = tableApplicants.columnAtPoint(e.getPoint());
                if (row != hoverRow || col != hoverCol) {
                    hoverRow = row;
                    hoverCol = col;
                    tableApplicants.repaint();
                }
            }
        });
        // Mouse exited để bỏ hiệu ứng hover khi chuột ra ngoài
        tableApplicants.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                tableApplicants.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableApplicants.rowAtPoint(e.getPoint());
                // Nếu đã chọn rồi thì bỏ chọn khi click lại, còn không thì chọn dòng đó
                if (row == tableApplicants.getSelectedRow()) {
                    tableApplicants.clearSelection();
                } else if (row >= 0) {
                    tableApplicants.setRowSelectionInterval(row, row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableApplicants);
        applicantPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel Tuyển dụng (bạn có thể bổ sung nội dung sau)
        JPanel recruitmentTabPanel = new JPanel(new BorderLayout());
        recruitmentTabPanel.add(new JLabel("Nội dung tab Tuyển dụng", SwingConstants.CENTER), BorderLayout.CENTER);

        // Thêm hai tab vào tabbedPane
        tabbedPane.addTab("Ứng viên", applicantPanel);
        tabbedPane.addTab("Tuyển dụng", recruitmentTabPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Load dữ liệu từ database
        loadApplicantsFromDatabase();

        // Sự kiện chọn mã tuyển dụng để lọc bảng
        cbMaTuyenDung.addActionListener(e -> {
            String selectedMaTD = (String) cbMaTuyenDung.getSelectedItem();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableApplicants.getModel());
            if ("Tất cả".equals(selectedMaTD)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(selectedMaTD) + "$", 0));
            }
            tableApplicants.setRowSorter(sorter);
        });

        // Sự kiện nút Tuyển
        btnRecruit.addActionListener(e -> {
            int row = tableApplicants.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ứng viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = tableApplicants.convertRowIndexToModel(row);
            String status = model.getValueAt(modelRow, 7).toString().trim();
            if (!"Chưa tuyển".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể tuyển ứng viên có trạng thái 'Chưa tuyển'!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object[] info = new Object[tableApplicants.getColumnCount()];
            for (int i = 0; i < info.length; i++) {
                info[i] = model.getValueAt(modelRow, i);
            }
            new CandidateRecruitDialog(SwingUtilities.getWindowAncestor(this), info, modelRow, model, tableApplicants).setVisible(true);
        });

        // Sự kiện nút Xóa
        btnDelete.addActionListener(e -> {
            int row = tableApplicants.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ứng viên để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = tableApplicants.convertRowIndexToModel(row);
            String maTD = model.getValueAt(modelRow, 0).toString();
            String hoTen = model.getValueAt(modelRow, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa ứng viên: " + hoTen + " (Mã tuyển dụng: " + maTD + ")?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Xóa trong database
                try (Connection conn = DatabaseUtils.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "DELETE FROM UngVien WHERE MaTuyenDung = ? AND HoTen = ?")) {
                    pstmt.setString(1, maTD);
                    pstmt.setString(2, hoTen);
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        model.removeRow(modelRow);
                        JOptionPane.showMessageDialog(this, "Đã xóa ứng viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy ứng viên để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa ứng viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadApplicantsFromDatabase() {
        model.setRowCount(0);
        cbMaTuyenDung.removeAllItems();
        cbMaTuyenDung.addItem("Tất cả");
        java.util.Set<String> maTDSet = new java.util.LinkedHashSet<>();
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             // Lỗi ở đây: Bảng 'UngVien' không tồn tại trong database của bạn
             ResultSet rs = stmt.executeQuery("SELECT MaTuyenDung, HoTen, SoDienThoai, Email, ChucVu, TrinhDo, MucLuongDeal, TrangThai FROM UngVien")) {
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("MaTuyenDung");
                row[1] = rs.getString("HoTen");
                row[2] = rs.getString("SoDienThoai");
                row[3] = rs.getString("Email");
                row[4] = rs.getString("ChucVu");
                row[5] = rs.getString("TrinhDo");
                row[6] = rs.getString("MucLuongDeal");
                row[7] = rs.getString("TrangThai");
                model.addRow(row);
                maTDSet.add(rs.getString("MaTuyenDung"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu ứng viên từ database: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        for (String ma : maTDSet) {
            cbMaTuyenDung.addItem(ma);
        }
    }

    // Getter cho các nút nếu cần xử lý sự kiện bên ngoài
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnRecruit() { return btnRecruit; }
    public JButton getBtnSearch() { return btnSearch; }
    public JTextField getTfSearch() { return tfSearch; }
    public JComboBox<String> getCbSearchType() { return cbSearchType; }
    public JTable getTableApplicants() { return tableApplicants; }
}

// Không tạo file mới, chỉ sửa hoặc bổ sung code trong file này!

// Dialog tuyển ứng viên
class CandidateRecruitDialog extends JDialog {
    private Object[] info;
    private int modelRow;
    private DefaultTableModel model;
    private JTable table;
    public CandidateRecruitDialog(Window owner, Object[] info, int modelRow, DefaultTableModel model, JTable table) {
        super(owner, "Tuyển ứng viên", ModalityType.APPLICATION_MODAL);
        this.info = info;
        this.modelRow = modelRow;
        this.model = model;
        this.table = table;
        setSize(900, 400);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thông tin ứng viên (bên trên)
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin ứng viên"));

        // Lưới 3 cột cho thông tin ứng viên
        JPanel gridPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cột 1
        gbc.gridx = 0; gbc.gridy = 0;
        gridPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel(getValue(info, 1)), gbc);

        gbc.gridx = 0; gbc.gridy++;
        gridPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel("Nam"), gbc);

        gbc.gridx = 0; gbc.gridy++;
        gridPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel("09-04-2000"), gbc);

        gbc.gridx = 0; gbc.gridy++;
        gridPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel(getValue(info, 2)), gbc);

        gbc.gridx = 0; gbc.gridy++;
        gridPanel.add(new JLabel("Trình độ học vấn:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel(getValue(info, 5)), gbc);

        gbc.gridx = 0; gbc.gridy++;
        gridPanel.add(new JLabel("Chuyên môn:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel("Cử nhân"), gbc);

        gbc.gridx = 0; gbc.gridy++;
        gridPanel.add(new JLabel("Chuyên ngành:"), gbc);
        gbc.gridx = 1;
        gridPanel.add(new JLabel("Kế toán"), gbc);

        // Cột 2
        gbc.gridx = 2; gbc.gridy = 0;
        gridPanel.add(new JLabel("Số nhà:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("TDUV12"), gbc);

        gbc.gridx = 2; gbc.gridy++;
        gridPanel.add(new JLabel("Đường:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("Khóm 1"), gbc);

        gbc.gridx = 2; gbc.gridy++;
        gridPanel.add(new JLabel("Phường,xã:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("An Phú"), gbc);

        gbc.gridx = 2; gbc.gridy++;
        gridPanel.add(new JLabel("Quận,huyện:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("An Phú"), gbc);

        gbc.gridx = 2; gbc.gridy++;
        gridPanel.add(new JLabel("Tỉnh,TP:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("An Giang"), gbc);

        gbc.gridx = 2; gbc.gridy++;
        gridPanel.add(new JLabel("Dân tộc:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("Không"), gbc);

        gbc.gridx = 2; gbc.gridy++;
        gridPanel.add(new JLabel("Tôn giáo:"), gbc);
        gbc.gridx = 3;
        gridPanel.add(new JLabel("Không"), gbc);

        // Cột 3
        gbc.gridx = 4; gbc.gridy = 0;
        gridPanel.add(new JLabel("Số CMND:"), gbc);
        gbc.gridx = 5;
        gridPanel.add(new JLabel("001145676"), gbc);

        gbc.gridx = 4; gbc.gridy++;
        gridPanel.add(new JLabel("Ngày cấp:"), gbc);
        gbc.gridx = 5;
        gridPanel.add(new JLabel("09-05-2023"), gbc);

        gbc.gridx = 4; gbc.gridy++;
        gridPanel.add(new JLabel("Nơi cấp:"), gbc);
        gbc.gridx = 5;
        gridPanel.add(new JLabel("An Giang"), gbc);

        gbc.gridx = 4; gbc.gridy++;
        gridPanel.add(new JLabel("Hôn nhân:"), gbc);
        gbc.gridx = 5;
        gridPanel.add(new JLabel("Độc thân"), gbc);

        gbc.gridx = 4; gbc.gridy++;
        gridPanel.add(new JLabel("Chức vụ ứng tuyển:"), gbc);
        gbc.gridx = 5;
        gridPanel.add(new JLabel(getValue(info, 4)), gbc);

        gbc.gridx = 4; gbc.gridy++;
        gridPanel.add(new JLabel("Mức lương deal:"), gbc);
        gbc.gridx = 5;
        gridPanel.add(new JLabel(getValue(info, 6)), gbc);

        // Thêm lưới vào infoPanel
        infoPanel.add(gridPanel, BorderLayout.CENTER);

        // Bổ sung thông tin (bên dưới)
        JPanel supplementPanel = new JPanel(new GridBagLayout());
        supplementPanel.setBorder(BorderFactory.createTitledBorder("Bổ sung thông tin"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 10, 5, 10);
        gbc2.anchor = GridBagConstraints.WEST;

        // Hình thức tuyển
        gbc2.gridx = 0; gbc2.gridy = 0;
        supplementPanel.add(new JLabel("Hình thức tuyển"), gbc2);
        gbc2.gridx = 1;
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Nhân viên chính thức", "Thử việc", "Thời vụ"});
        cbType.setPreferredSize(new Dimension(160, 25));
        supplementPanel.add(cbType, gbc2);

        // Mã nhân viên (có đề xuất)
        gbc2.gridx = 2;
        supplementPanel.add(new JLabel("Mã nhân viên"), gbc2);
        gbc2.gridx = 3;
        JTextField tfMaNV = new JTextField(10);
        // Đề xuất mã nhân viên nhỏ nhất chưa gán
        String suggestedMaNV = suggestSmallestUnusedEmployeeCode();
        if (suggestedMaNV != null) tfMaNV.setText(suggestedMaNV);
        supplementPanel.add(tfMaNV, gbc2);

        // Phòng ban
        gbc2.gridx = 4;
        supplementPanel.add(new JLabel("Phòng ban"), gbc2);
        gbc2.gridx = 5;
        JComboBox<String> cbPhongBan = new JComboBox<>(new String[]{"Phòng kĩ thuật công nghệ", "Phòng nhân sự", "Phòng kế toán"});
        cbPhongBan.setPreferredSize(new Dimension(180, 25));
        supplementPanel.add(cbPhongBan, gbc2);

        // Bắt đầu làm việc
        gbc2.gridx = 0; gbc2.gridy = 1;
        supplementPanel.add(new JLabel("Bắt đầu làm việc"), gbc2);
        gbc2.gridx = 1;
        JTextField tfStart = new JTextField("24-10-2023", 10);
        supplementPanel.add(tfStart, gbc2);

        // Thời hạn
        gbc2.gridx = 2;
        supplementPanel.add(new JLabel("Thời hạn"), gbc2);
        gbc2.gridx = 3;
        JComboBox<String> cbThoiHan = new JComboBox<>(new String[]{"1 năm", "2 năm", "Không thời hạn"});
        cbThoiHan.setPreferredSize(new Dimension(120, 25));
        supplementPanel.add(cbThoiHan, gbc2);

        // Nút Hủy và Lưu
        JPanel btnPanel = new JPanel();
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(0, 200, 0)); // Xanh lá cây tươi
        btnSave.setOpaque(true);
        btnSave.setBorderPainted(false);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD)); // Chữ đậm
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (tfMaNV.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                tfMaNV.requestFocus();
                return;
            }
            // Cập nhật trạng thái trong database
            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE UngVien SET TrangThai = N'Đã tuyển' WHERE MaTuyenDung = ? AND HoTen = ?")) {
                pstmt.setString(1, info[0].toString());
                pstmt.setString(2, info[1].toString());
                pstmt.executeUpdate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái ứng viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Cập nhật trên model (giao diện)
            model.setValueAt("Đã tuyển", modelRow, 7);
            model.fireTableRowsUpdated(modelRow, modelRow);
            table.repaint();
            JOptionPane.showMessageDialog(this, "Đã lưu thông tin tuyển dụng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        // Layout tổng
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(supplementPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private String getValue(Object[] info, int idx) {
        return info != null && idx < info.length && info[idx] != null ? info[idx].toString() : "";
    }

    // Hàm đề xuất mã nhân viên nhỏ nhất chưa gán
    private String suggestSmallestUnusedEmployeeCode() {
        try (Connection conn = utils.DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             // Lấy tất cả mã nhân viên đã dùng (EmployeeCode) dạng số
             ResultSet rs = stmt.executeQuery("SELECT EmployeeCode FROM Employees")) {
            java.util.TreeSet<Integer> usedCodes = new java.util.TreeSet<>();
            while (rs.next()) {
                String code = rs.getString(1);
                if (code != null) {
                    try {
                        int num = Integer.parseInt(code.replaceAll("\\D", ""));
                        usedCodes.add(num);
                    } catch (NumberFormatException ignore) {}
                }
            }
            // Tìm số nhỏ nhất chưa dùng, bắt đầu từ 1
            int candidate = 1;
            for (int code : usedCodes) {
                if (code == candidate) {
                    candidate++;
                } else if (code > candidate) {
                    break;
                }
            }
            // Đề xuất dạng 3 số, ví dụ "001"
            return String.format("%03d", candidate);
        } catch (Exception ex) {
            // Nếu lỗi, không đề xuất gì
            return "";
        }
    }
}
