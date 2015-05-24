package com.springapp.validation;

import com.springapp.exceptions.ConstraintViolatedException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.xml.bind.ValidationException;

/**
 * Created by franschl on 06.04.15.
 */
@ControllerAdvice
public class ValidationControllerAdvice {
    final static Logger logger = Logger.getLogger(ValidationControllerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ConstraintViolatedException> handleValidationErrors(MethodArgumentNotValidException exception) {
        ConstraintViolatedException cve = new ConstraintViolatedException();
        for (ObjectError e : exception.getBindingResult().getAllErrors()) {
            cve.addMessage(e.getDefaultMessage());
        }
        logger.error(cve);
        return new ResponseEntity(cve, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public void testError(ValidationException e) {
        e.printStackTrace();
        System.out.println("Noi");
    }
}
