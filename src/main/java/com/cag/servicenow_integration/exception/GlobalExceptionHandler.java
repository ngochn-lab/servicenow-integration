package com.cag.servicenow_integration.exception;

import com.cag.servicenow_integration.response.BaseResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(HttpStatus.BAD_REQUEST.toString());
        baseResponse.setMessage(message);
        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException ex) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(HttpStatus.BAD_REQUEST.toString());
        baseResponse.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(baseResponse);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<?> handleAdapter(HttpStatusCodeException ex) {
        int status = ex.getStatusCode().value();
        String msg = "Adapter error: " + ex.getStatusText();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(String.valueOf(ex.getStatusCode().value()));
        baseResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(status).body(baseResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        baseResponse.setMessage("Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }
}
