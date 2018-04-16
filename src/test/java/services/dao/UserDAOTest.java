package services.dao;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import services.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserDAOTest {
    private static final String LOGIN = "example_login";
    private static final String NICKNAME = "example_nickname";
    private static final String PASSWORD = "example_password";
    private static final String UPDATED_PASSWORD = "example_updated_password";
    private static final String UPDATED_AVATAR = "new_avatar.png";
    private static final int USER_PER_PAGE = 10;

    @Autowired
    private UserDAO userService;


    @Test
    public void testSimpleCreateUser() {
        final User userToRegister = new User(NICKNAME, LOGIN, PASSWORD, 0);

        assertTrue(userService.register(userToRegister));
        final User createdUser = userService.getUser(LOGIN);

        assertNotNull(createdUser);
        assertEquals(LOGIN, createdUser.getEmail());
        assertEquals(PASSWORD, createdUser.getPassword());
    }

    @Test
    public void testCreateDuplicatedUser() {
        final User user = new User(NICKNAME, LOGIN, PASSWORD, 0);
        if (userService.register(user)) {
            final User duplicatedUser = new User(NICKNAME, LOGIN, PASSWORD, 0);
            assert (!userService.register(duplicatedUser));
        } else {
            assert false;
        }
    }

    @Test
    public void testLoginUser() {
        final User userToLogin = new User(NICKNAME, LOGIN, PASSWORD, 0);
        assertTrue(userService.register(userToLogin) &&
                userService.login(userToLogin));

        final User createdUser = userService.getUser(LOGIN);
        assertNotNull(createdUser);
    }

    @Test
    public void testGetUser() {
        final User user = new User(NICKNAME, LOGIN, PASSWORD, 0);
        assertTrue(userService.register(user));

        final User existUser = userService.getUser(user.getEmail());
        assertNotNull(existUser);
    }

    @Test
    public void testUpdatetUser() {
        final User user = new User(NICKNAME, LOGIN, PASSWORD, 0);
        assertTrue(userService.register(user));

        user.setPassword(UPDATED_PASSWORD);
        final Boolean updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals(true, updatedUser);
    }

    @Test
    public void testUpdateAvatar() {
        final User user = new User(NICKNAME, LOGIN, PASSWORD, 0);
        assertTrue(userService.register(user));

        user.setAvatar(UPDATED_AVATAR);
        final Boolean updatedUser = userService.updateAvatar(user);

        assertNotNull(updatedUser);
        assertEquals(true, updatedUser);
    }

    @Test
    public void testGetLastPage() {
        final User user = new User(NICKNAME, LOGIN, PASSWORD, 0);
        assertTrue(userService.register(user));

        final Integer lastPage = userService.getLastPage(USER_PER_PAGE);
        assertNotNull(lastPage);
        assertEquals(lastPage.toString(), "1");
    }
}