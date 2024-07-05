package com.alphbank.signingservice.service.error;

public class SigningSessionNotFoundException extends RuntimeException {

    public SigningSessionNotFoundException() {
        super();
    }

    public SigningSessionNotFoundException(String message) {
        super(message);
    }

    public SigningSessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
