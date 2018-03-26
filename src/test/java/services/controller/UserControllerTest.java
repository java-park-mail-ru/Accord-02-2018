package services.controller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import services.dao.UserDAO;
import services.model.ServerResponse;
import services.model.User;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {
    private static final String LOGIN = "example_login";
    private static final String LOGIN_FIRST = "example_login_1";
    private static final String PASSWORD = "example_password";
    private static final int USER_PER_PAGE = 10;
    private static final HttpHeaders REQUEST_HEADERS = new HttpHeaders();

    @MockBean
    private UserDAO userService = new UserDAO();
    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeClass
    public static void setHttpHeaders() {
        final ArrayList<String> origin = new ArrayList<>();
        origin.add("http://localhost:8000");
        REQUEST_HEADERS.put(HttpHeaders.ORIGIN, origin);

        final ArrayList<String> contentType = new ArrayList<>();
        contentType.add("application/json");
        REQUEST_HEADERS.put(HttpHeaders.CONTENT_TYPE, contentType);
    }

    @Test
    public void testDbConnection() {
        final HttpEntity<ServerResponse> requestEntity = new HttpEntity<>(new ServerResponse("Ok", "Some fucking ditch"));

        // TODO разобраться что за шит, все идет по ...
        final ResponseEntity<String> response = restTemplate.postForEntity("/connection", requestEntity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(new ServerResponse("OK",
                "Congratulations, its successful connection"), response.getBody());
    }

    @Test
    public void testRegisterEmptyUser() {
        final User userToRegister = new User("", LOGIN, PASSWORD, 0);
        final HttpEntity<User> requestEntity = new HttpEntity<>(userToRegister, REQUEST_HEADERS);

        // если нет конекта, то получим NullPointerException
        final ResponseEntity<String> response = restTemplate.postForEntity("/register", requestEntity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(new ServerResponse("Error",
                " Empty nickname"), response.getBody());
    }
}
