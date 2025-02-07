package Autentificare;

import javax.swing.*;

import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clasa care inregistreaza utilizatorii noi.
 * Avem un frame, camp pentru a introduce numele de utilizator, camp pentru parola si unul pentru a confirma parola.
 */
public class Register {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    /**
     * Fereastra Register are 3 campuri, pentru nume, parola si confirmarea parolei.
     * Avem butonul care inregistreaza utilizatorul si butonul care ne duce inapoi la pagina de Login.
     * La apasarea butonului de register, se incearca conectarea la baza de date.
     * Daca reuseste, se verifica daca utilizatorul exista deja.
     * Daca nu exista, este inserat in baza de date si aplicatia ne duce inapoi pe pagina de login.
     * Daca apasam butonul "Back to Login" suntem redirectionati inapoi catre pagina de login.
     */
    public Register() {
        frame = new JFrame("Register");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        frame.getContentPane().setBackground(new Color(255, 228, 225));

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(238, 75, 106)); 
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER; 
        frame.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        registerButton.setBackground(new Color(64, 200, 134));
        registerButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(64, 200, 134));
        backButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(usernameLabel, gbc);

        gbc.gridx = 1;
        frame.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        frame.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        registerButton.setPreferredSize(new Dimension(100, 30));
        backButton.setPreferredSize(new Dimension(150, 30));

        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(registerButton, gbc);

        gbc.gridy = 5;
        frame.add(backButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(frame, "Parolele nu se potrivesc");
                } else {
                    try (Connection connection = ConexiuneBazaDate.getConnection()) {
                        creareTabel(connection);

                        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
                        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                        checkStatement.setString(1, username);

                        ResultSet resultSet = checkStatement.executeQuery();
                        resultSet.next();
                        if (resultSet.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(frame, "Acest username exista deja");
                            return;
                        }

                        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setString(1, username);
                        insertStatement.setString(2, password);
                        insertStatement.executeUpdate();

                        JOptionPane.showMessageDialog(frame, "Inregistrare cu succes");
                        new Login();
                        frame.setVisible(false);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Login();
                frame.setVisible(false);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    
    /**
     * 
     * @param connection Conexiunea catre baza de date
     * @throws SQLException 
     * Functie care verifica daca baza de date exista, iar daca nu, o creeaza, si creeaza tabelul users.
     */
    private void creareTabel(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getCatalogs();

        boolean databaseExists = false;
        while (resultSet.next()) {
            if ("user_db".equals(resultSet.getString(1))) {
                databaseExists = true;
                break;
            }
        }

        if (!databaseExists) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE user_db");
            System.out.println("Baza de date a fost creata");

            stmt.executeUpdate("USE user_db");

            String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "username VARCHAR(255) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL"
                    + ")";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Tabelul 'users' a fost creat");
        } else {
            System.out.println("Baza de date exista deja");
        }
    }
}
