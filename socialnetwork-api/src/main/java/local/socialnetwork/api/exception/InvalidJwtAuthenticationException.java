package local.socialnetwork.api.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -58070890601851667L;

    public InvalidJwtAuthenticationException(String msg) {
        super(msg);
    }
}