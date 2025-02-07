package DietDiary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clasa care contine actiunile referitoare la monitorizarea alimentatiei.
 * Avem un userId, dupa care vom cauta diferite date in tabele.
 */
public class MonitorizareAlimentatie {
	private int userId;
	/**
	 * 
	 * @param userId id-ul utilizatorului
	 * Avem doua butoane, unul prin care putem adauga alimente in tabelul "food", cu numarul de calorii, ora si data consumarii.
	 * Al doilea buton afiseaza caloriile consumate in functie de zile.
	 * Aici avem nevoie de userId, pentru a lua din tabelul cu alimente doar cele care au fost introduse de user-ul curent.
	 */
	 public MonitorizareAlimentatie(int userId) {
		 this.userId = userId;

	        JFrame frame = new JFrame("Monitorizare Alimentație");
	        frame.setSize(400, 200);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.getContentPane().setBackground(new Color(255, 228, 225));

	        frame.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(10, 10, 10, 10);

	        JButton addFoodButton = new JButton("Adaugă Aliment");
	        JButton viewJournalButton = new JButton("Jurnal Alimentar");
	        JButton closeButton = new JButton("Închide");

	        addFoodButton.setBackground(new Color(64, 200, 134));
	        addFoodButton.setForeground(Color.WHITE);
	        addFoodButton.setFocusPainted(false);

	        viewJournalButton.setBackground(new Color(64, 200, 134));
	        viewJournalButton.setForeground(Color.WHITE);
	        viewJournalButton.setFocusPainted(false);

	        closeButton.setBackground(new Color(238, 75, 106)); 
	        closeButton.setForeground(Color.WHITE);
	        closeButton.setFocusPainted(false);
	        
	        addFoodButton.addActionListener(e -> adaugaAliment(userId));
	        viewJournalButton.addActionListener(e -> jurnalAlimentar());
	        closeButton.addActionListener(e -> frame.dispose());
	        
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        frame.add(addFoodButton, gbc);

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
	 	 * Functie care verifica daca tabelul food exista.
	 	 * @param connection conexiunea catre baza de date
	 	 * @throws SQLException
	 	 * Daca tabelul food nu exista, acesta este creat. 
	 	 */
	 	private void creareTabel(Connection connection) throws SQLException {
	 	    String createTableSQL = "CREATE TABLE IF NOT EXISTS alimente ("
	 	            + "id INT PRIMARY KEY AUTO_INCREMENT, "
	 	            + "user_id INT NOT NULL, "
	 	            + "calorii INT NOT NULL, "
	 	            + "ora VARCHAR(255), "
	 	            + "ziua DATE, "
	 	            + "FOREIGN KEY (user_id) REFERENCES users(id) "
	 	            + ")";
	 	    try (Statement stmt = connection.createStatement()) {
	 	        stmt.executeUpdate(createTableSQL);
	 	    }
	 	}

	 	/**
	 	 * Buton care adauga alimente in tabelul "alimente".
	 	 * @param userId user-ul pentru care vor fi adaugate alimentele
	 	 * Pentru butonul de salvare, se conecteaza la baza de date si insereaza datele introduse in tabel.
	 	 */
	    private void adaugaAliment(int userId) {
	        JFrame addFoodFrame = new JFrame("Adaugă Aliment");
	        addFoodFrame.setSize(400, 300);
	        addFoodFrame.setLayout(new BorderLayout(10, 10));
	        addFoodFrame.getContentPane().setBackground(new Color(255, 228, 225)); 

	        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
	        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
	        formPanel.setBackground(new Color(255, 228, 225)); 

	        JLabel caloriesLabel = new JLabel("Număr calorii:");
	        caloriesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
	        JTextField caloriesField = new JTextField();
	        caloriesField.setFont(new Font("Arial", Font.PLAIN, 14));

	        JLabel timeLabel = new JLabel("Ora consumului:");
	        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
	        JTextField timeField = new JTextField();
	        timeField.setFont(new Font("Arial", Font.PLAIN, 14));

	        JLabel dateLabel = new JLabel("Ziua consumului (YYYY-MM-DD):");
	        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
	        JTextField dateField = new JTextField();
	        dateField.setFont(new Font("Arial", Font.PLAIN, 14));

	        formPanel.add(caloriesLabel);
	        formPanel.add(caloriesField);
	        formPanel.add(timeLabel);
	        formPanel.add(timeField);
	        formPanel.add(dateLabel);
	        formPanel.add(dateField);
	        
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
	        buttonPanel.setBackground(new Color(255, 228, 225)); 

	        JButton saveButton = new JButton("Salvează");
	        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
	        saveButton.setBackground(new Color(64, 200, 134)); 
	        saveButton.setForeground(Color.WHITE);
	        saveButton.setFocusPainted(false);
	        saveButton.setPreferredSize(new Dimension(120, 30));
	        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	        JButton closeButton = new JButton("Închide");
	        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
	        closeButton.setBackground(new Color(238, 75, 106)); 
	        closeButton.setForeground(Color.WHITE);
	        closeButton.setFocusPainted(false);
	        closeButton.setPreferredSize(new Dimension(120, 30));
	        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
	        statusLabel.setForeground(Color.RED);
	        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

	        saveButton.addActionListener(e -> {
	            String caloriesText = caloriesField.getText().trim();
	            String time = timeField.getText().trim();
	            String date = dateField.getText().trim();

	            try (Connection connection = ConexiuneBazaDate.getConnection()) {
	                creareTabel(connection);

	                if (!caloriesText.isEmpty() && !time.isEmpty() && !date.isEmpty()) {
	                    try {
	                        int calories = Integer.parseInt(caloriesText);

	                        String insertSQL = "INSERT INTO alimente (user_id, calorii, ora, ziua) VALUES (?, ?, ?, ?)";
	                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);
	                        pstmt.setInt(1, userId);  
	                        pstmt.setInt(2, calories);
	                        pstmt.setString(3, time);
	                        pstmt.setString(4, date);
	                        pstmt.executeUpdate();

	                        statusLabel.setText("Aliment salvat cu succes!");
	                        statusLabel.setForeground(new Color(0, 128, 0));
	                        caloriesField.setText("");
	                        timeField.setText("");
	                        dateField.setText("");
	                    } catch (NumberFormatException ex) {
	                        statusLabel.setText("Numărul de calorii trebuie să fie valid.");
	                        statusLabel.setForeground(Color.RED);
	                    }
	                } else {
	                    statusLabel.setText("Toate câmpurile sunt obligatorii.");
	                    statusLabel.setForeground(Color.RED);
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                statusLabel.setText("Eroare la salvarea alimentului.");
	                statusLabel.setForeground(Color.RED);
	            }
	        });
	        
	        closeButton.addActionListener(e -> addFoodFrame.dispose());

	        buttonPanel.add(saveButton);
	        buttonPanel.add(closeButton);

	        addFoodFrame.add(formPanel, BorderLayout.CENTER);
	        addFoodFrame.add(buttonPanel, BorderLayout.SOUTH);
	        addFoodFrame.add(statusLabel, BorderLayout.NORTH);

	        addFoodFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        addFoodFrame.setLocationRelativeTo(null); 
	        addFoodFrame.setVisible(true);
	    }
	    

	    /**
	     * Functie pentru butonul "Jurnal alimentar", care afiseaza numarul de calorii consumate in functie de zile.
	     * Avem un tabel, care va lua datele din baza de date cu ajutorul functiei getFoodDataForUser.
	     */
	    private void jurnalAlimentar() {
	    	 JFrame journalFrame = new JFrame("Jurnal Alimentar");
	         journalFrame.setSize(400, 300);
	         journalFrame.setLayout(new BorderLayout(10, 10));
	         journalFrame.getContentPane().setBackground(new Color(255, 228, 225));

	         String[] columnNames = {"Ziua", "Total Calorii"};
	         Object[][] data = preiaDateUtilizator(userId); 

	         JTable foodTable = new JTable(data, columnNames) {
	        	 @Override
	             public boolean isCellEditable(int row, int column) {
	                 return false;
	             }
	         };

	         foodTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

	         JTableHeader header = foodTable.getTableHeader();
	         header.setBackground(new Color(238, 75, 106));
	         header.setForeground(Color.WHITE);
	         header.setFont(new Font("Arial", Font.BOLD, 14));

	         foodTable.setFillsViewportHeight(true);
	         foodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	         foodTable.setRowHeight(30); 
	         foodTable.setFont(new Font("Arial", Font.PLAIN, 14)); 

	         JScrollPane scrollPane = new JScrollPane(foodTable);
	         journalFrame.add(scrollPane, BorderLayout.CENTER);

	         JButton closeButton = new JButton("Inchide");
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
	     * Functie care obtine datele din baza de date pentru butonul "Jurnal alimentar".
	     * @param userId id-ul utilizatorului pentru care cautam datele
	     * @return un tabel care pe o coloana are ziua, iar pe cealalta numarul de calorii consumate
	     * Folosesc un Map pentru a salva caloriile pe o zi.
	     * Se conecteaza la baza de date, se parcurg intrarile si se adauga caloriile pentru fiecare zi.
	     * Cu Object se creeaza tabelul pentru JTable.
	     */
	    private Object[][] preiaDateUtilizator(int userId) {
	    	  Map<LocalDate, Integer> caloriiZilnice = new LinkedHashMap<>(); 

	    	    String query = "SELECT ziua, calorii FROM alimente WHERE user_id = ? ORDER BY ziua DESC";

	    	    try (Connection connection = ConexiuneBazaDate.getConnection();
	    	         PreparedStatement pstmt = connection.prepareStatement(query)) {

	    	        pstmt.setInt(1, userId);
	    	        ResultSet rs = pstmt.executeQuery();

	    	        while (rs.next()) {
	    	            Date date = rs.getDate("ziua"); 
	    	            LocalDate day = date.toLocalDate(); 
	    	            int calories = rs.getInt("calorii");
	    	            caloriiZilnice.put(day, caloriiZilnice.getOrDefault(day, 0) + calories);
	    	        }
	    	    } catch (SQLException e) {
	    	        e.printStackTrace();
	    	    }

	    	    Object[][] data = new Object[caloriiZilnice.size()][2];
	    	    int index = 0;
	    	    for (Map.Entry<LocalDate, Integer> entry : caloriiZilnice.entrySet()) {
	    	        data[index][0] = entry.getKey(); 
	    	        data[index][1] = entry.getValue(); 
	    	        index++;
	    	    }

	    	    return data;
	    }

}
