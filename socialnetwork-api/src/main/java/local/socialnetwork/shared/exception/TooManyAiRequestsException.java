package local.socialnetwork.shared.exception;

import lombok.Getter;

/**
 * Thrown when a user exceeds the allowed number of AI-backed requests within the configured window.
 */
@Getter
public class TooManyAiRequestsException extends RuntimeException {

    private final long retryAfterSeconds;

    public TooManyAiRequestsException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
