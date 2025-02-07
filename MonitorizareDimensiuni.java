package DietDiary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clasa MonitorizareDimensiuni este pagina care se deschide cand apasam butonul de "Monitorizare Dimensiuni" de pe Main Menu.
 * Aceasta are un userId pentru a putea face legatura dintre tabelul cu utilizatori si cele din care preia apoi date.
 */
public class MonitorizareDimensiuni {
    private int userId;

    /**
     * Pagina de monitorizare a dimensiunilor corpului.
     * @param userId Id-ul utilizatorului dupa care cautam apoi informatii in celalalte tabele
     * Pagina are doua butoane, unul pentru a introduce detalii legate de dimeniuni si unul pentru a vizualiza datele sub forma de tabel.
     */
    public MonitorizareDimensiuni(int userId) {
        this.userId = userId;

        JFrame frame = new JFrame("Monitorizare Dimensiuni Corp");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(255, 228, 225));

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton addDimensionsButton = new JButton("Adaugă Dimensiuni");
        JButton viewDimensionsButton = new JButton("Jurnal Dimensiuni");
        JButton closeButton = new JButton("Închide");

        addDimensionsButton.setBackground(new Color(64, 200, 134));
        addDimensionsButton.setForeground(Color.WHITE);
        addDimensionsButton.setFocusPainted(false);

        viewDimensionsButton.setBackground(new Color(64, 200, 134));
        viewDimensionsButton.setForeground(Color.WHITE);
        viewDimensionsButton.setFocusPainted(false);

        closeButton.setBackground(new Color(238, 75, 106));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);

        addDimensionsButton.addActionListener(e -> adaugaDimensiuni(userId));
        viewDimensionsButton.addActionListener(e -> jurnalDimensiuni());
        closeButton.addActionListener(e -> frame.dispose());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(addDimensionsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(viewDimensionsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(closeButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Functie care creeaza tabelul "dimensiuni" unde vor fi introduse datele, daca acesta nu exista deja.
     * @param connection Conexiunea catre baza de date
     * @throws SQLException
     */
    private void creareTabel(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS dimensiuni ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "user_id INT NOT NULL, "
                + "talia DOUBLE, "
                + "brat DOUBLE, "
                + "picior DOUBLE, "
                + "data DATE, "
                + "FOREIGN KEY (user_id) REFERENCES users(id) "
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        }
    }

    /**
     * Functie care adauga datele despre dimensiuni in table.
     * @param userId Id-ul utilizatorului care este logat
     * Functia preia datele introduse de utilizator si le introduce in tabel.
     */
    private void adaugaDimensiuni(int userId) {
        JFrame addDimensionsFrame = new JFrame("Adaugă Dimensiuni Corp");
        addDimensionsFrame.setSize(400, 300);
        addDimensionsFrame.setLayout(new BorderLayout(10, 10));
        addDimensionsFrame.getContentPane().setBackground(new Color(255, 228, 225));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(255, 228, 225));

        JLabel taliaLabel = new JLabel("Talia (cm):");
        taliaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField taliaField = new JTextField();
        taliaField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel bratLabel = new JLabel("Brațe (cm):");
        bratLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField bratField = new JTextField();
        bratField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel piciorLabel = new JLabel("Picioare (cm):");
        piciorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField piciorField = new JTextField();
        piciorField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel dataLabel = new JLabel("Data măsurării (YYYY-MM-DD):");
        dataLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField dataField = new JTextField();
        dataField.setFont(new Font("Arial", Font.PLAIN, 14));

        formPanel.add(taliaLabel);
        formPanel.add(taliaField);
        formPanel.add(bratLabel);
        formPanel.add(bratField);
        formPanel.add(piciorLabel);
        formPanel.add(piciorField);
        formPanel.add(dataLabel);
        formPanel.add(dataField);

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
            try (Connection connection = ConexiuneBazaDate.getConnection()) {
                creareTabel(connection);

                double talia = Double.parseDouble(taliaField.getText());
                double brat = Double.parseDouble(bratField.getText());
                double picior = Double.parseDouble(piciorField.getText());
                String data = dataField.getText().trim();

                String insertSQL = "INSERT INTO dimensiuni (user_id, talia, brat, picior, data) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, talia);
                pstmt.setDouble(3, brat);
                pstmt.setDouble(4, picior);
                pstmt.setString(5, data);
                pstmt.executeUpdate();

                statusLabel.setText("Dimensiuni salvate cu succes!");
                statusLabel.setForeground(new Color(0, 128, 0));

            } catch (SQLException ex) {
                ex.printStackTrace();
                statusLabel.setText("Eroare la salvarea dimensiunilor.");
                statusLabel.setForeground(Color.RED);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Toate câmpurile trebuie să fie numerice.");
                statusLabel.setForeground(Color.RED);
            }
        });

        closeButton.addActionListener(e -> addDimensionsFrame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        addDimensionsFrame.add(formPanel, BorderLayout.CENTER);
        addDimensionsFrame.add(buttonPanel, BorderLayout.SOUTH);
        addDimensionsFrame.add(statusLabel, BorderLayout.NORTH);

        addDimensionsFrame.setLocationRelativeTo(null);
        addDimensionsFrame.setVisible(true);
    }

    /**
     * Functie care afiseaza datele legate de dimensiuni sub forma unui tabel.
     * Tabelul are coloanele despre data la care au fost introduse dimensiunile si dimensiunile efective despre talie, brate si picioare.
     */
    private void jurnalDimensiuni() {
        JFrame journalFrame = new JFrame("Jurnal Dimensiuni Corp");
        journalFrame.setSize(400, 300);
        journalFrame.setLayout(new BorderLayout(10, 10));
        journalFrame.getContentPane().setBackground(new Color(255, 228, 225));

        String[] columnNames = {"Data", "Talia (cm)", "Brațe (cm)", "Picioare (cm)"};
        Object[][] data = preiaDateUtilizator(userId);

        JTable dimensionsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        dimensionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JTableHeader header = dimensionsTable.getTableHeader();
        header.setBackground(new Color(238, 75, 106));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        dimensionsTable.setFillsViewportHeight(true);
        dimensionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dimensionsTable.setRowHeight(30);
        dimensionsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(dimensionsTable);
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
     * Functie care preia datele din baza de date in functie de id-ul utilizatorului.
     * @param userId Id-ul dupa care extragem datele din tabel
     * @return Un obiect care contine datele din tabel
     * Datele sunt stocate initial sub forma unei mape, iar apoi sub puse intr-un vector si returnate.
     */
    private Object[][] preiaDateUtilizator(int userId) {
        String query = "SELECT data, talia, brat, picior FROM dimensiuni WHERE user_id = ? ORDER BY data DESC";
        Object[][] data = new Object[0][0];
        try (Connection connection = ConexiuneBazaDate.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            LinkedHashMap<LocalDate, Double[]> dimensionsMap = new LinkedHashMap<>();
            while (rs.next()) {
                LocalDate date = rs.getDate("data").toLocalDate();
                Double[] dimensions = {
                    rs.getDouble("talia"),
                    rs.getDouble("brat"),
                    rs.getDouble("picior")
                };
                dimensionsMap.put(date, dimensions);
            }

            data = new Object[dimensionsMap.size()][4];
            int index = 0;
            for (Map.Entry<LocalDate, Double[]> entry : dimensionsMap.entrySet()) {
                data[index][0] = entry.getKey();
                System.arraycopy(entry.getValue(), 0, data[index], 1, entry.getValue().length);
                index++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

}
