package services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.dao.UserDAO;
import services.model.ServerResponse;
import services.model.User;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static services.Application.pathAvatarsFolder;
import static services.controller.UserController.SESSION_KEY;

@RestController
@CrossOrigin(origins = {"*", "http://localhost:8000"})
public class AvatarUploadController {

    @Autowired
    private final UserDAO userService = new UserDAO();


    @GetMapping(value = "/upload/avatar")
    public ResponseEntity<?> provideUploadInfo(HttpSession httpSession) {
        final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);

        if (userFromSession != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServerResponse("Ok", "Ready to load your avatar"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ServerResponse("Error", "You are not login"));
        }
    }

    @PostMapping(value = "/upload/avatar")
    public ResponseEntity<?> handleFileUpload(@RequestParam("avatar") MultipartFile file, HttpSession httpSession) {
        final User userFromSession = (User) httpSession.getAttribute(SESSION_KEY);
        String oldFileName = file.getOriginalFilename();
        String typeOfAvatar = oldFileName.substring(oldFileName.lastIndexOf('.'));
        final String nameFile = userFromSession.getId() + '.' + typeOfAvatar;


        if (userFromSession != null) {
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(new File(pathAvatarsFolder, nameFile)));
                    stream.write(bytes);
                    stream.close();

                    userFromSession.setAvatar(nameFile);
                    if (userService.updateAvatar(userFromSession)) {
                        return ResponseEntity.status(HttpStatus.OK).body(new ServerResponse("Ok", "Successful loading"));
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerResponse("Error", "Unsuccessful loading"));
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerResponse("Error", "Bad file"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerResponse("Error", "You are not login"));
        }
    }
}
