package services.controller;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import services.dao.UserDAO;
import services.model.ScoreBoard;
import services.model.User;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ScoreBoardControllerTest {
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
    public void testGetLeadersEmpty() {
        final HttpEntity<User> requestEntity = new HttpEntity<>(REQUEST_HEADERS);
        final ResponseEntity<ScoreBoard> response = restTemplate.getForEntity("/scoreboard/1",
                ScoreBoard.class, requestEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().getScoreBoard().isEmpty());
    }
}
