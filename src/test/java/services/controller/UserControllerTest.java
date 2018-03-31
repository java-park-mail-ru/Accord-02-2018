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

import static org.junit.Assert.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class UserControllerTest {
    private static final int PORT = 5001;
    private static final String LOGIN = "example_login";
    private static final String LOGIN_FIRST = "example_login_1";
    private static final String PASSWORD = "example_password";
    private static final int USER_PER_PAGE = 10;
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
