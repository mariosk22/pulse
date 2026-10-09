package com.pulse.backend.exception;
import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;
import  org.springframework.http.HttpStatus;
import  org.springframework.http.ResponseEntity;
import  org.springframework.security.authentication.BadCredentialsException;
import  org.springframework.web.bind.MethodArgumentNotValidException;
import  org.springframework.web.bind.annotation.ExceptionHandler;
import  org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

        // Bez tohto handledu by nehandleodovana vynimka skoncila na /error, ktory
        // Spring Security blokuje -> klient by dostal 403 s prazdnym telom namiesto 500.
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleUnexpected(Exception ex){
            log.error("Unhandled exception", ex);
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
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