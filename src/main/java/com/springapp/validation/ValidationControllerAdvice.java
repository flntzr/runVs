package com.springapp.validation;

import com.springapp.exceptions.ConstraintViolatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;

/**
 * Created by franschl on 06.04.15.
 */
@ControllerAdvice
public class ValidationControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ConstraintViolatedException> handleValidationErrors(MethodArgumentNotValidException exception) {
        ConstraintViolatedException cve = new ConstraintViolatedException();
        for (ObjectError e : exception.getBindingResult().getAllErrors()) {
            cve.addMessage(e.getDefaultMessage());
        }
        return new ResponseEntity(cve, HttpStatus.BAD_REQUEST);
    }
}
