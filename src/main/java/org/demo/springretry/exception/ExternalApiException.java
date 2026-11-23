package org.demo.springretry.exception;

public class ExternalApiException extends RuntimeException{

    public ExternalApiException(String message) {
        super(message);
    }

}
