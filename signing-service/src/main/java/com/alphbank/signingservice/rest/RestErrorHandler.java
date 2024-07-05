package com.alphbank.signingservice.rest;

import com.alphbank.signingservice.service.error.SigningSessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SigningSessionNotFoundException.class)
    public void handleSigningSessionNotFoundException(SigningSessionNotFoundException exception){
        System.out.print("not found..");
    }

}
