package org.demo.springretry.exception;

public class OrderTemporaryException extends RuntimeException {
    public OrderTemporaryException(String msg) {
        super(msg);
    }

}
