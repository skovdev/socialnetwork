package local.socialnetwork.dto.api.response;

import org.springframework.http.HttpStatus;

import java.util.List;

import java.time.Instant;

/**
 * Generic API response envelope used for successful responses.
 *
 * @param timestamp the time the response was produced
 * @param status    the HTTP status code (mirrors the actual HTTP status)
 * @param message   a human-readable summary
 * @param errorCode machine-readable error code (null on success)
 * @param data      the response payload (null for void operations such as register)
 * @param errors    list of field validation errors (empty on success)
 */
public record ApiResponseDto<T>(
        Instant timestamp,
        int status,
        String message,
        String errorCode,
        T data,
        List<String> errors) {

    /**
     * Builds a success envelope. {@code data} may be {@code null} for void operations.
     */
    public static <T> ApiResponseDto<T> buildSuccessResponse(T data) {
        return new ApiResponseDto<>(
                Instant.now(), HttpStatus.OK.value(), "Request was successful", null, data, List.of());
    }

}
