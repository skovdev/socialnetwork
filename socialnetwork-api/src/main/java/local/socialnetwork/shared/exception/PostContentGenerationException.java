package local.socialnetwork.shared.exception;

/**
 * Thrown when the AI provider fails to generate post content.
 */
public class PostContentGenerationException extends RuntimeException {

    public PostContentGenerationException(String message) {
        super(message);
    }

    public PostContentGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
