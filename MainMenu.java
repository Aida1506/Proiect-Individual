package DietDiary;

import javax.swing.*;

import Autentificare.Login;
import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Meniul principal al aplicatie, care se deschide dupa ce ne autentificam.
 * Avem un frame, username si userId, pe care le retine pentru a putea verifica functii ulterioare unde va trebui sa extraga din tabele date in functie de persoana care este logata.
 */
public class MainMenu {
    private JFrame frame;
    private String username;
    private int userId;

    /**
     * 
     * @param username numele utilizatorului dupa care vom obtine Id-ul si vom face urmatoarele operatii
     * Avem o fereastra principala si un panou de titlu.
     * Sub titlu este meniul cu butoane, care contine actiunile pe care utilizatorul le poate realiza. 
     */
    public MainMenu(String username) {
    	this.username = username;
        this.userId = getUserIdByUsername(username);

        frame = new JFrame("Main Menu");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(255, 228, 225));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 228, 225));
        JLabel titleLabel = new JLabel("Bine ai venit, " + username + "!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(255, 228, 225));

        JButton alimentatie = new JButton("Monitorizare alimentație");
        alimentatie.setMaximumSize(new Dimension(250, 40));
        alimentatie.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        alimentatie.setBackground(new Color(64, 200, 134));
        alimentatie.setForeground(Color.WHITE);
        alimentatie.setFocusPainted(false);
        alimentatie.setFont(new Font("Arial", Font.BOLD, 16));

        JButton greutate = new JButton("Monitorizare greutate");
        greutate.setMaximumSize(new Dimension(250, 40));
        greutate.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        greutate.setBackground(new Color(64, 200, 134));
        greutate.setForeground(Color.WHITE);
        greutate.setFocusPainted(false);
        greutate.setFont(new Font("Arial", Font.BOLD, 16));

        JButton dimensiuni = new JButton("Monitorizare dimensiuni");
        dimensiuni.setMaximumSize(new Dimension(250, 40));
        dimensiuni.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        dimensiuni.setBackground(new Color(64, 200, 134));
        dimensiuni.setForeground(Color.WHITE);
        dimensiuni.setFocusPainted(false);
        dimensiuni.setFont(new Font("Arial", Font.BOLD, 16));

        JButton programari = new JButton("Programări nutriționist");
        programari.setMaximumSize(new Dimension(250, 40));
        programari.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        programari.setBackground(new Color(64, 200, 134));
        programari.setForeground(Color.WHITE);
        programari.setFocusPainted(false);
        programari.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton diagrama = new JButton("Diagrama evoluției");
        diagrama.setMaximumSize(new Dimension(250, 40));
        diagrama.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        diagrama.setBackground(new Color(64, 200, 134));
        diagrama.setForeground(Color.WHITE);
        diagrama.setFocusPainted(false);
        diagrama.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton backButton = new JButton("Logout");
        backButton.setMaximumSize(new Dimension(150, 30));
        backButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        backButton.setBackground(new Color(238, 75, 106));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));

        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(alimentatie);
        buttonPanel.add(Box.createVerticalStrut(10)); 
        buttonPanel.add(greutate);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(dimensiuni);
        buttonPanel.add(Box.createVerticalStrut(10)); 
        buttonPanel.add(programari);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(diagrama);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        alimentatie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MonitorizareAlimentatie(userId);
            }
        });
        
        greutate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MonitorizareGreutate(userId);
            }
        });
        
        dimensiuni.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MonitorizareDimensiuni(userId);
            }
        });
        
        programari.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ProgramariNutritionist(userId);
            }
        });
        
        diagrama.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Diagrama.afiseazaGrafic(userId);
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	frame.dispose();
            	new Login();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Metoda care obtine Id-ul unui utilizator pe baza numelui de utilizator.
     * Se conecteaza la baza de date si cauta id-ul utilizatorului cu username-ul primit ca parametru.
     * @param username numele utilizatorului
     * @return id-ul utilizatorului daca exista, -1 altfel
     */
    private int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = ConexiuneBazaDate.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id"); 
            } else {
                System.out.println("Utilizatorul nu a fost găsit!");
                return -1; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
