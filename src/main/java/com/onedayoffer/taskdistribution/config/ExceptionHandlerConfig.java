package com.onedayoffer.taskdistribution.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerConfig {

    // todo: доработать исключания для конкретных кейсов и соответствующие обработчики
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleException(RuntimeException ex) {
        return new ResponseEntity(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

}
