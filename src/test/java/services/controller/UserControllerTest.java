package services.controller;


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
    private static final String LOGIN = "example_login";
    private static final String LOGIN_FIRST = "example_login_1";
    private static final String NICKNAME = "example_nickname";
    private static final String PASSWORD = "example_password";
    private static final String WRONG_PASSWORD = "wrong_password";
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
        final ResponseEntity<User> getAvatarResponse = restTemplate.
                getForEntity("/avatar/defaultуSuper.gif", User.class);
        assertEquals(HttpStatus.NOT_FOUND, getAvatarResponse.getStatusCode());
    }

    @Test
    public void testRegisterRequiresEmail() {
        final User userToRegister = new User(NICKNAME, "", PASSWORD, 0);
        final HttpEntity<User> requestEntity = new HttpEntity<>(userToRegister, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/register",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Empty email", response.getBody().getMessage());
    }


    @Test
    public void testRegisterSameUserExist() {
        when(userService.register(any())).thenReturn(false);

        final User userToRegister = new User(LOGIN_FIRST, LOGIN, PASSWORD, 0);
        final HttpEntity<User> requestEntity = new HttpEntity<>(userToRegister, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/register",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User with same email already exists", response.getBody().getMessage());
    }

    @Test
    public void testLoginWrongPassword() {
        when(userService.login(any())).thenReturn(false);

        final User userToLogin = new User(NICKNAME, LOGIN, WRONG_PASSWORD, 0);
        final HttpEntity<User> requestEntity = new HttpEntity<>(userToLogin, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/login",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody().getMessage());
    }

    @Test
    public void testLoginRequiresEmail() {
        final User userToLogin = new User(NICKNAME, "", PASSWORD, 0);
        final HttpEntity<User> requestEntity = new HttpEntity<>(userToLogin, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.postForEntity("/login",
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Empty email", response.getBody().getMessage());
    }
}
