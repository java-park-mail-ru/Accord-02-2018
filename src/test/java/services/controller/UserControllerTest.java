package services.controller;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import services.dao.UserDAO;
import services.model.ServerResponse;
import services.model.User;


import java.util.ArrayList;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserControllerTest {
    private static final User user = new User();
    private static final HttpHeaders REQUEST_HEADERS = new HttpHeaders();

    @MockBean
    private UserDAO userService;

    @Autowired
    private TestRestTemplate restTemplate;


    @SuppressWarnings("Duplicates")
    @BeforeClass
    public static void setHttpHeaders() {
        final ArrayList<String> origin = new ArrayList<>();
        origin.add("http://127.0.0.1:8000");
        REQUEST_HEADERS.put(HttpHeaders.ORIGIN, origin);

        final ArrayList<String> contentType = new ArrayList<>();
        contentType.add("application/json");
        REQUEST_HEADERS.put(HttpHeaders.CONTENT_TYPE, contentType);
    }

    @Test
    public void testDbConnection() {
        final HttpEntity<User> requestEntity = new HttpEntity<>(REQUEST_HEADERS);
        final ResponseEntity<ServerResponse> response = restTemplate.getForEntity("/connection",
                ServerResponse.class, requestEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Congratulations, its successful connection",
                response.getBody().getMessage());
    }

    @Test
    public void testGetAvatarNotFound() {
        final ResponseEntity<?> getAvatarResponse = restTemplate.
                getForEntity("/avatar/defaultуSuper.gif", null);
        assertEquals(HttpStatus.NOT_FOUND, getAvatarResponse.getStatusCode());
    }

    @Before
    public void setUser() {
        user.setEmail("example_email@mail.ru");
        user.setNickname("example_nickname");
        user.setPassword("example_password");
        user.setRating(0);
    }

    @Test
    public void testRegisterRequiresEmail() {
        user.setEmail("");
        final HttpEntity<User> requestEntity = new HttpEntity<>(user, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/register",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Empty email", response.getBody().getMessage());
    }

    @Test
    public void testLoginRequiresEmail() {
        user.setEmail("");
        final HttpEntity<User> requestEntity = new HttpEntity<>(user, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/login",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Empty email", response.getBody().getMessage());
    }

    @Test
    public void testRegisterSameUserExist() {
        when(userService.register(any())).thenReturn(false);

        final HttpEntity<User> requestEntity = new HttpEntity<>(user, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/register",
                requestEntity, ServerResponse.class);

        System.out.println(response.getBody().getMessage());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User with same email already exists", response.getBody().getMessage());
    }

    @Test
    public void testLoginWrongPassword() {
        when(userService.login(any())).thenReturn(false);

        final HttpEntity<User> requestEntity = new HttpEntity<>(user, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/login",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody().getMessage());
    }
}
