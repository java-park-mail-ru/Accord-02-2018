package services.model;

import java.util.List;

public class ScoreBoard {
    private int currentPage;
    private int numberOfPages;
    private List<UserInfo> scoreBoard;

    @SuppressWarnings("unused")
    public ScoreBoard() {
        this.currentPage = 0;
        this.numberOfPages = 0;
        this.scoreBoard = null;
    }

    public ScoreBoard(int currentPage, int numberOfPages, List<UserInfo> usersList) {
        this.currentPage = currentPage;
        this.numberOfPages = numberOfPages;
        this.scoreBoard = usersList;
    }

    @SuppressWarnings("unused")
    public int getcurrentPage() {
        return this.currentPage;
    }

    @SuppressWarnings("unused")
    public int getnumberOfPages() {
        return this.numberOfPages;
    }

    @SuppressWarnings("unused")
    public List<UserInfo> getScoreBoard() {
        return this.scoreBoard;
    }

    @SuppressWarnings("unused")
    public void setcurrentPage(int newCurrentPage) {
        this.currentPage = newCurrentPage;
    }

    @SuppressWarnings("unused")
    public void setnumberOfPages(int newNumberOfPages) {
        this.numberOfPages = newNumberOfPages;
    }

    @SuppressWarnings("unused")
    public void setScoreBoard(List<UserInfo> newListUserInfo) {
        this.scoreBoard = newListUserInfo;
    }
}
