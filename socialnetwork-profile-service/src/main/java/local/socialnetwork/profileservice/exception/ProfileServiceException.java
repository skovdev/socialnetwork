package local.socialnetwork.profileservice.exception;

import java.io.Serial;

public class ProfileServiceException extends Exception {

    @Serial
    private static final long serialVersionUID = 7569110011273789664L;

    public ProfileServiceException(String message) {
        super(message);
    }
}