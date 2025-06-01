package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;

import utils.DatabaseUtils;

public class ContractPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnSearch, btnRefresh, btnSort, btnExport;

    public ContractPanel() {
        setLayout(new BorderLayout());

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab "HỢP ĐỒNG"
        JPanel contractTab = new JPanel(null);

        // Thanh tìm kiếm và nút
        tfSearch = new JTextField("Tìm kiếm nhanh...");
        tfSearch.setBounds(220, 15, 260, 32);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contractTab.add(tfSearch);

        btnSearch = new JButton();
        btnSearch.setBounds(485, 15, 36, 32);
        contractTab.add(btnSearch);

        btnRefresh = new JButton();
        btnRefresh.setBounds(525, 15, 36, 32);
        contractTab.add(btnRefresh);

        btnSort = new JButton();
        btnSort.setBounds(565, 15, 36, 32);
        contractTab.add(btnSort);

        btnExport = new JButton();
        btnExport.setBounds(605, 15, 36, 32);
        contractTab.add(btnExport);

        // Thêm 2 nút Gia hạn và Hủy hợp đồng
        JButton btnExtend = new JButton("Gia hạn Hợp Đồng");
        btnExtend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExtend.setBounds(700, 15, 150, 32);
        contractTab.add(btnExtend);

        JButton btnCancel = new JButton("Hủy hợp đồng");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBounds(860, 15, 130, 32);
        contractTab.add(btnCancel);

        // Bảng dữ liệu
        String[] columns = {
            "STT", "Mã - Tên nhân viên", "Phòng ban", "Từ ngày", "Đến ngày", "Loại hợp đồng", "Lương cơ bản"
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
        btnRefresh.setBounds(525, 15, 36, 32);
        btnSort.setBounds(565, 15, 36, 32);
        btnExport.setBounds(605, 15, 36, 32);
        btnExtend.setBounds(700, 15, 150, 32);
        btnCancel.setBounds(860, 15, 130, 32);
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
        btnExtend.addActionListener(e -> {
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
        btnCancel.addActionListener(e -> {
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

        // Thêm tab vào tabbedPane
        tabbedPane.addTab("HỢP ĐỒNG", contractTab);
        tabbedPane.addTab("KÍ HỢP ĐỒNG", new JPanel());
        tabbedPane.addTab("THỐNG KÊ", new JPanel());

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadContractsFromDatabase() {
        model.setRowCount(0);
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT c.Id, e.EmployeeCode, e.FirstName, e.LastName, d.TenPhongBan, c.StartDate, c.EndDate, c.ContractType, c.BaseSalary " +
                     "FROM Contracts c " +
                     "JOIN Employees e ON c.EmployeeCode = e.EmployeeCode " +
                     "LEFT JOIN Departments d ON e.DepartmentId = d.Id")) {
            int stt = 1;
            while (rs.next()) {
                String maNV = rs.getString("EmployeeCode");
                String tenNV = rs.getString("FirstName") + " " + rs.getString("LastName");
                String phongBan = rs.getString("TenPhongBan");
                String tuNgay = rs.getString("StartDate");
                String denNgay = rs.getString("EndDate");
                String loaiHD = rs.getString("ContractType");
                String luong = rs.getString("BaseSalary");
                model.addRow(new Object[]{
                        String.format("%03d", stt++),
                        maNV + " - " + tenNV,
                        phongBan,
                        tuNgay,
                        denNgay,
                        loaiHD,
                        luong
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu hợp đồng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
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
                                model.setValueAt(newEndDate, modelRow, 4);
                                valueLabels[5].setText(newEndDate);
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
}
