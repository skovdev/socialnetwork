package local.socialnetwork.shared.exception.handler;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.TokenExpiredException;

import org.springframework.dao.DataIntegrityViolationException;

import local.socialnetwork.shared.exception.TokenNotFoundException;
import local.socialnetwork.shared.exception.EmailDeliveryException;
import local.socialnetwork.shared.exception.TokenAlreadyUsedException;
import local.socialnetwork.shared.exception.EmailAlreadyExistsException;
import local.socialnetwork.shared.exception.AccountNotVerifiedException;
import local.socialnetwork.shared.exception.UsernameAlreadyExistsException;
import local.socialnetwork.shared.exception.InvalidCurrentPasswordException;
import local.socialnetwork.shared.exception.AccountAlreadyVerifiedException;
import local.socialnetwork.shared.exception.InvalidJwtAuthenticationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Centralised exception-to-HTTP-response mapping. All error responses use
 * {@code application/problem+json} (RFC 7807) with an {@code errorCode} extension property.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "CONFLICT", "A resource with the same unique field already exists.");
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        log.warn("Username conflict: {}", ex.getMessage(), ex);
        return problem(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Email conflict: {}", ex.getMessage(), ex);
        return problem(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<ProblemDetail> handleEmailDelivery(EmailDeliveryException ex) {
        log.error("Email delivery failed: {}", ex.getMessage(), ex);
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "EMAIL_DELIVERY_FAILED",
                "Email delivery failed. Please try again later.");
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleInvalidJwt(InvalidJwtAuthenticationException ex) {
        log.warn("Invalid JWT: {}", ex.getMessage(), ex);
        return problem(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage(), ex);
        return problem(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password.");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Username not found: {}", ex.getMessage(), ex);
        return problem(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password.");
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ProblemDetail> handleAccountNotVerified(AccountNotVerifiedException ex) {
        log.warn("Account not verified: {}", ex.getMessage(), ex);
        return problem(HttpStatus.FORBIDDEN, "ACCOUNT_NOT_VERIFIED", ex.getMessage());
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<ProblemDetail> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
        log.warn("Account already verified: {}", ex.getMessage(), ex);
        return problem(HttpStatus.CONFLICT, "ACCOUNT_ALREADY_VERIFIED", ex.getMessage());
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCurrentPassword(InvalidCurrentPasswordException ex) {
        log.warn("Invalid current password: {}", ex.getMessage(), ex);
        return problem(HttpStatus.BAD_REQUEST, "INVALID_CURRENT_PASSWORD", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage(), ex);
        return problem(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTokenNotFound(TokenNotFoundException ex) {
        log.warn("Token not found: {}", ex.getMessage(), ex);
        return problem(HttpStatus.NOT_FOUND, "TOKEN_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handleTokenExpired(TokenExpiredException ex) {
        log.warn("Token expired: {}", ex.getMessage(), ex);
        return problem(HttpStatus.GONE, "TOKEN_EXPIRED", ex.getMessage());
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<ProblemDetail> handleTokenAlreadyUsed(TokenAlreadyUsedException ex) {
        log.warn("Token already used: {}", ex.getMessage(), ex);
        return problem(HttpStatus.GONE, "TOKEN_ALREADY_USED", ex.getMessage());
    }

    /**
     * Collects all field-level validation failures from {@code @Valid} into a single response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("Validation failed: {}", errors, ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setProperty("errorCode", "VALIDATION_FAILED");
        problemDetail.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }

    /**
     * Catch-all for unexpected exceptions. The cause is logged; only a generic message is returned.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String errorCode, String detail) {
        var pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("errorCode", errorCode);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }
}
