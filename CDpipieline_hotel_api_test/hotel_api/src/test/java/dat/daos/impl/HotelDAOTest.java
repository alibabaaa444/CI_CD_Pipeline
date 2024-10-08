package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.HotelDTO;
import dat.entities.Hotel;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HotelDAOTest {

    private static HotelDAO hotelDAO;
    private static EntityManagerFactory emf;

    public HotelDAOTest(HotelDAO hotelDAO, EntityManagerFactory emf) {
        this.hotelDAO = hotelDAO;
        this.emf = emf;
    }

    @BeforeAll
    static void setup() {
        // Enable testing mode and use TestContainers for the test environment
        HibernateConfig.setTest(true);

        // Initialize EntityManagerFactory for TestContainers setup
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // Get instance of HotelDAO using the static method
        hotelDAO = HotelDAO.getInstance(emf);
    }

    // Method to populate and add three hotels to the database
    public List<HotelDTO> populate3Hotels(HotelDAO hotelDAO) {
        HotelDTO h1, h2, h3;

        // Create 3 HotelDTO objects
        h1 = new HotelDTO("Grand Hotel", "123 Ocean Avenue", Hotel.HotelType.LUXURY);
        h2 = new HotelDTO("Budget Inn", "456 Elm Street", Hotel.HotelType.BUDGET);
        h3 = new HotelDTO("Business Suites", "789 Pine Road", Hotel.HotelType.BUSINESS);

        // Add hotels to the database using hotelDAO
        h1 = hotelDAO.create(h1);
        h2 = hotelDAO.create(h2);
        h3 = hotelDAO.create(h3);

        // Return them in a list
        return new ArrayList<>(List.of(h1, h2, h3));
    }

    @Test
    void create() {
        // Use the populate3Hotels method to create 3 hotels and persist them
        List<HotelDTO> hotels = populate3Hotels(hotelDAO);

        // Verify that each hotel has been saved by checking if the ID is not null
        for (HotelDTO hotel : hotels) {
            assertNotNull(hotel.getId());
        }

        // You can add additional assertions as needed to verify specific properties
        assertEquals("Grand Hotel", hotels.get(0).getHotelName());
    }

    @AfterAll
    public void cleanUpHotels() {
        // Delete all data from hotel table and reset the sequence for hotel IDs
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Hotel").executeUpdate();  // Deletes all hotel entries
            em.createNativeQuery("ALTER SEQUENCE hotel_hotel_id_seq RESTART WITH 1").executeUpdate();  // Resets the ID sequence
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace(); // Logs any exceptions that occur
        }
    }
}