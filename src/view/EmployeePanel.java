package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.DatabaseUtils;

public class EmployeePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public EmployeePanel() {
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Danh sách nhân viên");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Bộ lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        String[] departments = {
            "Phòng ban",
            "Phòng kĩ thuật công nghệ",
            "Phòng kinh doanh",
            "Phòng nghiên cứu và phát triển",
            "Phòng nhân sự",
            "Phòng tài chính kế toán",
            "Phòng truyền thông marketing",
            "Phòng tuyển dụng"
        };
        JComboBox<String> cbDepartment = new JComboBox<>(departments);
        cbDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbDepartment.setPreferredSize(new Dimension(270, 32));
        filterPanel.add(cbDepartment);

        // Thêm bộ lọc giới tính: Nam, Nữ
        String[] genders = {"Giới tính", "Nam", "Nữ"};
        JComboBox<String> cbGender = new JComboBox<>(genders);
        cbGender.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbGender.setPreferredSize(new Dimension(120, 32));
        filterPanel.add(cbGender);

        // Thêm bộ lọc độ tuổi như ảnh
        String[] ages = {"Độ tuổi", "16 - 25", "26 - 40", "41 - 55", "56 - 65"};
        JComboBox<String> cbAge = new JComboBox<>(ages);
        cbAge.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbAge.setPreferredSize(new Dimension(120, 32));
        filterPanel.add(cbAge);

        // Thêm bộ lọc loại hình như ảnh
        String[] types = {"Loại hình", "Chính thức", "Thử việc"};
        JComboBox<String> cbType = new JComboBox<>(types);
        cbType.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbType.setPreferredSize(new Dimension(120, 32));
        filterPanel.add(cbType);

        // Thêm bộ lọc mức lương như ảnh
        String[] salaryRanges = {"Mức lương", "10M - 20M", "20M - 30M", "30M - 40M"};
        JComboBox<String> cbSalary = new JComboBox<>(salaryRanges);
        cbSalary.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbSalary.setPreferredSize(new Dimension(120, 32));
        filterPanel.add(cbSalary);

        // Thêm bộ lọc sắp xếp như ảnh
        String[] sortFields = {
            "Sắp xếp theo: Mã nhân viên",
            "Sắp xếp theo: Tên nhân viên",
            "Sắp xếp theo: Độ tuổi",
            "Sắp xếp theo: Mức lương"
        };
        JComboBox<String> cbSortField = new JComboBox<>(sortFields);
        cbSortField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbSortField.setPreferredSize(new Dimension(180, 32));
        filterPanel.add(cbSortField);

        String[] sortOrders = {"Tăng dần", "Giảm dần"};
        JComboBox<String> cbSortOrder = new JComboBox<>(sortOrders);
        cbSortOrder.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbSortOrder.setPreferredSize(new Dimension(110, 32));
        filterPanel.add(cbSortOrder);

        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JButton(new ImageIcon())); // icon tìm kiếm
        filterPanel.add(new JButton(new ImageIcon())); // icon tải xuống
        JButton btnAdd = new JButton("+ Thêm");
        btnAdd.setBackground(new Color(64, 169, 243));
        btnAdd.setForeground(Color.BLACK); // Đổi màu chữ thành đen
        btnAdd.setFocusPainted(false);
        filterPanel.add(btnAdd);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setBackground(new Color(220, 220, 220));
        btnRefresh.setFocusPainted(false);
        filterPanel.add(btnRefresh);

        // Sự kiện nút Thêm: mở dialog nhập hồ sơ nhân viên (ngay trong file này)
        btnAdd.addActionListener(_ -> {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(EmployeePanel.this), "Thêm hồ sơ nhân viên", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(1050, 600);
            dialog.setLocationRelativeTo(EmployeePanel.this);
            dialog.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            JLabel title = new JLabel("THÊM HỒ SƠ NHÂN VIÊN");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            mainPanel.add(title, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Ảnh đại diện
            JPanel avatarPanel = new JPanel();
            avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
            avatarPanel.setBackground(Color.WHITE);
            JLabel avatarLabel = new JLabel();
            avatarLabel.setPreferredSize(new Dimension(120, 150));
            avatarLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLabel.setIcon(new ImageIcon(new BufferedImage(120, 150, BufferedImage.TYPE_INT_ARGB)));
            JButton btnChangeAvatar = new JButton("Thay ảnh");
            btnChangeAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
            avatarPanel.add(Box.createVerticalStrut(10));
            avatarPanel.add(avatarLabel);
            avatarPanel.add(Box.createVerticalStrut(10));
            avatarPanel.add(btnChangeAvatar);

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 8;
            formPanel.add(avatarPanel, gbc);

            // Các trường nhập liệu (chỉ minh họa, bạn có thể bổ sung validate và sự kiện)
            String[] labels = {
                "Mã nhân viên", "Họ tên", "Giới tính", "Ngày sinh", "Số điện thoại", "Email", "Tỉnh, thành phố", "Quận, huyện",
                "Phường, xã", "Đường, ấp", "Số nhà", "Dân tộc", "Tôn giáo", "Số CMND", "Ngày cấp", "Nơi cấp",
                "Phòng ban", "Chức vụ", "Ngày nhận chức", "Trình độ học vấn", "Trình độ chuyên môn", "Chuyên ngành",
                "Loại hình làm việc", "Ngày bắt đầu làm việc", "Thời hạn hợp đồng", "Tình trạng hôn nhân", "Mức lương"
            };
            JTextField[] fields = new JTextField[labels.length];
            int col = 1, row = 0;
            for (int i = 0; i < labels.length; i++) {
                gbc.gridx = col; gbc.gridy = row; gbc.gridheight = 1; gbc.weightx = 0;
                formPanel.add(new JLabel(labels[i]), gbc);
                gbc.gridx = col + 1; gbc.weightx = 1;
                fields[i] = new JTextField(15);
                formPanel.add(fields[i], gbc);
                row++;
                if (row == 8) { row = 0; col += 2; }
            }

            // Nút Reset và Thêm
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
            JButton btnReset = new JButton("Reset");
            JButton btnSave = new JButton("Thêm");
            btnSave.setBackground(new Color(0, 200, 0));
            btnSave.setForeground(Color.WHITE);
            btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));
            btnPanel.add(btnReset);
            btnPanel.add(btnSave);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(btnPanel, BorderLayout.SOUTH);

            dialog.setContentPane(mainPanel);

            // Sự kiện nút Thay ảnh (chỉ demo, chưa lưu ảnh)
            btnChangeAvatar.addActionListener(_ -> {
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(dialog);
                if (res == JFileChooser.APPROVE_OPTION) {
                    ImageIcon icon = new ImageIcon(chooser.getSelectedFile().getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(120, 150, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                }
            });

            // Sự kiện nút Reset
            btnReset.addActionListener(_ -> {
                for (JTextField field : fields) field.setText("");
                avatarLabel.setIcon(new ImageIcon(new BufferedImage(120, 150, BufferedImage.TYPE_INT_ARGB)));
            });

            // Sự kiện nút Thêm (bạn tự xử lý lưu vào DB)
            btnSave.addActionListener(_ -> {
                JOptionPane.showMessageDialog(dialog, "Đã thêm nhân viên (demo)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            });

            dialog.setVisible(true);
        });

        // Dữ liệu mẫu
        String[] columns = {
            "STT", "Ảnh", "Nhân viên", "Giới tính", "Ngày sinh", "Địa chỉ", "Liên hệ", "Phòng ban", "Chức vụ", "Mức lương"
        };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Icon.class;
                return String.class;
            }
        };
        table = new JTable(model);
        table.setRowHeight(54);
        table.getColumnModel().getColumn(1).setPreferredWidth(40); // Ảnh
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Nhân viên

        // Ẩn header dọc
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);

        // Không thêm dữ liệu mẫu vào model ở đây!
        // Icon avatarIcon = new ImageIcon(new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB));
        // for (int i = 0; i < table.getRowCount(); i++) {
        //     table.setValueAt(avatarIcon, i, 1);
        // }

        // Bộ lọc phòng ban, giới tính, độ tuổi, loại hình, mức lương, sắp xếp
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Bộ lọc phòng ban, giới tính, độ tuổi, loại hình, mức lương
        cbDepartment.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });
        cbGender.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });
        cbAge.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });
        cbType.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });
        cbSalary.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });

        // Chỉ khi chọn cả trường sắp xếp và thứ tự thì mới sắp xếp
        cbSortField.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });
        cbSortOrder.addActionListener(_ -> {
            applyFiltersAndSort(sorter, cbDepartment, cbGender, cbAge, cbType, cbSalary, cbSortField, cbSortOrder);
        });

        // ScrollPane cho bảng
        JScrollPane scrollPane = new JScrollPane(table);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu từ database
        loadEmployeesFromDatabase();
    }

    private void loadEmployeesFromDatabase() {
        model.setRowCount(0);
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT e.EmployeeCode, e.FirstName, e.LastName, e.Gender, e.BirthDate, e.Address, e.Phone, d.TenPhongBan, p.PositionName, e.Status, c.Salary " +
                 "FROM Employees e " +
                 "INNER JOIN Contracts c ON e.EmployeeCode = c.EmployeeCode " +
                 "LEFT JOIN Departments d ON e.DepartmentId = d.Id " +
                 "LEFT JOIN Positions p ON e.PositionId = p.Id"
             );
             ResultSet rs = ps.executeQuery()) {
            int stt = 1;
            while (rs.next()) {
                String maNV = rs.getString("EmployeeCode");
                String tenNV = rs.getString("FirstName") + " " + rs.getString("LastName");
                String gioiTinh = rs.getString("Gender");
                String ngaySinh = rs.getString("BirthDate");
                String diaChi = rs.getString("Address");
                String lienHe = rs.getString("Phone");
                String phongBan = rs.getString("TenPhongBan");
                String chucVu = rs.getString("PositionName");
                String loaiHinh = rs.getString("Status");
                String mucLuong = rs.getString("Salary") != null ? String.format("%,.0f", rs.getDouble("Salary")) : "";
                Icon avatarIcon = new ImageIcon(new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB));
                model.addRow(new Object[]{
                    String.format("%03d", stt++),
                    avatarIcon,
                    "<html><b>" + maNV + " - " + tenNV + "</b><br>Nhân viên " + (loaiHinh != null ? loaiHinh : "") + "</html>",
                    gioiTinh,
                    ngaySinh,
                    diaChi,
                    lienHe,
                    phongBan,
                    chucVu,
                    mucLuong
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm lọc và sắp xếp kết hợp
    private void applyFiltersAndSort(
            TableRowSorter<TableModel> sorter,
            JComboBox<String> cbDepartment,
            JComboBox<String> cbGender,
            JComboBox<String> cbAge,
            JComboBox<String> cbType,
            JComboBox<String> cbSalary,
            JComboBox<String> cbSortField,
            JComboBox<String> cbSortOrder
    ) {
        String selectedDept = (String) cbDepartment.getSelectedItem();
        String selectedGender = (String) cbGender.getSelectedItem();
        String selectedAge = (String) cbAge.getSelectedItem();
        String selectedType = (String) cbType.getSelectedItem();
        String selectedSalary = (String) cbSalary.getSelectedItem();
        String selectedSortField = (String) cbSortField.getSelectedItem();
        String selectedSortOrder = (String) cbSortOrder.getSelectedItem();

        RowFilter<TableModel, Object> deptFilter = null;
        RowFilter<TableModel, Object> genderFilter = null;
        RowFilter<TableModel, Object> ageFilter = null;
        RowFilter<TableModel, Object> typeFilter = null;
        RowFilter<TableModel, Object> salaryFilter = null;

        if (selectedDept != null && !selectedDept.equals("Phòng ban")) {
            deptFilter = RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(selectedDept) + "$", 7);
        }
        if (selectedGender != null && !selectedGender.equals("Giới tính")) {
            genderFilter = RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(selectedGender) + "$", 3);
        }
        if (selectedAge != null && !selectedAge.equals("Độ tuổi")) {
            ageFilter = new RowFilter<TableModel, Object>() {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Object> entry) {
                    String birth = (String) entry.getValue(4);
                    if (birth == null || birth.isEmpty()) return false;
                    try {
                        int year = Integer.parseInt(birth.substring(0, 4));
                        int age = java.time.LocalDate.now().getYear() - year;
                        switch (selectedAge) {
                            case "16 - 25": return age >= 16 && age <= 25;
                            case "26 - 40": return age >= 26 && age <= 40;
                            case "41 - 55": return age >= 41 && age <= 55;
                            case "56 - 65": return age >= 56 && age <= 65;
                            default: return true;
                        }
                    } catch (Exception ex) {
                        return false;
                    }
                }
            };
        }
        if (selectedType != null && !selectedType.equals("Loại hình")) {
            typeFilter = new RowFilter<TableModel, Object>() {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Object> entry) {
                    String value = (String) entry.getValue(2);
                    if (value == null) return false;
                    if (selectedType.equals("Chính thức")) {
                        return value.contains("Nhân viên chính thức");
                    } else if (selectedType.equals("Thử việc")) {
                        return value.contains("Nhân viên thử việc");
                    }
                    return false;
                }
            };
        }
        if (selectedSalary != null && !selectedSalary.equals("Mức lương")) {
            salaryFilter = new RowFilter<TableModel, Object>() {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Object> entry) {
                    String salaryStr = (String) entry.getValue(9);
                    if (salaryStr == null || salaryStr.isEmpty()) return false;
                    try {
                        int salary = Integer.parseInt(salaryStr.replace(",", "").replace(".", ""));
                        switch (selectedSalary) {
                            case "10M - 20M": return salary >= 10_000_000 && salary <= 20_000_000;
                            case "20M - 30M": return salary > 20_000_000 && salary <= 30_000_000;
                            case "30M - 40M": return salary > 30_000_000 && salary <= 40_000_000;
                            default: return true;
                        }
                    } catch (Exception ex) {
                        return false;
                    }
                }
            };
        }

        java.util.List<RowFilter<TableModel, Object>> filters = new java.util.ArrayList<>();
        if (deptFilter != null) filters.add(deptFilter);
        if (genderFilter != null) filters.add(genderFilter);
        if (ageFilter != null) filters.add(ageFilter);
        if (typeFilter != null) filters.add(typeFilter);
        if (salaryFilter != null) filters.add(salaryFilter);

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        // Sắp xếp: chỉ thực hiện khi đã chọn cả trường sắp xếp và thứ tự
        if (selectedSortField != null && selectedSortOrder != null) {
            int col = 0;
            boolean ascending = selectedSortOrder.equals("Tăng dần");
            switch (selectedSortField) {
                case "Sắp xếp theo: Mã nhân viên":
                    col = 2; // "Nhân viên" (dạng "<b>002 - ...</b>")
                    sorter.setComparator(col, (o1, o2) -> {
                        // So sánh theo mã số ở đầu chuỗi
                        String s1 = o1.toString().replaceAll("<[^>]*>", "");
                        String s2 = o2.toString().replaceAll("<[^>]*>", "");
                        String code1 = s1.split("-")[0].trim();
                        String code2 = s2.split("-")[0].trim();
                        try {
                            int n1 = Integer.parseInt(code1);
                            int n2 = Integer.parseInt(code2);
                            return Integer.compare(n1, n2);
                        } catch (Exception ex) {
                            return s1.compareTo(s2);
                        }
                    });
                    break;
                case "Sắp xếp theo: Tên nhân viên":
                    col = 2;
                    sorter.setComparator(col, (o1, o2) -> {
                        // So sánh theo tên phía sau dấu "-"
                        String s1 = o1.toString().replaceAll("<[^>]*>", "");
                        String s2 = o2.toString().replaceAll("<[^>]*>", "");
                        String name1 = s1.contains("-") ? s1.split("-", 2)[1].trim() : s1;
                        String name2 = s2.contains("-") ? s2.split("-", 2)[1].trim() : s2;
                        return name1.compareToIgnoreCase(name2);
                    });
                    break;
                case "Sắp xếp theo: Độ tuổi":
                    col = 4; // "Ngày sinh"
                    sorter.setComparator(col, (o1, o2) -> {
                        // So sánh năm sinh (yyyy-MM-dd)
                        try {
                            return o1.toString().compareTo(o2.toString());
                        } catch (Exception ex) {
                            return 0;
                        }
                    });
                    break;
                case "Sắp xếp theo: Mức lương":
                    col = 9;
                    sorter.setComparator(col, (o1, o2) -> {
                        try {
                            int n1 = Integer.parseInt(o1.toString().replace(",", "").replace(".", ""));
                            int n2 = Integer.parseInt(o2.toString().replace(",", "").replace(".", ""));
                            return Integer.compare(n1, n2);
                        } catch (Exception ex) {
                            return o1.toString().compareTo(o2.toString());
                        }
                    });
                    break;
                default:
                    col = 2;
            }
            sorter.setSortKeys(java.util.Collections.singletonList(new RowSorter.SortKey(col, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING)));
        } else {
            sorter.setSortKeys(null); // Không sắp xếp nếu chưa chọn đủ
        }
    }
}

// Dữ liệu hiển thị trên giao diện nhân viên (EmployeePanel) chỉ là dữ liệu mẫu (hardcode trong mảng data) để minh họa giao diện.
// Những nhân viên này CHƯA được lưu trong database, trừ khi bạn đã có code thêm vào DB ở nơi khác.
// Nếu muốn đồng bộ dữ liệu với database, bạn cần viết code truy vấn DB (SELECT) và đổ dữ liệu vào bảng thay vì dùng mảng mẫu.
