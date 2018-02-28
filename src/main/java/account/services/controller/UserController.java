package account.services.controller;

import account.services.dao.UserDAO;
import account.services.model.User;
import org.json.JSONException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
// or "https://smtrepo.herokuapp.com"})
@CrossOrigin({"*"})
public class UserController {
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ERROR_EMAIL = "Error email";
    private static final String ERROR_PASSWORD = "Error password";
    private static final String ERROR_NICKNAME = "Error nickname";


    @Autowired
    private UserDAO userService;

    private boolean isEmptyField(String field) {
        return ((field == null) || field.isEmpty());
    }


    @GetMapping(value = "/connection")
    public String test(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new JSONObject().put("status", "Congratulations, its successful connection").toString();
    }

    @PostMapping(value = "/user/register")
    public String register(@RequestBody @NotNull User user, HttpServletResponse response) throws JSONException {
        JSONObject responseJson = new JSONObject();
        JSONArray arrayErrorsJson = new JSONArray();

        System.out.println(user.getEmail());
        System.out.println(user.getPassword());
        System.out.println(user.getNickname());

        if (isEmptyField(user.getEmail())) {
            arrayErrorsJson.put(ERROR_EMAIL);
        }

        if (isEmptyField(user.getPassword())) {
            arrayErrorsJson.put(ERROR_PASSWORD);
        }

        if (isEmptyField(user.getNickname())) {
            arrayErrorsJson.put(ERROR_NICKNAME);
        }

        if (arrayErrorsJson.length() > 0) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseJson.put("errors", arrayErrorsJson);
            return responseJson.toString();
        }

        try {
            userService.register(user);
            return new JSONObject().put("status", "Ok").toString();
        } catch (DataAccessException error) {
            // если попали в этот блок
            // значит такой юзер с таким мейлом уже существует
            // (email - primary key в БД)
            // поэтому просто вернем ошибку
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseJson.put("error", "Invalid parameters");
            return responseJson.toString();
        }
    }

    @GetMapping(value = "/user/get")
    public String getUser(HttpSession httpSession, HttpServletResponse response) {
        try {
            final User user = (User) httpSession.getAttribute(SESSION_KEY);
            return user.getUser().toString();
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println(e);
            return null;
        }
    }


    @PostMapping(value = "/user/update")
    public String update(@RequestBody @NotNull User updateData, HttpSession httpSession,
                         HttpServletResponse response) throws JSONException {
        try {
            // попробуем найти уже существующие данные
            // о юзере которому хотим обновить данные
            try {
                userService.getUser((User) httpSession.getAttribute(SESSION_KEY));
            } catch (DataAccessException e) {
                // если такой юзер не нашелся
                // то печальбеда - 404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            // обновляем данные если все хорошо
            userService.updateUser(updateData);
            response.setStatus(HttpServletResponse.SC_OK);
            return new JSONObject().put("status", "Ok").toString();
        } catch (DataAccessException e) {
            // произошел конфликт
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @PostMapping(value = "/login")
    public String login(@RequestBody User userToLogin, HttpSession httpSession,
                        HttpServletResponse response) throws JSONException {
        JSONObject responseJson = new JSONObject();
        JSONArray arrayErrorsJson = new JSONArray();

        if (isEmptyField(userToLogin.getEmail())) {
            arrayErrorsJson.put(ERROR_EMAIL);
        }

        if (isEmptyField(userToLogin.getPassword())) {
            arrayErrorsJson.put(ERROR_PASSWORD);
        }

        if (arrayErrorsJson.length() > 0) {
            responseJson.put("error", arrayErrorsJson);
            return responseJson.toString();
        }

        if (userService.login(userToLogin)) {
            httpSession.setAttribute(SESSION_KEY, userToLogin);
            responseJson.put("status", "Ok");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseJson.put("error", "Invalid email or password");
        }

        return responseJson.toString();
    }


    @PostMapping(value = "/logout")
    public String logout(HttpSession httpSession) throws JSONException {
        JSONObject responseJson = new JSONObject();

        if (httpSession.getAttribute(SESSION_KEY) != null) {
            httpSession.invalidate();
            responseJson.put("status", "Ok");
        } else {
            responseJson.put("error", "Unsuccesful logout");
        }

        return responseJson.toString();
    }
}