package com.mini.ecommerce.user.adapter.in.web.exception;

import com.mini.ecommerce.user.application.exception.BusinessException;
import com.mini.ecommerce.user.application.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = buildBaseErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
                
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
            
        ErrorResponse errorResponse = buildBaseErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
                
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
            
        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return new ErrorResponse.ValidationError(fieldName, errorMessage);
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = buildBaseErrorResponse(
                HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
        errorResponse.setDetails(validationErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {
            
        ErrorResponse errorResponse = buildBaseErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
                
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildBaseErrorResponse(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
