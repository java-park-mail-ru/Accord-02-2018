package services.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

@SuppressWarnings("unused")
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

    @JsonCreator
    public User(
            @JsonProperty("email") String email,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("rating") Integer rating
    ) {
        this.email = email;
        this.nickname = nickname;
        this.rating = rating;
    }

    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put("rating", this.rating);
        json.put("nickname", this.nickname);
        json.put("email", this.email);

        return json;
    }

    public Object getUser() {
        return this;
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
