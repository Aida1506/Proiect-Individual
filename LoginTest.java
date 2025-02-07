package TesteUnitare;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import Autentificare.Login;
import BazaDate.ConexiuneBazaDate;

/**
 * Clasa LoginTest este o clasa care testeaza functionaliatatea paginii de Login.
 */
class LoginTest {
	
	/**
	 * Functie care autentifica un utilizator.
	 * @param username numele de utilizator care va fi verificat
	 * @param password parola utilizatorului
	 * @return false daca utilizatorul nu exista si true daca utilizatorul exista
	 */
	 public boolean authenticateUser(String username, String password) {
	        try (Connection connection = ConexiuneBazaDate.getConnection()) {
	            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
	            PreparedStatement statement = connection.prepareStatement(sql);
	            statement.setString(1, username);
	            statement.setString(2, password);

	            ResultSet resultSet = statement.executeQuery();
	            return resultSet.next(); 
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            return false;
	        }
	    }
	 
	 @Test
	 /**
	  * Testeaza daca un utilizator dat, care deja exista in baza de date, poate fi autentificat. 
	  */
	 void testTrue() {
		 Login login = new Login();
			
		 String testUsername = "aida";
	     String testPassword = "1234";
	     boolean isAuthenticated = authenticateUser(testUsername, testPassword);
	     assertTrue(isAuthenticated, "Autentificarea ar trebui să fie valida");
	 }

	@Test
	/**
	 * Testeaza daca un utilizator invalid este autentificat in baza de date, si trebuie sa returneze fals.
	 */
	void testFalse() {
		Login login = new Login();
		
		 String testUsername = "invalidUser";
	     String testPassword = "invalidPass";
	     boolean isAuthenticated = authenticateUser(testUsername, testPassword);
	     assertFalse(isAuthenticated, "Autentificarea ar trebui să fie invalidă pentru utilizatorul inexistent.");
    }
}


