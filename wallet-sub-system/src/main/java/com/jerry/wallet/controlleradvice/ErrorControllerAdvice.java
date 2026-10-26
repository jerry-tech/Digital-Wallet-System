package com.jerry.wallet.controlleradvice;

import com.jerry.wallet.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ErrorControllerAdvice.class);

    private final MessageSource messageSource;

    public ErrorControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private ResponseEntity<ApiResponse<String>> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status.value(), message));
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


    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<String>> handleConflict(OptimisticLockingFailureException e) {
        return build(HttpStatus.CONFLICT, "The wallet was modified concurrently, please retry");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)          // @Max/@Min on @RequestParam (Spring 6.1+)
    public ResponseEntity<ApiResponse<String>> handleParamValidation(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isBlank() ? "Invalid request" : message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleUnreadable(HttpMessageNotReadableException e) {
        return build(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + e.getName() + "'");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handle(Exception e) {
        String reference = UUID.randomUUID().toString();
        logger.error("Unhandled exception ref={}", reference, e);          // details stay in the logs
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "There was a problem processing your request. Please contact support with reference " + reference);
    }

}
