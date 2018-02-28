package services.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({"unused", "StringBufferReplaceableByString"})
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

    @Override
    public String toString(){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n").append("\"status\": ")
                .append('\"').append(this.status).append("\",").append('\n')
                .append("\"message\": ")
                .append('\"').append(this.message).append('\"').append("\n}");

        return stringBuilder.toString();
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
