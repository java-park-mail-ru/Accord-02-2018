package services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import services.dao.UserInfoDAO;
import services.model.ScoreBoard;
import services.model.ServerResponse;
import services.model.UserInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@CrossOrigin(origins = {"*", "http://localhost:8000"})
public class ScoreBoardController {
    private static final int USER_PER_PAGE = 10;

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private final UserInfoDAO userInfoService = new UserInfoDAO();


    @PostMapping(path = "/scoreboard/{page}")
    public ResponseEntity<?> getLeaders(@PathVariable("page") @NotNull Integer page) {
        final int numberOfPages = userInfoService.getLastPage(USER_PER_PAGE);

        if (page == null || page < 1) {
            page = 1;
        } else {
            if (page > numberOfPages) {
                page = numberOfPages;
            }
        }

        final List<UserInfo> userInfoList = userInfoService.getSortedUsersInfoByRating(USER_PER_PAGE, page);
        if (userInfoList != null) {
            final ScoreBoard scoreBoardResponse = new ScoreBoard(page, numberOfPages, userInfoList);
            return ResponseEntity.status(HttpStatus.OK).body(scoreBoardResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerResponse(
                    "Error", "Unsuccessful try"));
        }
    }
}
