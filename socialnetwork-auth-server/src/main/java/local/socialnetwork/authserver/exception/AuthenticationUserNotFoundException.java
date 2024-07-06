package local.socialnetwork.authserver.exception;

public class AuthenticationUserNotFoundException extends RuntimeException {

    public AuthenticationUserNotFoundException(String message) {
        super(message);
    }
}
