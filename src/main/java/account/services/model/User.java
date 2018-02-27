package account.services.model;

public class User {
    private String email;
    private String password;
    private String nickname;
    private Integer rating;

    public User(String nickname, String email, String password, Integer rating) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.rating = rating;
    }

    public User(User user) {
        this.nickname = user.nickname;
        this.password = user.password;
        this.email = user.email;
        this.rating = user.rating;
    }

    public User() {

    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRating() {
        return rating;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
