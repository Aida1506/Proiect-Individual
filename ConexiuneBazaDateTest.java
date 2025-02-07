package TesteUnitare;

import org.junit.jupiter.api.Test;

import BazaDate.ConexiuneBazaDate;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Clasa ConexiuneBazaDataTest creeaza un test care sa verifice conexiunea bazei de date.
 * Se incearca conexiunea la baza de date prin functia getConnection din clasa ConexiuneBazaDate, iar daca aceasta este nula, arunca o exceptie. 
 */
class ConexiuneBazaDateTest {

    @Test
    void testGetConnection() {
        try (Connection connection = ConexiuneBazaDate.getConnection()) {
            assertNotNull(connection, "Conexiunea la baza de date nu ar trebui să fie null.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Testul a eșuat din cauza unei erori de conexiune.");
        }
    }
}
