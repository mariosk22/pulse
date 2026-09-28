package com.pulse.backend.exception;
import  org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
        public ResponseEntity<Object>handleApiException(ApiException ex){
            return buildResponse(ex.getStatus(),ex.getMessage());
        }
        @ExceptionHandler(BadCredentialsException.class)
                public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex){
            return buildResponse(HttpStatus.UNAUTHORIZED,"Incorrect email or password");
        }
        @ExceptionHandler(MethodArgumentNotValidException.class)
                public ResponseEntity<Object>handleValidation(MethodArgumentNotValidException ex){
            String message = ex.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(e->e.getField()+":"+e.getDefaultMessage())
                    .orElse("Invalid input data");
            return buildResponse(HttpStatus.BAD_REQUEST, message);
        }


    private ResponseEntity<Object> buildResponse(HttpStatus status,String message){
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp",Instant.now());
        body.put("status",status.value());
        body.put("error",status.getReasonPhrase());
        body.put("message",message);
        return ResponseEntity.status(status).body(body);
    }
}
