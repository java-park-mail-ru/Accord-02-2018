package UserServices.Controller;

import UserServices.Model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
// or "https://smtrepo.herokuapp.com"})
@CrossOrigin({"*"})
public class UserController {
    private static final String SESSIONKEY = "sessionkey";
    private static final ObjectMapper mapperData = new ObjectMapper();
    @Autowired
    private UserServices.DAO.UserDAO userTemplate;


    @PostMapping(value = "/api/user/register")
    public String register(@RequestBody @NotNull User user, HttpServletResponse response) {
        System.out.println("/api/user/register" + user.getNickname());
        try {
            userTemplate.register(user.getEmail(), user.getNickname(), user.getPassword());
            response.setStatus(HttpServletResponse.SC_OK);

            return userTemplate.getUser(user.getNickname()).toObjectNode(mapperData).toString();
        } catch (DataAccessException error) {
            // если попали в этот блок
            // значит такой юзер уже существует
            // поэтому просто вернем ошибку
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GetMapping(value = "/api/user/get")
    public User getUser(HttpSession httpSession, HttpServletResponse response) {
        try {
            final User user = (User) httpSession.getAttribute(SESSIONKEY);
            response.setStatus(HttpServletResponse.SC_OK);
            return user;
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println(e);
            return null;
        }
    }

    @PostMapping(value = "/api/user/update")
    public User update(@RequestBody @NotNull User updateData, HttpServletResponse response) {
        try {
            try {
                userTemplate.getUser(updateData.getNickname());
            } catch (DataAccessException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            userTemplate.updateUser(updateData);
            response.setStatus(HttpServletResponse.SC_OK);
            return userTemplate.getUser(updateData.getNickname());
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody User userToLogin, HttpSession httpSession) {
        final ResponseEntity<?> result = ResponseEntity.status(getStatus(userTemplate.login(userToLogin))).body("Trying to login");
        setHttpSession((ResponseEntity<String>) result, httpSession, userToLogin);

        return result;
    }


    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body("You successfully logout");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You unsuccessful logout");
        }
    }


    public HttpStatus getStatus(Boolean flag) {
        return flag ? HttpStatus.OK : HttpStatus.FORBIDDEN;
    }

    public void setHttpSession(ResponseEntity<String> result, HttpSession httpSession, User body) {
        if (result.getStatusCode() == HttpStatus.OK) {
            httpSession.setAttribute(SESSIONKEY, body);
        }
    }
}