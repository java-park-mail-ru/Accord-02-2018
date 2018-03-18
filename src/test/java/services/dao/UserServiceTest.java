package services.dao;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import services.model.User;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    @Autowired
    private UserDAO userService = new UserDAO();
    @Autowired
    private JdbcTemplate template;
    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;

    @Test
    public void testSimpleCreateUser() {
        final User userToRegister = new User("", LOGIN, PASSWORD, 0);
        if (userService.register(userToRegister)) {
            final User createdUser = userService.getUser(LOGIN);

            assertNotNull(createdUser);
            assertEquals(LOGIN, createdUser.getEmail());
            assertEquals(PASSWORD, createdUser.getPassword());
        }
    }
}