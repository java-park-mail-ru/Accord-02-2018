package services.model;

public class UserInfo {
    private String nickname;
    private Integer rating;

    @SuppressWarnings("unused")
    public UserInfo(String nickname, Integer rating) {
        this.nickname = nickname;
        this.rating = rating;
    }

    @SuppressWarnings("unused")
    public UserInfo(UserInfo userInfo) {
        this.nickname = userInfo.nickname;
        this.rating = userInfo.rating;
    }

    public UserInfo() {

    }

    @SuppressWarnings("unused")
    public String getNickname() {
        return nickname;
    }

    @SuppressWarnings("unused")
    public Integer getRating() {
        return rating;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
