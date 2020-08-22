package local.socialnetwork.api.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOldPasswordException extends RuntimeException {

    private static final long serialVersionUID = 2351280171207787018L;

    public InvalidOldPasswordException(String message) {
        super(message);
    }
}