package DietDiary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clasa care reprezinta pagina de monitorizare a greutatii care se deschide cand apasam pe butonul de "Monitorizare Greutate" din Main Menu.
 */
public class MonitorizareGreutate {
    private final int userId;

    /**
     * Pagina cu doua butoane, unul unde putem introduce greutatea si unul unde putem vedea greutatile in functie de data.
     * @param userId Id-ul utilizatorului dupa care cautam in tabele
     */
    public MonitorizareGreutate(int userId) {
        this.userId = userId;

        JFrame frame = new JFrame("Monitorizare Greutate");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(255, 228, 225));

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton addWeightButton = new JButton("Adaugă Greutate");
        JButton viewJournalButton = new JButton("Jurnal Greutate");
        JButton closeButton = new JButton("Închide");

        addWeightButton.setBackground(new Color(64, 200, 134));
        addWeightButton.setForeground(Color.WHITE);
        addWeightButton.setFocusPainted(false);

        viewJournalButton.setBackground(new Color(64, 200, 134));
        viewJournalButton.setForeground(Color.WHITE);
        viewJournalButton.setFocusPainted(false);

        closeButton.setBackground(new Color(238, 75, 106));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);

        addWeightButton.addActionListener(e -> adaugaGreutate());
        viewJournalButton.addActionListener(e -> jurnalGreutate());
        closeButton.addActionListener(e -> frame.dispose());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(addWeightButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(viewJournalButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(closeButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * Functie care creeaza tabelul "weight" daca acesta nu exista.
     * @param connection Conexiunea catre baza de date
     * @throws SQLException
     */
    private void creareTabel(Connection connection) throws SQLException {
 	    String createTableSQL = "CREATE TABLE IF NOT EXISTS greutate ("
 	            + "id INT PRIMARY KEY AUTO_INCREMENT, "
 	            + "user_id INT NOT NULL, "
 	            + "kilograme double NOT NULL, "
 	            + "ziua DATE NOT NULL, "
 	            + "FOREIGN KEY (user_id) REFERENCES users(id) "
 	            + ")";
 	    try (Statement stmt = connection.createStatement()) {
 	        stmt.executeUpdate(createTableSQL);
 	    }
 	}

    /**
     * Functie care deschide fereastra unde putem adauga greutatea si data pentru aceasta.
     */
    private void adaugaGreutate() {
        JFrame addWeightFrame = new JFrame("Adaugă Greutate");
        addWeightFrame.setSize(400, 300);
        addWeightFrame.setLayout(new BorderLayout(10, 10));
        addWeightFrame.getContentPane().setBackground(new Color(255, 228, 225));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(255, 228, 225));
        
        JLabel weightLabel = new JLabel("Greutate (kg):");
        weightLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField weightField = new JTextField();
        weightField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel dateLabel = new JLabel("Data (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        formPanel.add(weightLabel);
        formPanel.add(weightField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);

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
            String weightText = weightField.getText().trim();
            String dateText = dateField.getText().trim();

            try (Connection connection = ConexiuneBazaDate.getConnection()) {
            	creareTabel(connection);
                if (!weightText.isEmpty() && !dateText.isEmpty()) {
                    try {
                        double weight = Double.parseDouble(weightText);

                        String insertSQL = "INSERT INTO greutate (user_id, kilograme, ziua) VALUES (?, ?, ?)";
                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                        pstmt.setInt(1, userId);
                        pstmt.setDouble(2, weight);
                        pstmt.setDate(3, Date.valueOf(dateText));
                        pstmt.executeUpdate();

                        statusLabel.setText("Greutatea a fost salvată cu succes!");
                        statusLabel.setForeground(new Color(0, 128, 0));
                        weightField.setText("");
                        dateField.setText("");
                    } catch (NumberFormatException ex) {
                        statusLabel.setText("Greutatea trebuie să fie un număr valid.");
                    }
                } else {
                    statusLabel.setText("Toate câmpurile sunt obligatorii.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                statusLabel.setText("Eroare la salvarea greutății.");
            }
        });

        closeButton.addActionListener(e -> addWeightFrame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        addWeightFrame.add(formPanel, BorderLayout.CENTER);
        addWeightFrame.add(buttonPanel, BorderLayout.SOUTH);
        addWeightFrame.add(statusLabel, BorderLayout.NORTH);

        addWeightFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWeightFrame.setLocationRelativeTo(null);
        addWeightFrame.setVisible(true);
    }

    /**
     * Functie care arata greutatile masurate si data la care au fost masurate, sub forma unui tabel.
     * Daca sunt introduse mai multe dimensiuni la aceeasi data, va fi luata utlima masuratoare.
     */
    private void jurnalGreutate() {
        JFrame journalFrame = new JFrame("Jurnal Greutate");
        journalFrame.setSize(400, 300);
        journalFrame.setLayout(new BorderLayout(10, 10));
        journalFrame.getContentPane().setBackground(new Color(255, 228, 225));

        String[] columnNames = {"Data", "Greutate (kg)"};
        Object[][] data = preiaDateUtilizator(userId);

        JTable weightTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        weightTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JTableHeader header = weightTable.getTableHeader();
        header.setBackground(new Color(238, 75, 106));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        weightTable.setFillsViewportHeight(true);
        weightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        weightTable.setRowHeight(30);
        weightTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(weightTable);
        journalFrame.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Închide");
        closeButton.setBackground(new Color(238, 75, 106));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.addActionListener(e -> journalFrame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 228, 225));
        buttonPanel.add(closeButton);
        journalFrame.add(buttonPanel, BorderLayout.SOUTH);

        journalFrame.setLocationRelativeTo(null);
        journalFrame.setVisible(true);
    }

    /**
     * Functie care preia datele din table in functie de Id-ul utilizatorului.
     * @param userId Id-ul dupa care se face cautarea datelor
     * @return Un obiect care contine datele extrase din tabel
     * Datele sunt extrase sub forma unei mape, iar apoi sunt puse intr-un vector.
     */
    private Object[][] preiaDateUtilizator(int userId) {
        Map<LocalDate, Double> mapaGreutate = new LinkedHashMap<>();

        String query = "SELECT ziua, kilograme FROM greutate WHERE user_id = ? ORDER BY ziua DESC";

        try (Connection connection = ConexiuneBazaDate.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("ziua").toLocalDate();
                double weight = rs.getDouble("kilograme");
                mapaGreutate.put(date, weight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[][] data = new Object[mapaGreutate.size()][2];
        int index = 0;
        for (Map.Entry<LocalDate, Double> entry : mapaGreutate.entrySet()) {
            data[index][0] = entry.getKey();
            data[index][1] = entry.getValue();
            index++;
        }

        return data;
    }
}
