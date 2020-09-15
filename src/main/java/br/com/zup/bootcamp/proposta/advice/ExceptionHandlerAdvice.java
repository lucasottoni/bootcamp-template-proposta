package br.com.zup.bootcamp.proposta.advice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handle(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<String> messages = new ArrayList<>();

        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            messages.add(String.format("Field: %s Error: %s", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        ErrorMessage errorMessage = new ErrorMessage(messages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorMessage> handle(ResponseStatusException responseStatusException) {
        ErrorMessage errorMessage = new ErrorMessage(Collections.singletonList(responseStatusException.getMessage()));
        return ResponseEntity.status(responseStatusException.getStatus()).body(errorMessage);
    }
}
