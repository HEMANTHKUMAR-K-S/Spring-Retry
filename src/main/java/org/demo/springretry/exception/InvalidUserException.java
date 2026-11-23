package org.demo.springretry.exception;

public class InvalidUserException extends RuntimeException {

    public InvalidUserException(String msg){
        super(msg);
    }
}
