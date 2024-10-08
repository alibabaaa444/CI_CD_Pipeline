package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.daos.impl.HotelDAO;
import dat.daos.impl.HotelDAOTest; // Import the HotelDAOTest
import dat.dtos.HotelDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelRouteTest {

    private static Javalin app;
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static String BASE_URL = "http://localhost:7070/api/v1/hotels"; // Base URL for hotel routes
    private static HotelDAO hotelDao = HotelDAO.getInstance(emf);
    private static HotelDAOTest hotelDAOTest = new HotelDAOTest(hotelDao, emf);

    private HotelDTO h1, h2, h3; // Variables to hold hotel DTOs
    private List<HotelDTO> hotels; // List of hotels for testing

    @BeforeAll
    void init() {
        app = ApplicationConfig.startServer(7070); // Start the Javalin server on port 7070
        HibernateConfig.setTest(true); // Set Hibernate to testing mode
    }

    @BeforeEach
    void setUp() {
        hotels = hotelDAOTest.populate3Hotels(hotelDao); // Populate three hotels before each test
        h1 = hotels.get(0); // Assign the first hotel
        h2 = hotels.get(1); // Assign the second hotel
        h3 = hotels.get(2); // Assign the third hotel
    }

    @AfterEach
    void tearDown() {
        hotelDAOTest.cleanUpHotels(); // Clean up hotels after each test
    }

    @AfterAll
    void closeDown() {
        ApplicationConfig.stopServer(app); // Stop the server after all tests are complete
    }

    @Test
    void testGetHotel() {
        // Test getting a single hotel by ID
        HotelDTO hotel =
                given()
                        .when()
                        .get(BASE_URL + "/" + h1.getId()) // Use the ID of the first populated hotel
                        .then()
                        .log().all()
                        .statusCode(200) // Expect HTTP 200 OK
                        .extract()
                        .as(HotelDTO.class); // Extract response as HotelDTO

        assertThat(hotel, equalTo(h1)); // Validate that the returned hotel matches the expected hotel
    }

    @Test
    void testGetHotels() {
        // Test getting all hotels
        HotelDTO[] hotelsArray =
                given()
                        .when()
                        .get(BASE_URL) // Fetch all hotels
                        .then()
                        .log().all()
                        .statusCode(200) // Expect HTTP 200 OK
                        .extract()
                        .as(HotelDTO[].class); // Extract response as an array of HotelDTO

        assertEquals(3, hotelsArray.length); // Check that three hotels were returned
        assertThat(hotelsArray, arrayContainingInAnyOrder(h1, h2, h3)); // Validate the returned hotels match the populated hotels
    }
}
