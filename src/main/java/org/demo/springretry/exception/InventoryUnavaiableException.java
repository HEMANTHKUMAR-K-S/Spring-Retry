package org.demo.springretry.exception;

public class InventoryUnavaiableException extends RuntimeException{

    public InventoryUnavaiableException(String message){
                            super(message);
    }
}
