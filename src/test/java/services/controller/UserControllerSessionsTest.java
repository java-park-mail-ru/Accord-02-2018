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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserControllerSessionsTest {
    private static final String LOGIN = "example_login";
    private static final String NICKNAME = "example_nickname";
    private static final String PASSWORD = "example_password";
    private static final HttpHeaders REQUEST_HEADERS = new HttpHeaders();

    @MockBean
    private UserDAO userService;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeClass
    public static void setHttpHeadersForSession() {
        final List<String> origin = new ArrayList<>();
        origin.add("http://127.0.0.1:8000");
        REQUEST_HEADERS.put(HttpHeaders.ORIGIN, origin);

        final List<String> contentType = new ArrayList<>();
        contentType.add("application/json");
        REQUEST_HEADERS.put(HttpHeaders.CONTENT_TYPE, contentType);
    }

    @Test
    public void testRegisterUserSuccessful() {
        final User userToRegister = new User(NICKNAME, LOGIN, PASSWORD, 0);
        when(userService.register(any())).thenReturn(true);
        when(userService.getUser(anyString())).thenReturn(userToRegister);

        final HttpEntity<User> requestEntity = new HttpEntity<>(userToRegister, REQUEST_HEADERS);

        final ResponseEntity<User> response = restTemplate.postForEntity("/register",
                requestEntity, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        final HttpHeaders responseHeaders = response.getHeaders();
        final User responseUser = response.getBody();

        assertNotNull(responseHeaders);
        final List<String> cookies = responseHeaders.get("Set-Cookie");
        assertNotNull(cookies);
        assertFalse(cookies.isEmpty());

        assertNotNull(responseUser);
        assertEquals(LOGIN, responseUser.getEmail());
    }

    @Test
    public void testLoginUserSuccessful() {
        login();
    }

    @Test
    public void testGetUserSuccessful() {
        final User userToGet = new User(NICKNAME, LOGIN, PASSWORD, 0);
        when(userService.getUser(anyString())).thenReturn(userToGet);

        final List<String> cookies = login();
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<User> getUserResponse = restTemplate.exchange("/getUser", HttpMethod.GET, requestEntity, User.class);

        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        final User responseUser = getUserResponse.getBody();
        assertNotNull(responseUser);
        assertEquals(NICKNAME, responseUser.getNickname());
    }

    @Test
    public void testGetUserRequiresLogin() {
        final ResponseEntity<User> getUserResponse = restTemplate.
                getForEntity("/getUser", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, getUserResponse.getStatusCode());
    }

    @Test
    public void testUpdateAvatarRequiresLogin() {
        final ResponseEntity<User> updateAvatarResponse = restTemplate.
                getForEntity("/updateAvatar", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, updateAvatarResponse.getStatusCode());
    }

    @Test
    public void testUpdateUserRequiresLogin() {
        final User userToUpdate = new User(NICKNAME, LOGIN, PASSWORD, 0);
        final HttpEntity<User> requestEntity = new HttpEntity<>(userToUpdate, REQUEST_HEADERS);

        final ResponseEntity<ServerResponse> response = restTemplate.exchange(
                "/updateUser", HttpMethod.PUT, requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You are not login", response.getBody().getMessage());
    }

    @Test
    public void testLogoutRequiresLogin() {
        final ResponseEntity<ServerResponse> response = restTemplate.exchange(
                "/logout", HttpMethod.DELETE, null, ServerResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You are not login", response.getBody().getMessage());
    }

    @Test
    public void testLogoutSuccessful() {
        final List<String> cookies = login();
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<ServerResponse> response = restTemplate.exchange("/logout", HttpMethod.DELETE,
                requestEntity, ServerResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successful logout", response.getBody().getMessage());
    }

    private List<String> login() {
        final User userToLogin = new User(NICKNAME, LOGIN, PASSWORD, 0);
        when(userService.login(any())).thenReturn(true);
        when(userService.getUser(anyString())).thenReturn(userToLogin);

        final HttpEntity<User> requestEntity = new HttpEntity<>(userToLogin, REQUEST_HEADERS);
        final ResponseEntity<User> loginResponse = restTemplate.postForEntity("/login", requestEntity, User.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        final List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");
        assertNotNull(cookies);
        assertFalse(cookies.isEmpty());

        final User responseUser = loginResponse.getBody();
        assertNotNull(responseUser);
        assertEquals(NICKNAME, responseUser.getNickname());

        return cookies;
    }
}
