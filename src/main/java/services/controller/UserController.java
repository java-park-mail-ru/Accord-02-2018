package services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.dao.UserDAO;
import services.model.ServerResponse;
import services.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
@CrossOrigin(origins = {"*", "http://localhost:8000"})
public class UserController {
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ERROR_EMAIL = "Error email";
    private static final String ERROR_PASSWORD = "Error password";
    private static final String ERROR_NICKNAME = "Error nickname";
    private static final int MAX_LENGTH_PASSWORD = 255;

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private final UserDAO userService = new UserDAO();

    private boolean isEmptyField(String field) {
        return ((field == null) || field.isEmpty());
    }

    private boolean isValidField(Integer field) {
        return ((field != null) && (field >= 0));
    }


    @GetMapping(value = "/connection")
    public ResponseEntity<String> connection() {
        final ServerResponse response = new ServerResponse("OK", "Congratulations, its successful connection");
        return ResponseEntity.status(HttpStatus.OK).body(response.getServerResponse().toString());
    }

    @PostMapping(value = "/user/register")
    public ResponseEntity<String> register(@RequestBody @NotNull User user) {
        final ServerResponse response = new ServerResponse();
        final StringBuilder errorString = new StringBuilder();

        if (isEmptyField(user.getEmail())) {
            errorString.append(ERROR_EMAIL);
        }

        if (isEmptyField(user.getPassword())) {
            errorString.append(' ' + ERROR_PASSWORD);
        }

        if (isEmptyField(user.getNickname())) {
            errorString.append(' ' + ERROR_NICKNAME);
        }

        if (errorString.length() > 0) {
            response.setStatus("Error");
            response.setMessage(errorString.toString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getServerResponse().toString());
        }

        if (userService.register(user)) {
            response.setStatus("Ok");
            response.setMessage("Successful registration");
            return ResponseEntity.status(HttpStatus.OK).body(response.getServerResponse().toString());
        } else {
            // если попали в этот блок
            // значит такой юзер с таким мейлом уже существует
            // поэтому просто вернем ошибку
            response.setStatus("Error");
            response.setMessage("Invalid parameters");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getServerResponse().toString());
        }
    }

    @GetMapping(value = "/user/get")
    public ResponseEntity<String> getUser(HttpSession httpSession) {
        final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);

        if (userFromSession != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userFromSession.getUser().toString());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @PostMapping(value = "/user/update")
    public ResponseEntity<String> update(@RequestBody @NotNull User updateData, HttpSession httpSession) {
        try {
            // попробуем найти уже существующие данные
            // о юзере которому хотим обновить данные
            final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);
            final User userForUpdate;

            if (userFromSession != null) {
                userForUpdate = userService.getUser(userFromSession.getEmail());
            } else {
                // если такой юзер не нашелся
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // переместим значения ненулевых полей
            if (isValidField(updateData.getRating())) {
                userForUpdate.setRating(updateData.getRating());
            }

            if (!isEmptyField(updateData.getPassword())) {
                userForUpdate.setPassword(updateData.getPassword());
            }

            if (!isEmptyField(updateData.getNickname())) {
                userForUpdate.setNickname(updateData.getNickname());
            }

            // обновляем данные если все хорошо
            userService.updateUser(userForUpdate);
            final ServerResponse response = new ServerResponse("Ok", "Successful update");
            return ResponseEntity.status(HttpStatus.OK).body(response.getServerResponse().toString());
        } catch (DataAccessException e) {
            // произошел конфликт
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody User userToLogin, HttpSession httpSession) {
        final ServerResponse response = new ServerResponse();
        final StringBuilder errorString = new StringBuilder();


        if (isEmptyField(userToLogin.getEmail())) {
            errorString.append(ERROR_EMAIL);
        }

        if (isEmptyField(userToLogin.getPassword()) || userToLogin.getPassword().length() > MAX_LENGTH_PASSWORD) {
            errorString.append(' ' + ERROR_PASSWORD);
        }

        if (errorString.length() > 0) {
            response.setStatus("Error");
            response.setMessage(errorString.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getServerResponse().toString());
        }

        if (userService.login(userToLogin)) {
            httpSession.setAttribute(SESSION_KEY, userToLogin);

            response.setStatus("Ok");
            response.setMessage("Successful login");
            return ResponseEntity.status(HttpStatus.OK).body(response.getServerResponse().toString());
        } else {
            response.setStatus("Error");
            response.setMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getServerResponse().toString());
        }
    }


    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(HttpSession httpSession) {
        final ServerResponse response = new ServerResponse();

        if (httpSession.getAttribute(SESSION_KEY) != null) {
            httpSession.invalidate();

            response.setStatus("Ok");
            response.setMessage("Successful logout");
            return ResponseEntity.status(HttpStatus.OK).body(response.getServerResponse().toString());
        } else {
            response.setStatus("Error");
            response.setMessage("Unsuccessful logout");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getServerResponse().toString());
        }
    }
}