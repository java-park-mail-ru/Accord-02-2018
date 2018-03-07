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


@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@RestController
@CrossOrigin(origins = {"*", "http://localhost:8000"})
public class UserController {
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ERROR_EMAIL = "Empty email";
    private static final String ERROR_PASSWORD = "Empty password";
    private static final String ERROR_NICKNAME = "Empty nickname";
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


    @GetMapping(value = "/connection", produces = "application/json")
    public ResponseEntity<?> connection() {
        return ResponseEntity.status(HttpStatus.OK).body(new ServerResponse("OK",
                "Congratulations, its successful connection"));
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody @NotNull User user, HttpSession httpSession) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerResponse("Error",
                    errorString.toString()));
        }

        if (userService.register(user)) {
            httpSession.setAttribute(SESSION_KEY, user);

            return ResponseEntity.status(HttpStatus.OK).body(new ServerResponse("Ok",
                    "Successful registration"));
        } else {
            // если попали в этот блок
            // значит такой юзер с таким мейлом уже существует
            // поэтому просто вернем ошибку
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerResponse("Error",
                    "Unsuccessful registration"));
        }
    }


    @GetMapping(value = "/getUser", produces = "application/json")
    public ResponseEntity<?> getUser(HttpSession httpSession) {
        final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);

        if (userFromSession != null) {
            final User userForReturn = userService.getUser(userFromSession.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(userForReturn);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new
                    ServerResponse("Error", "You are not login"));
        }
    }


    @PostMapping(value = "/updateUser", produces = "application/json")
    public ResponseEntity<?> update(@RequestBody @NotNull User updateData, HttpSession httpSession) {
        // попробуем найти уже существующие данные
        // о юзере которому хотим обновить данные
        final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);
        final User userForUpdate;

        if (userFromSession != null) {
            userForUpdate = userService.getUser(userFromSession.getEmail());
        } else {
            // если такой юзер не нашелся
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new
                    ServerResponse("Error", "You are not login"));
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
            return ResponseEntity.status(HttpStatus.OK).body(new
                    ServerResponse("Ok", "Successful update"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new
                    ServerResponse("Error", "Unsuccessful update"));
        }

    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody User userToLogin, HttpSession httpSession) {
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

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity<?> logout(HttpSession httpSession) {
        final ServerResponse response = new ServerResponse();

        if (httpSession.getAttribute(SESSION_KEY) != null) {
            httpSession.invalidate();

            response.setStatus("Ok");
            response.setMessage("Successful logout");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.setStatus("Error");
            response.setMessage("Unsuccessful logout");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}