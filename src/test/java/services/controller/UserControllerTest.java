package services.controller;


import org.junit.Assert;
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
import services.model.ScoreBoard;
import services.model.ServerResponse;
import services.model.User;


import java.util.ArrayList;


import static org.junit.Assert.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserControllerTest {
//    private static final int PORT = 5001;
//    private static final String LOGIN = "example_login";
//    private static final String LOGIN_FIRST = "example_login_1";
//    private static final String PASSWORD = "example_password";
//    private static final int USER_PER_PAGE = 10;
    private static final HttpHeaders REQUEST_HEADERS = new HttpHeaders();

    @MockBean
    private UserDAO userService;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeClass
    public static void setHttpHeaders() {
        final ArrayList<String> origin = new ArrayList<>();
        origin.add("http://localhost:" + "8000");
        REQUEST_HEADERS.put(HttpHeaders.ORIGIN, origin);

        final ArrayList<String> contentType = new ArrayList<>();
        contentType.add("application/json");
        REQUEST_HEADERS.put(HttpHeaders.CONTENT_TYPE, contentType);
    }

    @Test
    public void testDbConnection() {
        final HttpEntity<User> requestEntity = new HttpEntity<>(REQUEST_HEADERS);
        final ResponseEntity<ServerResponse> response = restTemplate.getForEntity("/connection", ServerResponse.class, requestEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Congratulations, its successful connection", response.getBody().getMessage());
    }

    @Test
    public void testGetUserRequiresLogin() {
        final ResponseEntity<User> getUserResponse = restTemplate.getForEntity("/getUser", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, getUserResponse.getStatusCode());
    }

    @Test
    public void testUpdateAvatarRequiresLogin() {
        final ResponseEntity<User> updateAvatarResponse = restTemplate.getForEntity("/updateAvatar", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, updateAvatarResponse.getStatusCode());
    }

    @Test
    public void testGetAvatarNotFound() {
        final ResponseEntity<User> getAvatarResponse = restTemplate.getForEntity("/avatar/defaultумвдльдм.jpg", User.class);
        assertEquals(HttpStatus.NOT_FOUND, getAvatarResponse.getStatusCode());
    }

    @Test
    public void testGetLeadersEmpty() {
        final HttpEntity<User> requestEntity = new HttpEntity<>(REQUEST_HEADERS);
        final ResponseEntity<ScoreBoard> response = restTemplate.getForEntity("/scoreboard/1", ScoreBoard.class, requestEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().getScoreBoard().isEmpty());
    }

//    @Test
//    public void testLogin() {
//        login();
//    }
//
//    private List<String> login() {
//        when(userService.ensureUserExists(anyString())).thenReturn(new User("tester"));
//
//        final ResponseEntity<User> loginResp = restTemplate.postForEntity("/login", null, User.class);
//        assertEquals(HttpStatus.OK, loginResp.getStatusCode());
//
//        final List<String> cookies = loginResp.getHeaders().get("Set-Cookie");
//        assertNotNull(cookies);
//        assertFalse(cookies.isEmpty());
//
//        final User user = loginResp.getBody();
//        assertNotNull(user);
//        assertEquals("tester", user.getNickname());
//
//        return cookies;
//    }

//    @Test
//    public void testRegisterEmptyUser() {
//        final User userToRegister = new User("", LOGIN, PASSWORD, 0);
//        final HttpEntity<User> requestEntity = new HttpEntity<>(userToRegister, REQUEST_HEADERS);
//
//        final ResponseEntity<String> response = restTemplate.postForEntity("/register",
//                requestEntity, String.class);
//
////        ResponseEntity<ServerResponse> response = restTemplate.exchange(
////                "http://localhost:" + PORT + "/register", HttpMethod.POST, requestEntity, ServerResponse.class);
////
//////        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//////        assertEquals(" Empty nickname", response.getBody().getMessage());
//    }
}
