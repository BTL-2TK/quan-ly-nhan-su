package view;

import utils.DatabaseUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class AttendancePanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel employeeModel;
    private JComboBox<String> cbMonth, cbYear;
    private JLabel lblEmployee;
    private JPanel calendarPanel;
    private JButton btnNghi, btnDiTre, btnTangCa, btnXoa, btnThem;
    private int selectedRow = -1;
    private String selectedEmployeeCode = null;
    private Map<LocalDate, String> attendanceMap = new HashMap<>();
    private LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

    public AttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top: chọn tháng/năm
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        cbMonth = new JComboBox<>();
        for (int m = 1; m <= 12; m++) cbMonth.addItem("Tháng " + m);
        cbYear = new JComboBox<>();
        int yearNow = LocalDate.now().getYear();
        for (int y = yearNow - 2; y <= yearNow + 2; y++) cbYear.addItem(String.valueOf(y));
        cbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cbYear.setSelectedItem(String.valueOf(yearNow));
        topPanel.add(cbMonth);
        topPanel.add(cbYear);

        add(topPanel, BorderLayout.NORTH);

        // Center: chia 2 panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.35);

        // Bảng nhân viên bên trái
        String[] empCols = {"STT", "Nhân viên", "Trạng thái"};
        employeeModel = new DefaultTableModel(empCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        employeeTable = new JTable(employeeModel);
        employeeTable.setRowHeight(28);
        JScrollPane empScroll = new JScrollPane(employeeTable);

        splitPane.setLeftComponent(empScroll);

        // Panel lịch chấm công bên phải
        JPanel rightPanel = new JPanel(new BorderLayout());
        lblEmployee = new JLabel("Chọn nhân viên để chấm công", SwingConstants.CENTER);
        lblEmployee.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(lblEmployee, BorderLayout.NORTH);

        calendarPanel = new JPanel();
        rightPanel.add(calendarPanel, BorderLayout.CENTER);

        // Panel nút trạng thái
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        btnNghi = new JButton("Nghỉ");
        btnDiTre = new JButton("Đi trễ");
        btnTangCa = new JButton("Tăng ca");
        btnXoa = new JButton("Xóa");
        btnThem = new JButton("Thêm");
        btnThem.setBackground(new Color(0, 200, 0));
        btnThem.setForeground(Color.WHITE);
        statusPanel.add(btnNghi);
        statusPanel.add(btnDiTre);
        statusPanel.add(btnTangCa);
        statusPanel.add(btnXoa);
        statusPanel.add(Box.createHorizontalStrut(40));
        statusPanel.add(btnThem);
        rightPanel.add(statusPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Load dữ liệu nhân viên
        loadEmployeeList();

        // Sự kiện chọn tháng/năm
        ActionListener reloadCal = _ -> {
            currentMonth = LocalDate.of(
                Integer.parseInt(cbYear.getSelectedItem().toString()),
                cbMonth.getSelectedIndex() + 1, 1
            );
            reloadAttendance();
        };
        cbMonth.addActionListener(reloadCal);
        cbYear.addActionListener(reloadCal);

        // Sự kiện chọn nhân viên
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row >= 0) {
                selectedRow = row;
                selectedEmployeeCode = employeeModel.getValueAt(row, 1).toString().split("-")[0].trim();
                lblEmployee.setText(employeeModel.getValueAt(row, 1).toString());
                reloadAttendance();
            }
        });

        // Sự kiện chọn ngày trên lịch
        calendarPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component comp = calendarPanel.getComponentAt(e.getPoint());
                if (comp instanceof DayCell) {
                    for (Component c : calendarPanel.getComponents()) {
                        if (c instanceof DayCell) ((DayCell) c).setSelected(false);
                    }
                    ((DayCell) comp).setSelected(true);
                    calendarPanel.repaint();
                }
            }
        });

        // Sự kiện các nút trạng thái
        btnNghi.addActionListener(_ -> setStatusForSelectedDay("Nghỉ"));
        btnDiTre.addActionListener(_ -> setStatusForSelectedDay("Đi trễ"));
        btnTangCa.addActionListener(_ -> setStatusForSelectedDay("Tăng ca"));
        btnXoa.addActionListener(_ -> setStatusForSelectedDay(null));
        btnThem.addActionListener(_ -> saveAttendance());

        // Mặc định chọn tháng/năm hiện tại
        reloadAttendance();
    }

    private void loadEmployeeList() {
        employeeModel.setRowCount(0);
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT e.EmployeeCode, e.FirstName, e.LastName FROM Employees e"
             );
             ResultSet rs = ps.executeQuery()) {
            int stt = 1;
            while (rs.next()) {
                String code = rs.getString("EmployeeCode");
                String name = rs.getString("FirstName") + " " + rs.getString("LastName");
                // Trạng thái: kiểm tra đã có chấm công tháng này chưa
                String status = getAttendanceStatus(code, currentMonth.getYear(), currentMonth.getMonthValue());
                employeeModel.addRow(new Object[]{
                    stt++, code + " - " + name, status
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getAttendanceStatus(String employeeCode, int year, int month) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM Attendance WHERE EmployeeCode = ? AND YEAR(WorkDate) = ? AND MONTH(WorkDate) = ?"
             )) {
            ps.setString(1, employeeCode);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return "<html><span style='color:green'>Đã chấm công</span></html>";
                }
            }
        } catch (Exception ex) {}
        return "<html><span style='color:red'>Chưa chấm công</span></html>";
    }

    private void reloadAttendance() {
        calendarPanel.removeAll();
        attendanceMap.clear();
        if (selectedEmployeeCode == null) {
            calendarPanel.revalidate();
            calendarPanel.repaint();
            return;
        }
        // Lấy dữ liệu chấm công từ DB
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT WorkDate, Status FROM Attendance WHERE EmployeeCode = ? AND YEAR(WorkDate) = ? AND MONTH(WorkDate) = ?"
             )) {
            ps.setString(1, selectedEmployeeCode);
            ps.setInt(2, currentMonth.getYear());
            ps.setInt(3, currentMonth.getMonthValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("WorkDate").toLocalDate();
                    String status = rs.getString("Status");
                    attendanceMap.put(date, status);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Vẽ lịch
        drawCalendar();
    }

    private void drawCalendar() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new GridLayout(0, 7, 2, 2));
        // Header thứ
        String[] days = {"CN", "Hai", "Ba", "Tư", "Năm", "Sáu", "Bảy"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setOpaque(true);
            lbl.setBackground(new Color(240, 240, 240));
            calendarPanel.add(lbl);
        }
        LocalDate firstDay = currentMonth.withDayOfMonth(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // CN=0, T2=1,...
        int daysInMonth = currentMonth.lengthOfMonth();
        int cellCount = 0;
        // Ô trống đầu tháng
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
            cellCount++;
        }
        // Các ngày trong tháng
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = currentMonth.withDayOfMonth(d);
            String status = attendanceMap.get(date);
            DayCell cell = new DayCell(date, status);
            calendarPanel.add(cell);
            cellCount++;
        }
        // Ô trống cuối tháng
        while (cellCount % 7 != 0) {
            calendarPanel.add(new JLabel(""));
            cellCount++;
        }
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void setStatusForSelectedDay(String status) {
        for (Component c : calendarPanel.getComponents()) {
            if (c instanceof DayCell && ((DayCell) c).isSelected()) {
                ((DayCell) c).setStatus(status);
                calendarPanel.repaint();
                break;
            }
        }
    }

    private void saveAttendance() {
        if (selectedEmployeeCode == null) return;
        // Lưu trạng thái từng ngày vào DB
        try (Connection conn = DatabaseUtils.getConnection()) {
            // Xóa dữ liệu cũ tháng này
            try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM Attendance WHERE EmployeeCode = ? AND YEAR(WorkDate) = ? AND MONTH(WorkDate) = ?"
            )) {
                ps.setString(1, selectedEmployeeCode);
                ps.setInt(2, currentMonth.getYear());
                ps.setInt(3, currentMonth.getMonthValue());
                ps.executeUpdate();
            }
            // Thêm mới
            for (Component c : calendarPanel.getComponents()) {
                if (c instanceof DayCell) {
                    DayCell cell = (DayCell) c;
                    if (cell.getStatus() != null && !cell.getStatus().isEmpty()) {
                        try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO Attendance (EmployeeCode, WorkDate, Status) VALUES (?, ?, ?)"
                        )) {
                            ps.setString(1, selectedEmployeeCode);
                            ps.setDate(2, java.sql.Date.valueOf(cell.getDate()));
                            ps.setString(3, cell.getStatus());
                            ps.executeUpdate();
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Đã lưu chấm công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Cập nhật trạng thái bảng nhân viên
            loadEmployeeList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu chấm công: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ô ngày trong lịch
    static class DayCell extends JPanel {
        private final LocalDate date;
        private String status;
        private boolean selected = false;

        public DayCell(LocalDate date, String status) {
            this.date = date;
            this.status = status;
            setPreferredSize(new Dimension(48, 48));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            setToolTipText(date.getDayOfMonth() + " " + date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("vi")));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selected = true;
                    getParent().repaint();
                }
            });
        }

        public LocalDate getDate() { return date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean sel) { this.selected = sel; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                setBackground(new Color(230, 230, 230));
            } else {
                setBackground(Color.WHITE);
            }
            if (selected) {
                g.setColor(new Color(64, 169, 243, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.drawString(String.valueOf(date.getDayOfMonth()), 8, 20);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("vi")), 8, 38);
            // Hiển thị trạng thái
            if (status != null) {
                switch (status) {
                    case "Nghỉ":
                        g.setColor(Color.RED);
                        g.drawString("Nghỉ", getWidth() - 32, getHeight() - 8);
                        break;
                    case "Đi trễ":
                        g.setColor(new Color(255, 140, 0));
                        g.drawString("Trễ", getWidth() - 32, getHeight() - 8);
                        break;
                    case "Tăng ca":
                        g.setColor(new Color(0, 153, 0));
                        g.drawString("Tăng ca", getWidth() - 48, getHeight() - 8);
                        break;
                }
            }
        }
    }
}
