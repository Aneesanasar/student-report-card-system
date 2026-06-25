import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.ArrayList;

public class CompleteReportCardSystem extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private ArrayList<Student> studentList = new ArrayList<>();
    private DefaultTableModel tableModel;

    public CompleteReportCardSystem() {
        setTitle("Student Report Card System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "Login");
        mainPanel.add(new AdminPanel(this), "Admin");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    // ---------------- Student Model ----------------
    class Student {
        int id;
        String name, className, section;
        int sub1, sub2, sub3, total;
        String grade;

        Student(int id, String name, String className, String section, int sub1, int sub2, int sub3) {
            this.id = id;
            this.name = name;
            this.className = className;
            this.section = section;
            this.sub1 = sub1;
            this.sub2 = sub2;
            this.sub3 = sub3;
            calculateTotalAndGrade();
        }

        void calculateTotalAndGrade() {
            total = sub1 + sub2 + sub3;
            double percent = total / 3.0;
            if (percent >= 90) grade = "A+";
            else if (percent >= 80) grade = "A";
            else if (percent >= 70) grade = "B+";
            else if (percent >= 60) grade = "B";
            else if (percent >= 50) grade = "C";
            else grade = "F";
        }
    }

    // ---------------- Login Panel ----------------
    class LoginPanel extends JPanel {
        public LoginPanel(CompleteReportCardSystem frame) {
            setLayout(new GridBagLayout());
            setBackground(new Color(230, 245, 255));

            JLabel title = new JLabel("Student Report Card System");
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(new Color(0, 102, 204));

            JLabel userLabel = new JLabel("Username:");
            JLabel passLabel = new JLabel("Password:");
            JTextField userField = new JTextField(15);
            JPasswordField passField = new JPasswordField(15);

            JButton loginBtn = new JButton("Login");
            loginBtn.setBackground(new Color(0, 153, 76));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.setFocusPainted(false);

            loginBtn.addActionListener(e -> frame.switchPanel("Admin"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(title, gbc);

            gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; add(userLabel, gbc);
            gbc.gridx = 1; add(userField, gbc);

            gbc.gridy = 2; gbc.gridx = 0; add(passLabel, gbc);
            gbc.gridx = 1; add(passField, gbc);

            gbc.gridy = 3; gbc.gridx = 1; add(loginBtn, gbc);
        }
    }

    // ---------------- Admin Panel ----------------
    class AdminPanel extends JPanel {
        public AdminPanel(CompleteReportCardSystem frame) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            JLabel header = new JLabel("Admin Panel - Manage Students", JLabel.CENTER);
            header.setFont(new Font("Arial", Font.BOLD, 22));
            header.setOpaque(true);
            header.setBackground(new Color(0, 102, 204));
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(100, 60));
            add(header, BorderLayout.NORTH);

            String[] columns = {"ID", "Name", "Class", "Section", "Sub1", "Sub2", "Sub3", "Total", "Grade"};
            tableModel = new DefaultTableModel(columns, 0);
            JTable table = new JTable(tableModel);
            table.setRowHeight(25);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

           JPanel buttonPanel = new JPanel();
buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
buttonPanel.setPreferredSize(new Dimension(900, 60)); // Make it tall enough
buttonPanel.setBackground(new Color(245, 245, 245));

// Create buttons
JButton addBtn = new JButton("Add Student");
JButton updateBtn = new JButton("Update Student");
JButton deleteBtn = new JButton("Delete Student");
JButton viewBtn = new JButton("View Report Card");
JButton printBtn = new JButton("Print Report");
JButton logoutBtn = new JButton("Logout");

// Add buttons to panel
JButton[] buttons = {addBtn, updateBtn, deleteBtn, viewBtn, printBtn, logoutBtn};
for (JButton btn : buttons) {
    btn.setBackground(new Color(0, 153, 76));
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setPreferredSize(new Dimension(160, 35));
    buttonPanel.add(btn);
}

// Add the panel to the bottom of AdminPanel
add(buttonPanel, BorderLayout.SOUTH);

            logoutBtn.addActionListener(e -> frame.switchPanel("Login"));

            addBtn.addActionListener(e -> addStudent());
            updateBtn.addActionListener(e -> updateStudent(table.getSelectedRow()));
            deleteBtn.addActionListener(e -> deleteStudent(table.getSelectedRow()));
            viewBtn.addActionListener(e -> viewReportCard(table.getSelectedRow()));
	    printBtn.addActionListener(e -> printReport());
        }

        private void addStudent() {
            JTextField nameField = new JTextField();
            JTextField classField = new JTextField();
            JTextField sectionField = new JTextField();
            JTextField sub1Field = new JTextField();
            JTextField sub2Field = new JTextField();
            JTextField sub3Field = new JTextField();

            Object[] message = {
                    "Name:", nameField,
                    "Class:", classField,
                    "Section:", sectionField,
                    "Subject 1:", sub1Field,
                    "Subject 2:", sub2Field,
                    "Subject 3:", sub3Field
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Add Student", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int id = studentList.size() + 1;
                    int sub1 = Integer.parseInt(sub1Field.getText());
                    int sub2 = Integer.parseInt(sub2Field.getText());
                    int sub3 = Integer.parseInt(sub3Field.getText());

                    Student s = new Student(id, nameField.getText(), classField.getText(),
                            sectionField.getText(), sub1, sub2, sub3);
                    studentList.add(s);
                    refreshTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Enter valid numbers for marks!");
                }
            }
        }

        private void updateStudent(int index) {
            if (index < 0 || index >= studentList.size()) {
                JOptionPane.showMessageDialog(null, "Select a student to update!");
                return;
            }
            Student s = studentList.get(index);

            JTextField nameField = new JTextField(s.name);
            JTextField classField = new JTextField(s.className);
            JTextField sectionField = new JTextField(s.section);
            JTextField sub1Field = new JTextField(String.valueOf(s.sub1));
            JTextField sub2Field = new JTextField(String.valueOf(s.sub2));
            JTextField sub3Field = new JTextField(String.valueOf(s.sub3));

            Object[] message = {
                    "Name:", nameField,
                    "Class:", classField,
                    "Section:", sectionField,
                    "Subject 1:", sub1Field,
                    "Subject 2:", sub2Field,
                    "Subject 3:", sub3Field
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Update Student", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    s.name = nameField.getText();
                    s.className = classField.getText();
                    s.section = sectionField.getText();
                    s.sub1 = Integer.parseInt(sub1Field.getText());
                    s.sub2 = Integer.parseInt(sub2Field.getText());
                    s.sub3 = Integer.parseInt(sub3Field.getText());
                    s.calculateTotalAndGrade();
                    refreshTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Enter valid numbers for marks!");
                }
            }
        }

        private void deleteStudent(int index) {
            if (index < 0 || index >= studentList.size()) {
                JOptionPane.showMessageDialog(null, "Select a student to delete!");
                return;
            }
            studentList.remove(index);
            refreshTable();
        }

        private void viewReportCard(int index) {
            if (index < 0 || index >= studentList.size()) {
                JOptionPane.showMessageDialog(null, "Select a student to view!");
                return;
            }
            Student s = studentList.get(index);

            JTextArea reportArea = new JTextArea();
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            reportArea.setEditable(false);
            reportArea.setText(
                    "==============================\n" +
                    "        Student Report Card        \n" +
                    "==============================\n" +
                    "Name   : " + s.name + "\n" +
                    "Class  : " + s.className + "\n" +
                    "Section: " + s.section + "\n" +
                    "------------------------------\n" +
                    "Subject 1: " + s.sub1 + "\n" +
                    "Subject 2: " + s.sub2 + "\n" +
                    "Subject 3: " + s.sub3 + "\n" +
                    "------------------------------\n" +
                    "Total  : " + s.total + "\n" +
                    "Grade  : " + s.grade + "\n" +
                    "==============================\n"
            );

            JScrollPane scroll = new JScrollPane(reportArea);
            scroll.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(null, scroll, "Report Card", JOptionPane.INFORMATION_MESSAGE);
        }

        private void refreshTable() {
            tableModel.setRowCount(0);
            for (Student s : studentList) {
                Object[] row = {s.id, s.name, s.className, s.section, s.sub1, s.sub2, s.sub3, s.total, s.grade};
                tableModel.addRow(row);
            }
        }
        private void printReport() {
    try {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Student Report PDF");

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pf, int pageIndex) {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                g.setFont(new Font("Monospaced", Font.PLAIN, 12));

                int y = 80;
                g.drawString("STUDENT REPORT CARDS", 220, y);
                y += 30;
                g.drawString("---------------------------------------------------------------------", 50, y);
                y += 20;
                g.drawString(String.format("%-4s %-15s %-6s %-7s %-6s %-6s %-6s %-6s %-5s",
                        "ID", "Name", "Class", "Sec", "Sub1", "Sub2", "Sub3", "Total", "Grade"), 60, y);
                y += 18;
                g.drawString("---------------------------------------------------------------------", 50, y);
                y += 18;

                for (Student s : studentList) {
                    String line = String.format("%-4d %-15s %-6s %-7s %-6d %-6d %-6d %-6d %-5s",
                            s.id, s.name, s.className, s.section, s.sub1, s.sub2, s.sub3, s.total, s.grade);
                    g.drawString(line, 60, y);
                    y += 18;
                }

                return PAGE_EXISTS;
            }
        });

        // Automatically open your system print dialog (choose “Save as PDF”)
        if (job.printDialog()) {
            job.print();
        }

    } catch (PrinterException ex) {
        JOptionPane.showMessageDialog(null, "Error printing report: " + ex.getMessage());
    }
}
   
}

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompleteReportCardSystem().setVisible(true));
    }
}