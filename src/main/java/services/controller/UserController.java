package services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.dao.UserDAO;
import services.model.ServerResponse;
import services.model.User;
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
    public ResponseEntity<ServerResponse> connection() {
        return ResponseEntity.status(HttpStatus.OK).body(new ServerResponse("OK",
                "Congratulations, its successful connection"));
    }

    @PostMapping(value = "/user/register", produces = "application/json")
    public ResponseEntity<ServerResponse> register(@RequestBody @NotNull User user, HttpSession httpSession) {
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (userService.register(user)) {
            httpSession.setAttribute(SESSION_KEY, user);

            response.setStatus("Ok");
            response.setMessage("Successful registration");
            return ResponseEntity.status(HttpStatus.OK).body(new ServerResponse());
        } else {
            // если попали в этот блок
            // значит такой юзер с таким мейлом уже существует
            // поэтому просто вернем ошибку
            response.setStatus("Error");
            response.setMessage("Invalid parameters");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @GetMapping(value = "/user/get")
    public ResponseEntity<User> getUser(HttpSession httpSession) {
        final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);

        if (userFromSession != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userFromSession);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @PostMapping(value = "/user/update")
    public ResponseEntity<String> update(@RequestBody @NotNull User updateData, HttpSession httpSession) {
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
        if (userService.updateUser(userForUpdate)) {
            final ServerResponse response = new ServerResponse("Ok", "Successful update");
            return ResponseEntity.status(HttpStatus.OK).body(response.toString());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

    }

    @PostMapping(value = "/login")
    public ResponseEntity<ServerResponse> login(@RequestBody User userToLogin, HttpSession httpSession) {
        final StringBuilder errorString = new StringBuilder();


        if (isEmptyField(userToLogin.getEmail())) {
            errorString.append(ERROR_EMAIL);
        }

        if (isEmptyField(userToLogin.getPassword()) || userToLogin.getPassword().length() > MAX_LENGTH_PASSWORD) {
            errorString.append(' ' + ERROR_PASSWORD);
        }

        if (errorString.length() > 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerResponse("Error",
                    errorString.toString()));
        }

        if (userService.login(userToLogin)) {
            httpSession.setAttribute(SESSION_KEY, userToLogin);

            return ResponseEntity.status(HttpStatus.OK).body(new ServerResponse("Ok",
                    "Successful login"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerResponse("Error",
                    "Invalid email or password"));
        }
    }


    @PostMapping(value = "/logout")
    public ResponseEntity<ServerResponse> logout(HttpSession httpSession) {
        final ServerResponse response = new ServerResponse();

        if (httpSession.getAttribute(SESSION_KEY) != null) {
            httpSession.invalidate();

            response.setStatus("Ok");
            response.setMessage("Successful logout");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.setStatus("Error");
            response.setMessage("Unsuccessful logout");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
}