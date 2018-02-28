package services.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class ServerResponse {
    private String status;
    private String message;


    public ServerResponse() {

    }

    @JsonCreator
    public ServerResponse(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message
    ) {
        this.status = status;
        this.message = message;
    }

    public ServerResponse(ServerResponse serverResponse) {
        this.status = serverResponse.status;
        this.message = serverResponse.message;
    }

    public Object getServerResponse() {
        return this;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}