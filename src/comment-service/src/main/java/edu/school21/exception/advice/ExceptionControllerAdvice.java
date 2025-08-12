package edu.school21.exception.advice;

import edu.school21.dto.response.ErrorInfoRsDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EntityExistsException.class, MissingRequestHeaderException.class})
    public ErrorInfoRsDto handleBadRequestException(HttpServletRequest req,
                                                    Exception ex) {
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
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfoRsDto handleDataIntegrityViolationException(HttpServletRequest req,
                                                                DataIntegrityViolationException ex) {
        String message = "Ошибка сохранения данных.";
        return new ErrorInfoRsDto(req.getRequestURL().toString(),
                message,
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorInfoRsDto handleEntityNotFoundException(HttpServletRequest req,
                                                        EntityNotFoundException ex) {
        return new ErrorInfoRsDto(req.getRequestURL().toString(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value());
    }
}
