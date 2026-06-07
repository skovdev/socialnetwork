package local.socialnetwork.shared.exception.handler;

import local.socialnetwork.dto.api.response.ApiResponseDto;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.TokenExpiredException;
import local.socialnetwork.shared.exception.TokenNotFoundException;
import local.socialnetwork.shared.exception.EmailDeliveryException;
import local.socialnetwork.shared.exception.TokenAlreadyUsedException;
import local.socialnetwork.shared.exception.EmailAlreadyExistsException;
import local.socialnetwork.shared.exception.AccountNotVerifiedException;
import local.socialnetwork.shared.exception.UsernameAlreadyExistsException;
import local.socialnetwork.shared.exception.InvalidJwtAuthenticationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @see UsernameAlreadyExistsException */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        log.warn("Username conflict: {}", ex.getMessage(), ex);
        return error(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS", ex.getMessage());
    }

    /** @see EmailAlreadyExistsException */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Email conflict: {}", ex.getMessage(), ex);
        return error(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", ex.getMessage());
    }

    /** @see EmailDeliveryException */
    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<ApiResponseDto<?>> handleEmailDelivery(EmailDeliveryException ex) {
        log.error("Email delivery failed: {}", ex.getMessage(), ex);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "EMAIL_DELIVERY_FAILED",
                "Email delivery failed. Please try again later.");
    }

    /** @see InvalidJwtAuthenticationException */
    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleInvalidJwt(InvalidJwtAuthenticationException ex) {
        log.warn("Invalid JWT: {}", ex.getMessage(), ex);
        return error(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token.");
    }

    /** @see BadCredentialsException */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<?>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage(), ex);
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password.");
    }

    /** @see UsernameNotFoundException */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Username not found: {}", ex.getMessage(), ex);
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password.");
    }

    /** @see AccountNotVerifiedException */
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponseDto<?>> handleAccountNotVerified(AccountNotVerifiedException ex) {
        log.warn("Account not verified: {}", ex.getMessage(), ex);
        return error(HttpStatus.FORBIDDEN, "ACCOUNT_NOT_VERIFIED", ex.getMessage());
    }

    /** @see UserNotFoundException */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage(), ex);
        return error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    /** @see TokenNotFoundException */
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleTokenNotFound(TokenNotFoundException ex) {
        log.warn("Token not found: {}", ex.getMessage(), ex);
        return error(HttpStatus.NOT_FOUND, "TOKEN_NOT_FOUND", ex.getMessage());
    }

    /** @see TokenExpiredException */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponseDto<?>> handleTokenExpired(TokenExpiredException ex) {
        log.warn("Token expired: {}", ex.getMessage(), ex);
        return error(HttpStatus.GONE, "TOKEN_EXPIRED", ex.getMessage());
    }

    /** @see TokenAlreadyUsedException */
    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<ApiResponseDto<?>> handleTokenAlreadyUsed(TokenAlreadyUsedException ex) {
        log.warn("Token already used: {}", ex.getMessage(), ex);
        return error(HttpStatus.GONE, "TOKEN_ALREADY_USED", ex.getMessage());
    }

    /**
     * Handles Jakarta Bean Validation failures from {@code @Valid}-annotated request bodies.
     * All field errors are collected and returned in a single response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("Validation failed: {}", errors, ex);
        return ResponseEntity.badRequest()
                .body(ApiResponseDto.buildErrorResponse(
                        HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED", "Validation failed", errors));
    }

    /**
     * Catch-all handler for unexpected runtime exceptions.
     * The cause is logged server-side; only a generic message is returned to the caller.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<ApiResponseDto<?>> error(HttpStatus status, String errorCode, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponseDto.buildErrorResponse(status.value(), errorCode, message, List.of()));
    }

}
