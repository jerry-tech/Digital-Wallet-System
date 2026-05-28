package com.jerry.wallet.controlleradvice;

import com.jerry.wallet.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ErrorControllerAdvice.class);

    private final MessageSource messageSource;

    public ErrorControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type: " + MediaType.APPLICATION_JSON_VALUE;
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<String>> handle(ChangeSetPersister.NotFoundException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.NOT_FOUND.value());
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }


    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<?> handle(ErrorResponse e) {
        logger.error("ErrorResponse: {}", e.getMessage());
        return ResponseEntity.status(e.getApiResponse().getCode()).body(e.getApiResponse());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.add(err.getField() + ": " + err.getDefaultMessage());
        }

        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setCode(400);
        apiResponse.setMessage(errors.isEmpty() ? "Invalid request" : errors.get(0));
        apiResponse.setData(errors);

        return new ResponseEntity<>(apiResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<String>> handle(Exception e) {
        logger.error("Unhandled exception", e);

        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            message = "Unknown error";
        }

        // Safely truncate message
        int truncationLength = 60;
        String errorDescription =
                message.length() > truncationLength ?
                        message.substring(0, truncationLength) + "..." :
                        message;

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(500);
        apiResponse.setMessage(
                "There was a problem processing your request. " +
                        "Please contact support with this error description [" +
                        errorDescription + "]"
        );

        return ResponseEntity.status(500).body(apiResponse);
    }
}
