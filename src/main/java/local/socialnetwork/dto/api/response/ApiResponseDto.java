package local.socialnetwork.dto.api.response;

import org.springframework.http.HttpStatus;

import java.util.List;

import java.time.Instant;

public record ApiResponseDto<T>(
        Instant timestamp,
        int status,
        String message,
        String errorCode,
        T data,
        List<String> errors) {

    public static <T> ApiResponseDto<T> buildSuccessResponse(T data) {
        return new ApiResponseDto<>(
                Instant.now(), HttpStatus.OK.value(), "Request was successful", null, data, List.of());
    }

    public static <T> ApiResponseDto<T> buildErrorResponse(int status, String errorCode, String message, List<String> errors) {
        return new ApiResponseDto<>(Instant.now(), status, message, errorCode, null, errors);
    }
}
