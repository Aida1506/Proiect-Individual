package TesteUnitare;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

import org.junit.jupiter.api.Test;

import Autentificare.Register;
import BazaDate.ConexiuneBazaDate;

/**
 * Clasa de test care verifica daca dupa ce am inregistrat un utilizator, acesta exista in baza de date
 */
class RegisterTest {
	
	/**
	 * Functie care inregistreaza un utilizator in baza de date
	 * @param username numele utilizatorului
	 * @param password parola utilizatorului
	 * @return true daca acesta a fost inregistrat in baza de date, false altfel
	 */
	public boolean registerUser(String username, String password) {
        try (Connection connection = ConexiuneBazaDate.getConnection()) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return true; 
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; 
        }
    }
	
	/**
	 * Functie care verifica daca un user exista in baza de date
	 * @param username numele de utilizator pe care il verificam
	 * @return true daca acesta exista in baza de date, flase altfel
	 */
	private boolean userExistsInDatabase(String username) {
        try (Connection connection = ConexiuneBazaDate.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            return statement.executeQuery().next(); 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	/**
	 * Functie de test care verifica daca dupa ce am inregistrat un utilizator, acesta este in baza de date
	 */
	@Test
	void testSucces() {
		Register register = new Register();

        String testUsername = "testRegisterUser";
        String testPassword = "testPassword";

       
        boolean isRegistered = registerUser(testUsername, testPassword);
        assertTrue(isRegistered, "Înregistrarea ar trebui să fie validă pentru utilizatorul de test.");

        
        boolean exists = userExistsInDatabase(testUsername);
        assertTrue(exists, "Utilizatorul ar trebui să existe în baza de date după înregistrare.");
	}
	
}


