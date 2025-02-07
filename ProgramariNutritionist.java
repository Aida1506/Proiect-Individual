package DietDiary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa care gestioneaza programarile la nutritionist.
 * Are un userId care preia datele din tabel pentru utilizatorul curent.
 */
public class ProgramariNutritionist {
    private int userId;

    /**
     * Pagina care se deschide in momentul in care apasam butonul pentur Programari Nutritionist.
     * @param userId utilizatorul curent pentru care preluam informatiile din baza de date
     * Avem doua butoane, unul unde se poate adauga o programare si unul unde se pot vizualiza programarile facute deja. 
     * Atunci cand selectam o programare, avem optiunea de a o modifica.
     */
    public ProgramariNutritionist(int userId) {
        this.userId = userId;

        JFrame frame = new JFrame("Programări");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(255, 228, 225));

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton addAppointmentButton = new JButton("Adaugă Programare");
        JButton viewAppointmentsButton = new JButton("Vizualizare Programări");
        JButton closeButton = new JButton("Închide");

        addAppointmentButton.setBackground(new Color(64, 200, 134));
        addAppointmentButton.setForeground(Color.WHITE);
        addAppointmentButton.setFocusPainted(false);

        viewAppointmentsButton.setBackground(new Color(64, 200, 134));
        viewAppointmentsButton.setForeground(Color.WHITE);
        viewAppointmentsButton.setFocusPainted(false);

        closeButton.setBackground(new Color(238, 75, 106));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);

        addAppointmentButton.addActionListener(e -> adaugaProgramare(userId));
        viewAppointmentsButton.addActionListener(e -> vizualizareProgramari(userId));
        closeButton.addActionListener(e -> frame.dispose());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(addAppointmentButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(viewAppointmentsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(closeButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * Functie care creeaza tabelul programari daca acesta nu exista deja in baza de date.
     * @param connection conexiunea catre baza de date
     * @throws SQLException
     */
    private void creareTabel(Connection connection) throws SQLException {
 	    String createTableSQL = "CREATE TABLE IF NOT EXISTS programari ("
 	            + "id INT PRIMARY KEY AUTO_INCREMENT, "
 	            + "user_id INT NOT NULL, "
 	            + "ziua DATE, "
 	            + "ora VARCHAR(255), "
 	            + "FOREIGN KEY (user_id) REFERENCES users(id) "
 	            + ")";
 	    try (Statement stmt = connection.createStatement()) {
 	        stmt.executeUpdate(createTableSQL);
 	    }
 	}

    /**
     * Functie care adauga o programare in tabelul cu programari. 
     * @param userId utilizatorul curent pentru care adaugam programarea.
     */
    private void adaugaProgramare(int userId) {
        JFrame addAppointmentFrame = new JFrame("Adaugă Programare");
        addAppointmentFrame.setSize(400, 300);
        addAppointmentFrame.setLayout(new BorderLayout(10, 10));
        addAppointmentFrame.getContentPane().setBackground(new Color(255, 228, 225)); 

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        formPanel.setBackground(new Color(255, 228, 225)); 

        JLabel dateLabel = new JLabel("Data Programării (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel timeLabel = new JLabel("Ora Programării:");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField timeField = new JTextField();
        timeField.setFont(new Font("Arial", Font.PLAIN, 14));

        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 228, 225)); 

        JButton saveButton = new JButton("Salvează");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(64, 200, 134));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton closeButton = new JButton("Închide");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(238, 75, 106)); 
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        saveButton.addActionListener(e -> {
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();

            try (Connection connection = ConexiuneBazaDate.getConnection()) {
            	creareTabel(connection);
                String insertSQL = "INSERT INTO programari (user_id, ziua, ora) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                pstmt.setInt(1, userId);
                pstmt.setString(2, date);
                pstmt.setString(3, time);
                pstmt.executeUpdate();

                statusLabel.setText("Programare salvată cu succes!");
                statusLabel.setForeground(new Color(0, 128, 0));
                dateField.setText("");
                timeField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                statusLabel.setText("Eroare la salvarea programării.");
                statusLabel.setForeground(Color.RED);
            }
        });

        closeButton.addActionListener(e -> addAppointmentFrame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        addAppointmentFrame.add(formPanel, BorderLayout.CENTER);
        addAppointmentFrame.add(buttonPanel, BorderLayout.SOUTH);
        addAppointmentFrame.add(statusLabel, BorderLayout.NORTH);

        addAppointmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addAppointmentFrame.setLocationRelativeTo(null);
        addAppointmentFrame.setVisible(true);
    }

    /**
     * Functie care permite vizualizarea programarilor.
     * @param userId utilizatorul curent pentur care vizualizam programarile
     * Butonul "Modifica Programare" permite modificarea unei programari selectate.
     */
    private void vizualizareProgramari(int userId) {
        JFrame viewAppointmentsFrame = new JFrame("Vezi Programări");
        viewAppointmentsFrame.setSize(400, 300);
        viewAppointmentsFrame.setLayout(new BorderLayout(10, 10));
        viewAppointmentsFrame.getContentPane().setBackground(new Color(255, 228, 225));

        String[] columnNames = {"Data", "Ora"};
        Object[][] data = preiaDateUtilizator(userId);
        
        JTable appointmentsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(new Color(255, 240, 245));
                } else {
                    c.setBackground(Color.WHITE);
                }
                if (isSelected) {
                    c.setBackground(new Color(255, 182, 193));
                }
                return c;
            }
        });

        JTableHeader header = appointmentsTable.getTableHeader();
        header.setBackground(new Color(238, 75, 106));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        appointmentsTable.setFillsViewportHeight(true);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(30);
        appointmentsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        viewAppointmentsFrame.add(scrollPane, BorderLayout.CENTER);

        JButton modifyButton = new JButton("Modifică Programare");
        modifyButton.setBackground(new Color(64, 200, 134));
        modifyButton.setForeground(Color.WHITE);
        modifyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        modifyButton.setFocusPainted(false);
        modifyButton.setPreferredSize(new Dimension(170, 40));
        
        modifyButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedDate = (String) appointmentsTable.getValueAt(selectedRow, 0);
                String selectedTime = (String) appointmentsTable.getValueAt(selectedRow, 1);
                modificareProgramari(userId, selectedDate, selectedTime);
                viewAppointmentsFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(viewAppointmentsFrame, "Selectați o programare pentru modificare!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Închide");
        closeButton.setBackground(new Color(238, 75, 106));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.addActionListener(e -> viewAppointmentsFrame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 228, 225));
        buttonPanel.add(modifyButton);
        buttonPanel.add(closeButton);
        viewAppointmentsFrame.add(buttonPanel, BorderLayout.SOUTH);

        viewAppointmentsFrame.setLocationRelativeTo(null);
        viewAppointmentsFrame.setVisible(true);
    }

    /**
     * Functie care modifica o programare.
     * @param userId utilizatorul pentru care modificam programamrea
     * @param originalDate data initiala la care a fost facuta programarea
     * @param originalTime ora initiala la care a fost facuta programarea
     * Functia va modifica data si ora cu datele noi introduse de utilizator.
     */
    private void modificareProgramari(int userId, String originalDate, String originalTime) {
        JFrame modifyAppointmentFrame = new JFrame("Modifică Programare");
        modifyAppointmentFrame.setSize(400, 300);
        modifyAppointmentFrame.setLayout(new BorderLayout(10, 10));
        modifyAppointmentFrame.getContentPane().setBackground(new Color(255, 228, 225));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(255, 228, 225));

        JLabel dateLabel = new JLabel("Noua Dată (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField dateField = new JTextField(originalDate);
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel timeLabel = new JLabel("Noua Oră:");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField timeField = new JTextField(originalTime);
        timeField.setFont(new Font("Arial", Font.PLAIN, 14));

        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 228, 225));

        JButton saveButton = new JButton("Salvează");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(64, 200, 134));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(120, 40));
        
        JButton closeButton = new JButton("Închide");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(238, 75, 106));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(120, 40));
        
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        saveButton.addActionListener(e -> {
            String newDate = dateField.getText().trim();
            String newTime = timeField.getText().trim();

            try (Connection connection = ConexiuneBazaDate.getConnection()) {
                String updateSQL = "UPDATE programari SET ziua = ?, ora = ? WHERE user_id = ? AND ziua = ? AND ora = ?";
                PreparedStatement pstmt = connection.prepareStatement(updateSQL);
                pstmt.setString(1, newDate);
                pstmt.setString(2, newTime);
                pstmt.setInt(3, userId);
                pstmt.setString(4, originalDate);
                pstmt.setString(5, originalTime);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(modifyAppointmentFrame, "Programarea a fost modificată cu succes!");
                modifyAppointmentFrame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(modifyAppointmentFrame, "Eroare la modificarea programării.", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        closeButton.addActionListener(e -> modifyAppointmentFrame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        modifyAppointmentFrame.add(formPanel, BorderLayout.CENTER);
        modifyAppointmentFrame.add(buttonPanel, BorderLayout.SOUTH);
        modifyAppointmentFrame.setLocationRelativeTo(null);
        modifyAppointmentFrame.setVisible(true);
    }
    
    /**
     * Functie care preia datele utilizatorului din baza de date.
     * @param userId utilizatorul pentru care preluam datele
     * @return lista cu datele extrase din baza de date
     * Functia creeaza o lista, se conecteaza la baza de date si apoi preia datele din tabel, pe care le stocheaza in lista.
     */
    private Object[][] preiaDateUtilizator(int userId) {
        List<Object[]> dataList = new ArrayList<>();
        try (Connection connection = ConexiuneBazaDate.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT ziua, ora FROM programari WHERE user_id = ? ORDER BY ziua, ora")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataList.add(new Object[]{rs.getString("ziua"), rs.getString("ora")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList.toArray(new Object[0][2]);
    }
}
