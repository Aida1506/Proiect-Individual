package Autentificare;

import javax.swing.*;

import BazaDate.ConexiuneBazaDate;
import DietDiary.MainMenu;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Clasa Login este primul ecran care se deschide atunci cand deschidem aplicatia.
 * Are doua campuri, usernameField si passwordField, unde introducem numele si parola pentru a ne loga in aplicatie.
 */
public class Login {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    /**
     * Dimensiunea ecranului este fixa.
     * Culoarea ecranului este roz, la fel si cea a butoanelor.
     * Layout-ul este GridBagLayout.
     * Pe pagina de Login exista campul unde introducem numele utilizatorului si parola.
     * Cand apasam pe butonul de "Login", se retin numele si parola si se incearca conectarea la baza de date.
     * Daca s-a conectat, se introduc datele.
     * Avem si un buton de "Register", care merge catre pagina de inregistrare.
     */
    public Login() {
        frame = new JFrame("Login");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        frame.getContentPane().setBackground(new Color(255, 228, 225));

        JLabel titleLabel = new JLabel("Login");
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

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.setBackground(new Color(64, 200, 134));
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(64, 200, 134));
        registerButton.setForeground(Color.WHITE);

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
        gbc.gridwidth = 2;

        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));

        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(loginButton, gbc);

        gbc.gridy = 4;
        frame.add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try (Connection connection = ConexiuneBazaDate.getConnection()) {
                    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, username);
                    statement.setString(2, password);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        //frame.setVisible(false);
                        new MainMenu(username);
                        frame.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "User sau parola invalide!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Eroare: " + ex.getMessage());
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Register();
                frame.setVisible(false);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
