package DietDiary;

import javax.swing.*;

import BazaDate.ConexiuneBazaDate;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa pentru diagrama care arata evolutia greutatii in timp.
 * Clasa Diagrama extinde clasa JPanel, care este folosita pentru a desena graficul.
 * dataPoints contine datele utilizatorului, greutatea si data la care a fost inregistrata.
 */
public class Diagrama extends JPanel {

    private List<DataPoint> dataPoints;
    private int userId;  

    /**
     * Constructor care initializeaza user-ul si datele acestuia pe care le preia din baza de date.
     * @param userId Id-ul utilizatorului dupa care sunt extrase datele din tabel
     */
    public Diagrama(int userId) {
        this.userId = userId;
        this.dataPoints = new ArrayList<>();
        dateDinBazaDate();
    }

    /**
     * Functie care preia datele din baza de date.
     */
    private void dateDinBazaDate() {
        try (Connection conn = ConexiuneBazaDate.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ziua, kilograme FROM greutate WHERE user_id = ? ORDER BY ziua")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String data = rs.getString("ziua");
                double greutate = rs.getDouble("kilograme");
                long timestamp = convertesteData(data);
                dataPoints.add(new DataPoint(timestamp, greutate));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Functie care converteste data in din baza intr-o data calendaristica
     * @param dateStr Data care trebuie convertita
     * @return data calendaristica
     */
    private long convertesteData(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Functie care deseneaza graficul.
     * Suprascrie functia paintComponent si are ca parametru variabila g de tip Graphics.
     * Creeaza obiectul g2d de tip Graphics2D si il initializeaza.
     * Sunt setate marginile si dimensiunile graficului.
     * Sunt determinate limitele pentru axele X si Y.
     * Sunt desenate cele doua axe X si Y pe care vor fi puse valorile.
     * Este desenata linia care arata evolutia in timp a greutatii, in functie de datele extrase din tabel.
     * Pentru fiecare punct din tabel este adaugata eticheta acestuia unde este scrisa data si numarul de kilograme.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        setBackground(new Color(255, 228, 225));
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (dataPoints.isEmpty()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margin = 50;
        int width = getWidth() - 2 * margin;
        int height = getHeight() - 2 * margin;

        long minDate = Long.MAX_VALUE;
        long maxDate = Long.MIN_VALUE;
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;

        for (DataPoint point : dataPoints) {
            if (point.timestamp < minDate) minDate = point.timestamp;
            if (point.timestamp > maxDate) maxDate = point.timestamp;
            if (point.weight < minWeight) minWeight = point.weight;
            if (point.weight > maxWeight) maxWeight = point.weight;
        }

        g2d.setColor(new Color(64, 200, 134));
        g2d.drawLine(margin, height + margin, width + margin, height + margin);
        g2d.drawLine(margin, margin, margin, height + margin);
        
        g2d.setColor(new Color(238, 75, 106));
        g2d.drawString("Data", width + margin + 10, height + margin);
        g2d.drawString("Greutate", margin - 40, margin);
        
        for (int i = 1; i < dataPoints.size(); i++) {
            DataPoint prevPoint = dataPoints.get(i - 1);
            DataPoint currentPoint = dataPoints.get(i);

            int prevX = (int) map(prevPoint.timestamp, minDate, maxDate, margin, width + margin);
            int prevY = (int) map(prevPoint.weight, minWeight, maxWeight, height + margin, margin);
            int currX = (int) map(currentPoint.timestamp, minDate, maxDate, margin, width + margin);
            int currY = (int) map(currentPoint.weight, minWeight, maxWeight, height + margin, margin);

            g2d.setColor(new Color(64, 200, 134));
            g2d.drawLine(prevX, prevY, currX, currY);
        }

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (DataPoint point : dataPoints) {
            int x = (int) map(point.timestamp, minDate, maxDate, margin, width + margin);
            int y = (int) map(point.weight, minWeight, maxWeight, height + margin, margin);

            g2d.setColor(new Color(238, 75, 106));
            g2d.drawString(String.format("%.2f kg", point.weight), x + 5, y - 5);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(new java.util.Date(point.timestamp));

            g2d.drawString(formattedDate, x + 5, y + 10);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Evoluția Greutății", width / 2 + margin - 80, margin - 20);
    }

    /**
     * Functie care transforma valoarea data la dimensiunile reale ale graficului.
     * @param weight greutatea
     * @param minWeight greutatea minima
     * @param maxWeight greutatea maxima
     * @param minRange marginea inferioara
     * @param maxRange marginea superioara
     * @return valoarea din interval
     */
    private double map(double weight, double minWeight, double maxWeight, int minRange, int maxRange) {
        return (weight - minWeight) * (maxRange - minRange) / (double) (maxWeight - minWeight) + minRange;
    }

    /**
     * Clasa care reprezinta un punct de pe grafic.
     * Acesta are ca atribute greutatea si data la care aceasta a fost inregistrata.
     */
    private static class DataPoint {
        long timestamp;
        double weight;

        /**
         * Constructor care intializeaza punctul.
         * @param timestamp data inregistrarii
         * @param weight greutatea
         */
        public DataPoint(long timestamp, double weight) {
            this.timestamp = timestamp;
            this.weight = weight;
        }
    }

    /**
     * Functie care deschide graficul.
     * @param userId Id-ul utilizatorului pentru care este creat graficul
     */
    public static void afiseazaGrafic(int userId) {
    	 JFrame frame = new JFrame("Evoluția Greutății");
    	    Diagrama panel = new Diagrama(userId);

    	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	    frame.setSize(400, 300);
    	    frame.setLayout(new BorderLayout()); 
    	    frame.add(panel, BorderLayout.CENTER);

    	    JButton exitButton = new JButton("Ieșire");
    	    exitButton.setBackground(new Color(255, 200, 200)); 
    	    exitButton.setFont(new Font("Arial", Font.BOLD, 14));
    	    exitButton.addActionListener(e -> frame.dispose());

    	    frame.add(exitButton, BorderLayout.SOUTH);

    	    frame.setLocationRelativeTo(null);
    	    frame.setVisible(true);
    }
}
