package edu.school21.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.exception.UnauthorizedCommentDeletionException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionControllerAdvice {

    private final ObjectMapper objectMapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnauthorizedCommentDeletionException.class)
    public ErrorInfoRsDto handleUnauthorizedCommentDeletionException(HttpServletRequest req,
                                                                     UnauthorizedCommentDeletionException ex) {
        return new ErrorInfoRsDto(req.getRequestURL().toString(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorInfoRsDto handleMethodArgumentNotValidException(HttpServletRequest req,
                                                                MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ErrorInfoRsDto(req.getRequestURL().toString(),
                errors.values().toString(),
                HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorInfoRsDto handleConstraintViolationException(HttpServletRequest req,
                                                             ConstraintViolationException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            String paramName = violation.getPropertyPath().toString();
            if (paramName.contains(".")) {
                paramName = paramName.substring(paramName.lastIndexOf('.') + 1);
            }
            sb.append(paramName).append(": ").append(violation.getMessage()).append("; ");
        });
        return new ErrorInfoRsDto(req.getRequestURL().toString(),
                sb.toString(),
                HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorInfoRsDto> handleWebClientResponseException(HttpServletRequest req,
                                                                           WebClientResponseException ex) {
        try {
            String responseBody = ex.getResponseBodyAsString();
            ErrorInfoRsDto errorInfo = objectMapper
                    .readValue(responseBody, ErrorInfoRsDto.class);
            return ResponseEntity.status(ex.getStatusCode()).body(errorInfo);
        } catch (Exception parsingException) {
            ErrorInfoRsDto fallbackError = new ErrorInfoRsDto(
                    req.getRequestURL().toString(),
                    ex.getMessage(),
                    ex.getStatusCode().value()
            );
            return ResponseEntity.status(ex.getStatusCode()).body(fallbackError);
        }
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(CallNotPermittedException.class)
    public ErrorInfoRsDto handleCallNotPermittedException(HttpServletRequest req,
                                                          CallNotPermittedException ex) {
        return new ErrorInfoRsDto(req.getRequestURL().toString(),
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value());
    }
}