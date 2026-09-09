package com.inventory.exception;


public class databaseException extends RuntimeException {

    public databaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
