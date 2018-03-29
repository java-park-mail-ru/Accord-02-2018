package services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.constraints.NotNull;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(@NotNull String errorMessage) {
        super(errorMessage);
    }

    DatabaseConnectionException(DatabaseConnectionException error) {
        super(error);
    }
}
